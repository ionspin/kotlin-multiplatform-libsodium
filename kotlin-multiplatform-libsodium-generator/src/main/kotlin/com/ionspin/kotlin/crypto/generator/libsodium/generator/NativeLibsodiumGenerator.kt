package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeAliasSpec
import com.squareup.kotlinpoet.asTypeName

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
object NativeLibsodiumGenerator {


    fun createNativeFile(packageName: String, fileDefinition: KotlinFileDefinition): FileSpec {
        val fileBuilder = FileSpec.builder(packageName, fileDefinition.name)
        fileBuilder.addImport("kotlinx.cinterop", "toCValues")
        fileBuilder.addImport("kotlinx.cinterop", "convert")
        fileBuilder.addImport("kotlinx.cinterop", "ptr")
        fileBuilder.addImport("kotlinx.cinterop", "pin")
        fileBuilder.addImport("kotlinx.cinterop", "addressOf")
        fileBuilder.addImport("kotlinx.cinterop", "reinterpret")
        fileBuilder.addImport("kotlinx.cinterop", "pointed")
        fileBuilder.addImport("libsodium", "sodium_malloc")

        for (commonClassDefinition in fileDefinition.commonClassList) {
            //Create type-aliases
            commonClassDefinition.innerClasses.forEach {
                fileBuilder.addTypeAlias(createNativeInnerClassSpec(it, MultiplatformModifier.ACTUAL))
            }

            val commonClassSpec = createClass(
                fileBuilder,
                commonClassDefinition,
                MultiplatformModifier.ACTUAL,
                ::createNativeFunctionImplementation
            )
            //Workarounds for native not emitting types
            val byteEmitter = PropertySpec.builder("_emitByte", Byte::class.asTypeName())
            byteEmitter.initializer(CodeBlock.of("0"))
            val byteArrayEmitter = PropertySpec.builder("_emitByteArray", ByteArray::class.asTypeName())
            byteArrayEmitter.initializer(CodeBlock.of("ByteArray(0)"))
            commonClassSpec.addProperty(byteEmitter.build())
            commonClassSpec.addProperty(byteArrayEmitter.build())
            fileBuilder.addType(commonClassSpec.build())
        }
        val file = fileBuilder.build()
        file.writeTo(System.out)
        return file
    }

    fun createNativeInnerClassSpec(
        innerClassDefinition: InnerClassDefinition,
        multiplatformModifier: MultiplatformModifier
    ): TypeAliasSpec {
        val innerClassBuilder =
            TypeAliasSpec.builder(innerClassDefinition.name, ClassName("libsodium", innerClassDefinition.nativeName))
        innerClassBuilder.modifiers += multiplatformModifier.modifierList

        return innerClassBuilder.build()
    }

