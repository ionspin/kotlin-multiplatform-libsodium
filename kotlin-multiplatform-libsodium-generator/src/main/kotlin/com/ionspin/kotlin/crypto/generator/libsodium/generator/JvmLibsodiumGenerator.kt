package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.CustomTypeDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.FunctionDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.InnerClassDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.KotlinFileDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.ParameterDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.TypeDefinition
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeAliasSpec

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
object JvmLibsodiumGenerator {


    fun createJvmFile(packageName: String, fileDefinition: KotlinFileDefinition): FileSpec {
        val fileBuilder = FileSpec.builder(packageName, fileDefinition.name)
        val sodiumProperty = PropertySpec.builder("sodium", ClassName.bestGuess("com.goterl.lazycode.lazysodium.SodiumJava"))
        sodiumProperty.initializer(CodeBlock.of("SodiumJava()"))
        fileBuilder.addProperty(sodiumProperty.build())
        for (commonClassDefinition in fileDefinition.commonClassList) {
            //Create type-aliases
            commonClassDefinition.innerClasses.forEach {
                fileBuilder.addTypeAlias(createJvmInnerClassSpec(it, MultiplatformModifier.ACTUAL))
            }

            val commonClassSpec = createClass(
                commonClassDefinition,
                MultiplatformModifier.ACTUAL,
                ::createJvmFunctionImplementation
            )
            fileBuilder.addType(commonClassSpec.build())
        }
        val file = fileBuilder.build()
        file.writeTo(System.out)
        return file
    }

    fun createJvmInnerClassSpec(
        innerClassDefinition: InnerClassDefinition,
        multiplatformModifier: MultiplatformModifier
    ): TypeAliasSpec {
        val innerClassBuilder = TypeAliasSpec.builder(innerClassDefinition.name, ClassName.bestGuess(innerClassDefinition.javaName))
        innerClassBuilder.modifiers += multiplatformModifier.modifierList

        return innerClassBuilder.build()
    }

    fun createJvmFunctionImplementation(methodDefinition: FunctionDefinition): FunSpec.Builder {
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
                returnModifierName = paramDefinition.parameterName
            }
            if (paramDefinition.isActuallyAnOutputParam) {
                actualReturnParameterDefinition = paramDefinition
                actualReturnTypeFound = true
            }
        }
        if (actualReturnTypeFound) {
            if (returnModifierFound) {
                createOutputParam(
                    actualReturnParameterDefinition,
                    returnModifierName,
                    methodBuilder
                )
            } else {
                if (methodDefinition.outputLengthWhenArray == -1) {
                    throw RuntimeException("Function definition lacks a way to define output array length, function ${methodDefinition.name}")
                }
                createOutputParam(
                    actualReturnParameterDefinition,
                    methodDefinition.outputLengthWhenArray.toString(),
                    methodBuilder
                )
            }
        }
        methodBuilder.addStatement("println(\"Debug ${methodDefinition.name}\")")
        val constructJvmCall = StringBuilder()
        if (methodDefinition.isStateCreationFunction) {
            constructJvmCall.append("sodium.${methodDefinition.nativeName}")
            constructJvmCall.append(paramsToString(methodDefinition))
            methodBuilder.addStatement(constructJvmCall.toString())
            methodBuilder.addStatement("return state")
        } else if (actualReturnTypeFound) {
            constructJvmCall.append("sodium.${methodDefinition.nativeName}")
            constructJvmCall.append(paramsToString(methodDefinition))
            methodBuilder.addStatement(constructJvmCall.toString())
            methodBuilder.addStatement("return out")
        } else {
            when (methodDefinition.returnType) {
                TypeDefinition.ARRAY_OF_UBYTES -> {
                    constructJvmCall.append("val result = sodium.${methodDefinition.nativeName}")
                    constructJvmCall.append(paramsToString(methodDefinition))
                    methodBuilder.addStatement(constructJvmCall.toString())
                    methodBuilder.addStatement("return result")
                }
                TypeDefinition.INT -> {
                    constructJvmCall.append("val result = sodium.${methodDefinition.nativeName}")
                    constructJvmCall.append(paramsToString(methodDefinition))
                    methodBuilder.addStatement(constructJvmCall.toString())
                    methodBuilder.addStatement("return result")
                }
                TypeDefinition.UNIT -> {
                    constructJvmCall.append("sodium.${methodDefinition.nativeName}")
                    constructJvmCall.append(paramsToString(methodDefinition))
                    methodBuilder.addStatement(constructJvmCall.toString())
                }
                is CustomTypeDefinition -> {
                    constructJvmCall.append("val result = sodium.${methodDefinition.nativeName}")
                    constructJvmCall.append(paramsToString(methodDefinition))
                    methodBuilder.addStatement(constructJvmCall.toString())
                    methodBuilder.addStatement("return result")
                }
            }
        }
        methodBuilder.returns(methodDefinition.returnType.typeName)
        return methodBuilder
    }

    fun createOutputParam(outputParam: ParameterDefinition, length: String?, methodBuilder: FunSpec.Builder) {
        /*
        val hashed = ByteArray(Sha256Properties.MAX_HASH_BYTES)
        sodium.crypto_hash_sha256_final(state, hashed)
        return hashed.asUByteArray()
         */
        when (outputParam.parameterType) {
            TypeDefinition.ARRAY_OF_UBYTES, TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE, TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                methodBuilder.addStatement("val out = UByteArray($length)")
            }
            else -> {
                throw RuntimeException("Unhandled native output param type: ${outputParam.parameterType.typeName}")
            }


        }
    }

    fun createStateParam(stateParameterDefinition: ParameterDefinition, methodBuilder: FunSpec.Builder) {
        /*
        val state = Hash.State256()
         */
        val specificInitializer = stateParameterDefinition.specificJvmInitializer ?: ""
        methodBuilder.addStatement("val state = ${stateParameterDefinition.parameterType.typeName}($specificInitializer)")
    }

    fun paramsToString(methodDefinition: FunctionDefinition) : String {
        val paramsBuilder = StringBuilder()
        paramsBuilder.append("(")
        methodDefinition.parameterList.forEachIndexed { index, paramDefinition ->
            val separator = if (index == methodDefinition.parameterList.size - 1) {
                ""
            } else {
                ", "
            }
            if (paramDefinition.parameterType is CustomTypeDefinition) {
                paramsBuilder.append(paramDefinition.parameterName + separator)
            }
            if (paramDefinition.parameterType is TypeDefinition) {
                when(paramDefinition.parameterType) {
                    TypeDefinition.ARRAY_OF_UBYTES -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".asByteArray(), " + paramDefinition.parameterName + ".size" + separator)
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".asByteArray(), " + paramDefinition.parameterName + ".size.toLong()" + separator)
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".asByteArray()" + separator)
                    }
                    TypeDefinition.LONG -> {
                        paramsBuilder.append(paramDefinition.parameterName + separator)
                    }
                    TypeDefinition.INT -> {
                        paramsBuilder.append(paramDefinition.parameterName + separator)
                    }
                    TypeDefinition.STRING -> {
                        paramsBuilder.append(paramDefinition.parameterName + separator)
                    }
                }
            }

        }
        paramsBuilder.append(')')
        return paramsBuilder.toString()
    }



}
