package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.*
import com.squareup.kotlinpoet.*

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

    fun createJvmFunctionImplementation(methodDefinition: FunctionDefinition): FunSpec {
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

        methodBuilder.addStatement("println(\"Debug\")")
        val constructJvmCall = StringBuilder()
        when (methodDefinition.returnType) {
            TypeDefinition.ARRAY_OF_UBYTES -> {
                constructJvmCall.append("return sodium.${methodDefinition.nativeName}")
                constructJvmCall.append(paramsToString(methodDefinition))
            }
            TypeDefinition.INT -> {
                constructJvmCall.append("return sodium.${methodDefinition.nativeName}")
                constructJvmCall.append(paramsToString(methodDefinition))
            }
            TypeDefinition.UNIT -> {
                constructJvmCall.append("sodium.${methodDefinition.nativeName}")
                constructJvmCall.append(paramsToString(methodDefinition))
            }
            is CustomTypeDefinition -> {
                constructJvmCall.append("return sodium.${methodDefinition.nativeName}")
                constructJvmCall.append(paramsToString(methodDefinition))
            }
        }
        methodBuilder.addStatement(constructJvmCall.toString())
        methodBuilder.returns(methodDefinition.returnType.typeName)
        return methodBuilder.build()
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