    fun createNativeFunctionImplementation(methodDefinition: FunctionDefinition): FunSpec.Builder {
        val methodBuilder = FunSpec.builder(methodDefinition.name)
        methodBuilder.modifiers += MultiplatformModifier.ACTUAL.modifierList
        var returnModifierFound = false
        var returnModifierName = ""
        lateinit var actualReturnParameterDefinition: ParameterDefinition
        var actualReturnTypeFound: Boolean = false
        for (paramDefinition in methodDefinition.parameterList) {
            if (paramDefinition.isStateType && methodDefinition.isStateCreationFunction) {
                createStateParam(paramDefinition, methodBuilder)
            }
            if ((paramDefinition.isStateType.not() || methodDefinition.isStateCreationFunction.not()) && paramDefinition.isActuallyAnOutputParam.not()) {
                val parameterSpec =
                    ParameterSpec.builder(paramDefinition.parameterName, paramDefinition.parameterType.typeName)
                methodBuilder.addParameter(parameterSpec.build())
            }
            if (paramDefinition.modifiesReturn) {
                if (returnModifierFound == true) {
                    throw RuntimeException("Return modifier already found")
                }
                returnModifierFound = true
                when (paramDefinition.parameterType) {
                    TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                        returnModifierName = "${paramDefinition.parameterName}.size"
                    }
                    TypeDefinition.INT -> {
                        returnModifierName = paramDefinition.parameterName
                    }
                }

            }
            if (paramDefinition.isActuallyAnOutputParam) {
                actualReturnParameterDefinition = paramDefinition
                actualReturnTypeFound = true
            }
        }
        if (actualReturnTypeFound) {
            if (returnModifierFound) {
                createOutputParam(actualReturnParameterDefinition, returnModifierName, methodBuilder)
            } else {
                if (methodDefinition.outputLengthWhenArray == -1) {
                    throw RuntimeException("Function definition lacks a way to define output array length, function ${methodDefinition.name}")
                }
                createOutputParam(actualReturnParameterDefinition, methodDefinition.outputLengthWhenArray.toString(), methodBuilder)
            }
        }
        methodBuilder.addStatement("println(\"Debug ${methodDefinition.name}\")")
        val constructNativeCall = StringBuilder()
        if (methodDefinition.customCodeBlockReplacesFunctionBody != null) {
            for (codeBlock in methodDefinition.customCodeBlockReplacesFunctionBody.filter { it.applyOnTargets.contains(TargetPlatform.NATIVE) }) {
                constructNativeCall.append(codeBlock.codeBlock)
                methodBuilder.addStatement(constructNativeCall.toString())
            }
        } else {
            pinParams(methodDefinition, methodBuilder)
            if (methodDefinition.isStateCreationFunction) {
                constructNativeCall.append("libsodium.${methodDefinition.nativeName}")
                constructNativeCall.append(paramsToString(methodDefinition))
                methodBuilder.addStatement(constructNativeCall.toString())
                unpinParams(methodDefinition, methodBuilder)
                methodBuilder.addStatement("return state")
            } else if (actualReturnTypeFound) {
                constructNativeCall.append("libsodium.${methodDefinition.nativeName}")
                constructNativeCall.append(paramsToString(methodDefinition))
                methodBuilder.addStatement(constructNativeCall.toString())
                unpinParams(methodDefinition, methodBuilder)
                methodBuilder.addStatement("return ${actualReturnParameterDefinition.parameterName}")
            } else {
                when (methodDefinition.returnType) {
                    TypeDefinition.ARRAY_OF_UBYTES -> {
                        constructNativeCall.append("val result = libsodium.${methodDefinition.nativeName}")
                        constructNativeCall.append(paramsToString(methodDefinition))
                        methodBuilder.addStatement(constructNativeCall.toString())
                        unpinParams(methodDefinition, methodBuilder)
                        methodBuilder.addStatement("return result")
                    }
                    TypeDefinition.INT -> {
                        constructNativeCall.append("val result = libsodium.${methodDefinition.nativeName}")
                        constructNativeCall.append(paramsToString(methodDefinition))
                        methodBuilder.addStatement(constructNativeCall.toString())
                        unpinParams(methodDefinition, methodBuilder)
                        methodBuilder.addStatement("return result")
                    }
                    TypeDefinition.UNIT -> {
                        constructNativeCall.append("libsodium.${methodDefinition.nativeName}")
                        constructNativeCall.append(paramsToString(methodDefinition))
                        methodBuilder.addStatement(constructNativeCall.toString())
                        unpinParams(methodDefinition, methodBuilder)
                    }
                    is CustomTypeDefinition -> {
                        constructNativeCall.append("val result = libsodium.${methodDefinition.nativeName}")
                        constructNativeCall.append(paramsToString(methodDefinition))
                        methodBuilder.addStatement(constructNativeCall.toString())
                        unpinParams(methodDefinition, methodBuilder)
                        methodBuilder.addStatement("return result")
                    }
                }


            }
        }

        methodBuilder.returns(methodDefinition.returnType.typeName)

