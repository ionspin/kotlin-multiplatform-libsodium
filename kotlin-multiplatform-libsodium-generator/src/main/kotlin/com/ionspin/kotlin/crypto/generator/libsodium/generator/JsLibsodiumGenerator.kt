package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.*
import com.squareup.kotlinpoet.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
object JsLibsodiumGenerator {


    fun createJsFile(packageName: String, fileDefinition: KotlinFileDefinition): FileSpec {
        val fileBuilder = FileSpec.builder(packageName, fileDefinition.name)
        fileBuilder.addImport("ext.libsodium.com.ionspin.kotlin.crypto", "toUInt8Array")
        fileBuilder.addImport("ext.libsodium.com.ionspin.kotlin.crypto", "toUByteArray")
        fileBuilder.addImport("com.ionspin.kotlin.crypto", "getSodium")
        for (commonClassDefinition in fileDefinition.commonClassList) {
            //Create type-aliases
            commonClassDefinition.innerClasses.forEach {
                fileBuilder.addTypeAlias(createJsInnerClassSpec(it, MultiplatformModifier.ACTUAL))
            }

            val commonClassSpec = createClass(
                commonClassDefinition,
                MultiplatformModifier.ACTUAL,
                ::createJsFunctionImplementation
            )
            fileBuilder.addType(commonClassSpec.build())
        }
        val file = fileBuilder.build()
        file.writeTo(System.out)
        return file
    }

    fun createJsInnerClassSpec(
        innerClassDefinition: InnerClassDefinition,
        multiplatformModifier: MultiplatformModifier
    ): TypeAliasSpec {
        val innerClassBuilder = TypeAliasSpec.builder(innerClassDefinition.name, Any::class.asTypeName())
        innerClassBuilder.modifiers += multiplatformModifier.modifierList

        return innerClassBuilder.build()
    }

    fun createJsFunctionImplementation(methodDefinition: FunctionDefinition): FunSpec {
        val methodBuilder = FunSpec.builder(methodDefinition.name)
        methodBuilder.modifiers += MultiplatformModifier.ACTUAL.modifierList
        var returnModifierFound = false
        var returnModifierName = ""
        var actualReturnType: TypeName = DYNAMIC
        var actualReturnTypeFound: Boolean = false
        for (paramDefinition in methodDefinition.parameterList) {
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
                actualReturnTypeFound = true
                actualReturnType = paramDefinition.parameterType.typeName
            }
        }
        methodBuilder.addStatement("println(\"Debug\")")
        val constructJsCall = StringBuilder()
        when (methodDefinition.returnType) {
            TypeDefinition.ARRAY_OF_UBYTES -> {
                constructJsCall.append("return getSodium().${methodDefinition.javaName}")
                constructJsCall.append(paramsToString(methodDefinition) + ".toUByteArray()")
            }
            TypeDefinition.INT -> {
                constructJsCall.append("return getSodium().${methodDefinition.javaName}")
                constructJsCall.append(paramsToString(methodDefinition))
            }
            TypeDefinition.UNIT -> {
                constructJsCall.append("getSodium().${methodDefinition.javaName}")
                constructJsCall.append(paramsToString(methodDefinition))
            }
            is CustomTypeDefinition -> {
                constructJsCall.append("return getSodium().${methodDefinition.javaName}")
                constructJsCall.append(paramsToString(methodDefinition))
            }
        }
        methodBuilder.addStatement(constructJsCall.toString())
        if (actualReturnTypeFound) {
            methodBuilder.returns(actualReturnType)
            return methodBuilder.build()
        }

        if (methodDefinition.dynamicJsReturn) {
            methodBuilder.returns(Dynamic)
        } else {
            methodBuilder.returns(methodDefinition.returnType.typeName)
        }
        return methodBuilder.build()
    }

    fun paramsToString(methodDefinition: FunctionDefinition): String {
        val paramsBuilder = StringBuilder()
        paramsBuilder.append("(")
        val jsParams = methodDefinition.parameterList.filter { it.dropParameterFromDefinition.not() }
        jsParams.forEachIndexed { index, paramDefinition ->
            val separator = if (index == jsParams.size - 1) {
                ""
            } else {
                ", "
            }
            if (paramDefinition.parameterType is CustomTypeDefinition) {
                paramsBuilder.append(paramDefinition.parameterName + separator)
            }
            if (paramDefinition.parameterType is TypeDefinition) {
                when (paramDefinition.parameterType) {
                    TypeDefinition.ARRAY_OF_UBYTES -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".toUInt8Array()" + separator)
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".toUInt8Array(), " + separator)
                    }
                    TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE -> {
                        paramsBuilder.append(paramDefinition.parameterName + ".toUInt8Array()" + separator)
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
