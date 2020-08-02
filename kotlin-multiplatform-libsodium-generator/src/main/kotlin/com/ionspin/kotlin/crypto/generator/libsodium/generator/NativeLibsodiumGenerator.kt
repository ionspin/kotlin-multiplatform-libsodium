package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.*
import com.squareup.kotlinpoet.*

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
        for (commonClassDefinition in fileDefinition.commonClassList) {
            //Create type-aliases
            commonClassDefinition.innerClasses.forEach {
                fileBuilder.addTypeAlias(createNativeInnerClassSpec(it, MultiplatformModifier.ACTUAL))
            }

            val commonClassSpec = createClass(
                commonClassDefinition,
                MultiplatformModifier.ACTUAL,
                ::createNativeFunctionImplementation
            )
            fileBuilder.addType(commonClassSpec)
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

    fun createNativeFunctionImplementation(methodDefinition: FunctionDefinition): FunSpec {
        val methodBuilder = FunSpec.builder(methodDefinition.name)
        methodBuilder.modifiers += MultiplatformModifier.ACTUAL.modifierList
        var returnModifierFound = false
        var returnModifierName = ""
        for (paramDefinition in methodDefinition.parameterList) {
            val parameterSpec =
                ParameterSpec.builder(paramDefinition.parameterName, paramDefinition.parameterType.typeName)
            methodBuilder.addParameter(parameterSpec.build())
            if (paramDefinition.modifiesReturn) {
                if (returnModifierFound == true) {
                    throw RuntimeException("Return modifier already found")
                }
                returnModifierFound = true
                returnModifierName = paramDefinition.parameterName
            }
        }

        pinParams(methodDefinition, methodBuilder)

        if (methodDefinition.returnType == TypeDefinition.ARRAY_OF_UBYTES) {
            methodBuilder.addStatement("println(\"Debug\")")
            val constructJvmCall = StringBuilder()
            constructJvmCall.append("return libsodium.${methodDefinition.nativeName}")
            constructJvmCall.append(paramsToString(methodDefinition))

            methodBuilder.addStatement(constructJvmCall.toString())
        }

        if (methodDefinition.returnType == TypeDefinition.INT) {
            methodBuilder.addStatement("println(\"Debug\")")
            val constructJvmCall = StringBuilder()
            constructJvmCall.append("return libsodium.${methodDefinition.nativeName}")
            constructJvmCall.append(paramsToString(methodDefinition))

            methodBuilder.addStatement(constructJvmCall.toString())
        }

        if (methodDefinition.returnType == TypeDefinition.UNIT) {

            val constructNativeCall = StringBuilder()
            constructNativeCall.append("libsodium.${methodDefinition.nativeName}")
            constructNativeCall.append(paramsToString(methodDefinition))

            methodBuilder.addStatement(constructNativeCall.toString())
        }

        if (methodDefinition.returnType is CustomTypeDefinition) {
            methodBuilder.addStatement("println(\"Debug\")")
            val constructJvmCall = StringBuilder()
            constructJvmCall.append("return libsodium.${methodDefinition.nativeName}")
            constructJvmCall.append(paramsToString(methodDefinition))

            methodBuilder.addStatement(constructJvmCall.toString())
        }

        unpinParams(methodDefinition, methodBuilder)

        methodBuilder.returns(methodDefinition.returnType.typeName)
        return methodBuilder.build()
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
                        paramsBuilder.append("pinned" + paramDefinition.parameterName.capitalize() + ".addressOf(0), "+ paramDefinition.parameterName + ".size.convert()" + separator)
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                        paramsBuilder.append("pinned" + paramDefinition.parameterName.capitalize() + ".addressOf(0), "+  paramDefinition.parameterName + ".size.convert()" + separator)
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
                }
            }

        }
        paramsBuilder.append(')')
        return paramsBuilder.toString()
    }


}