        return methodBuilder
    }

    fun createStateParam(stateParameterDefinition: ParameterDefinition, methodBuilder: FunSpec.Builder) {
        /*
        val allocated = sodium_malloc(crypto_hash_sha256_state.size.convert())!!
        state = allocated.reinterpret<crypto_hash_sha256_state>().pointed
         */
        methodBuilder.addStatement("val allocated = sodium_malloc(${stateParameterDefinition.parameterType.typeName}.size.convert())!!")
        methodBuilder.addStatement("val state = allocated.reinterpret<${stateParameterDefinition.parameterType.typeName}>().pointed")
    }

    fun createOutputParam(outputParam: ParameterDefinition, length: String?, methodBuilder: FunSpec.Builder) {
        /*
        val hashResult = UByteArray(Sha256Properties.MAX_HASH_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_hash_sha256_final(state.ptr, hashResultPinned.addressOf(0))
        sodium_free(state.ptr)
        return hashResult
         */
        when (outputParam.parameterType) {
            TypeDefinition.ARRAY_OF_UBYTES, TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE, TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                methodBuilder.addStatement("val ${outputParam.parameterName} = UByteArray($length)")
            }
            else -> {
                throw RuntimeException("Unhandled native output param type: ${outputParam.parameterType.typeName}")
            }


        }
    }

    fun pinParams(methodDefinition: FunctionDefinition, methodBuilder: FunSpec.Builder) {
        methodDefinition.parameterList.forEachIndexed { index, paramDefinition ->
            if (paramDefinition.parameterType is TypeDefinition) {
                when (paramDefinition.parameterType) {
                    TypeDefinition.ARRAY_OF_UBYTES -> {
                        methodBuilder.addStatement("val pinned${paramDefinition.parameterName.capitalize()} = ${paramDefinition.parameterName}.pin()")
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                        methodBuilder.addStatement("val pinned${paramDefinition.parameterName.capitalize()} = ${paramDefinition.parameterName}.pin()")
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE -> {
                        methodBuilder.addStatement("val pinned${paramDefinition.parameterName.capitalize()} = ${paramDefinition.parameterName}.pin()")
                    }
                    TypeDefinition.LONG -> {

                    }
                    TypeDefinition.INT -> {

                    }
                    TypeDefinition.STRING -> {

                    }
                }
            }

        }
    }

    fun unpinParams(methodDefinition: FunctionDefinition, methodBuilder: FunSpec.Builder) {
        methodDefinition.parameterList.forEachIndexed { index, paramDefinition ->
            if (paramDefinition.parameterType is TypeDefinition) {
                when (paramDefinition.parameterType) {
                    TypeDefinition.ARRAY_OF_UBYTES -> {
                        methodBuilder.addStatement("pinned${paramDefinition.parameterName.capitalize()}.unpin()")
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                        methodBuilder.addStatement("pinned${paramDefinition.parameterName.capitalize()}.unpin()")
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE -> {
                        methodBuilder.addStatement("pinned${paramDefinition.parameterName.capitalize()}.unpin()")
                    }
                    TypeDefinition.LONG -> {

                    }
                    TypeDefinition.INT -> {

                    }
                    TypeDefinition.STRING -> {

                    }
                }
            }

        }
    }

    fun paramsToString(methodDefinition: FunctionDefinition): String {
        val paramsBuilder = StringBuilder()
        paramsBuilder.append("(")
        methodDefinition.parameterList.forEachIndexed { index, paramDefinition ->
            val separator = if (index == methodDefinition.parameterList.size - 1) {
                ""
            } else {
                ", "
            }
            if (paramDefinition.parameterType is CustomTypeDefinition) {
                paramsBuilder.append(paramDefinition.parameterName + ".ptr" + separator)
            }
            if (paramDefinition.parameterType is TypeDefinition) {
                when (paramDefinition.parameterType) {
                    TypeDefinition.ARRAY_OF_UBYTES -> {
                        paramsBuilder.append("pinned" + paramDefinition.parameterName.capitalize() + ".addressOf(0), " + paramDefinition.parameterName + ".size.convert()" + separator)
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                        paramsBuilder.append("pinned" + paramDefinition.parameterName.capitalize() + ".addressOf(0), " + paramDefinition.parameterName + ".size.convert()" + separator)
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE -> {
                        paramsBuilder.append("pinned" + paramDefinition.parameterName.capitalize() + ".addressOf(0)" + separator)
                    }
                    TypeDefinition.LONG -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".convert()" + separator)
                    }
                    TypeDefinition.INT -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".convert()" + separator)
                    }
                    TypeDefinition.STRING -> {
                        paramsBuilder.append(paramDefinition.parameterName + separator)
                    }
                    TypeDefinition.UBYTE -> {
                        paramsBuilder.append(paramDefinition.parameterName + separator)
                    }
                }
            }

        }
        paramsBuilder.append(')')
        return paramsBuilder.toString()
    }


}
