package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.*
import com.squareup.kotlinpoet.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
object JsLibsodiumGenerator {

    val jsInterfaceFunctionDefinitions : MutableList<FunSpec> = mutableListOf()


    fun createJsFile(packageName: String, fileDefinition: KotlinFileDefinition): FileSpec {
        val fileBuilder = FileSpec.builder(packageName, fileDefinition.name)
        fileBuilder.addImport("ext.libsodium.com.ionspin.kotlin.crypto", "toUInt8Array")
        fileBuilder.addImport("ext.libsodium.com.ionspin.kotlin.crypto", "toUByteArray")
        fileBuilder.addImport("com.ionspin.kotlin.crypto", "getSodium")
        fileBuilder.addImport("org.khronos.webgl", "Uint8Array")
        for (commonClassDefinition in fileDefinition.commonClassList) {
            //Create type-aliases
            commonClassDefinition.innerClasses.forEach {
                fileBuilder.addTypeAlias(createJsInnerClassSpec(it, MultiplatformModifier.ACTUAL))
            }

            val commonClassSpec = createClass(
                fileBuilder,
                commonClassDefinition,
                MultiplatformModifier.ACTUAL,
                ::createJsFunctionImplementation
            )
            fileBuilder.addType(commonClassSpec.build())
        }
        createJsInterfaceFile()
        val file = fileBuilder.build()
        file.writeTo(System.out)
        return file
    }

    // This helps with static typing in js target
    fun createJsInterfaceFile() {
        val fileBuilder = FileSpec.builder(packageName, "JsSodiumInterfaceDebug")
        val jsInterface = TypeSpec.interfaceBuilder("JsSodiumInterfaceDebug")
        jsInterface.addFunctions(jsInterfaceFunctionDefinitions)
        fileBuilder.addType(jsInterface.build())
        val file = fileBuilder.build()
        file.writeTo(System.out)
    }

    fun createJsInnerClassSpec(
        innerClassDefinition: InnerClassDefinition,
        multiplatformModifier: MultiplatformModifier
    ): TypeAliasSpec {
        val innerClassBuilder = TypeAliasSpec.builder(innerClassDefinition.name, Any::class.asTypeName())
        innerClassBuilder.modifiers += multiplatformModifier.modifierList

        return innerClassBuilder.build()
    }

    fun createJsFunctionImplementation(methodDefinition: FunctionDefinition): FunSpec.Builder {
        val methodBuilder = FunSpec.builder(methodDefinition.name)

        var returnModifierFound = false
        var returnModifierValue = ""
        var actualReturnType: TypeName = DYNAMIC
        var actualReturnTypeFound: Boolean = false
        for (paramDefinition in methodDefinition.parameterList) {
            if ((paramDefinition.isStateType.not() || methodDefinition.isStateCreationFunction.not()) && paramDefinition.isActuallyAnOutputParam.not()) {
                if (paramDefinition.parameterType != TypeDefinition.NULL) {
                    val parameterSpec =
                        ParameterSpec.builder(paramDefinition.parameterName, paramDefinition.parameterType.typeName)
                    methodBuilder.addParameter(parameterSpec.build())
                }
            }
            if (paramDefinition.modifiesReturnObjectSize) {
                if (returnModifierFound == true) {
                    throw RuntimeException("Return modifier already found")
                }
                returnModifierFound = true
                if (paramDefinition.specificReturnModification == null) {
                    when (paramDefinition.parameterType) {
                        TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE -> {
                            returnModifierValue = "${paramDefinition.parameterName}.size"
                        }
                        TypeDefinition.INT -> {
                            returnModifierValue = paramDefinition.parameterName
                        }
                    }
                } else {
                    returnModifierValue = paramDefinition.specificReturnModification!!
                }
            }
            if (paramDefinition.isActuallyAnOutputParam) {
                actualReturnTypeFound = true
                actualReturnType = paramDefinition.parameterType.typeName
            }
        }
        if (actualReturnTypeFound) {
            methodBuilder.returns(actualReturnType)
        } else if (methodDefinition.dynamicJsReturn) {
            methodBuilder.returns(Dynamic)
        } else {
            methodBuilder.returns(methodDefinition.returnType.typeName)
        }
        //Create a spec for interface
        methodBuilder.addModifiers(KModifier.ABSTRACT)
        jsInterfaceFunctionDefinitions.add(methodBuilder.build())
        //continue with normal func spec for implementation
        methodBuilder.modifiers.clear()
        methodBuilder.modifiers += MultiplatformModifier.ACTUAL.modifierList
        methodBuilder.addStatement("println(\"Debug ${methodDefinition.name}\")")
        val constructJsCall = StringBuilder()
        if (methodDefinition.customCodeBlockReplacesFunctionBody != null) {
            for (codeBlock in methodDefinition.customCodeBlockReplacesFunctionBody.filter { it.applyOnTargets.contains(TargetPlatform.JS) }) {
                constructJsCall.append(codeBlock.codeBlock)
            }
        } else {
            when (methodDefinition.returnType) {
                TypeDefinition.ARRAY_OF_UBYTES -> {
                    constructJsCall.append("return getSodium().${methodDefinition.jsName}")
                    constructJsCall.append(paramsToString(methodDefinition) + ".toUByteArray()")
                }
                TypeDefinition.INT -> {
                    constructJsCall.append("return getSodium().${methodDefinition.jsName}")
                    constructJsCall.append(paramsToString(methodDefinition))
                }
                TypeDefinition.UNIT -> {
                    constructJsCall.append("getSodium().${methodDefinition.jsName}")
                    constructJsCall.append(paramsToString(methodDefinition))
                }
                is CustomTypeDefinition -> {
                    if (methodDefinition.parameterList.filter { it.isStateType.not() }.size > 0) {
                        constructJsCall.append("return getSodium().${methodDefinition.jsName}")
                        constructJsCall.append(paramsToString(methodDefinition))
                    } else {
                        constructJsCall.append("val result  = js(\"getSodium().${methodDefinition.jsName}()\")")
                        constructJsCall.append("\nreturn result")
                    }
                }
            }
        }
        methodBuilder.addStatement(constructJsCall.toString())
        return methodBuilder
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
                        paramsBuilder.append(paramDefinition.parameterName + ".toUInt8Array()" + separator)
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
                    TypeDefinition.UBYTE -> {
                        paramsBuilder.append(paramDefinition.parameterName + separator)
                    }
                    TypeDefinition.NULL -> {
                        println("Got null parameter in js")
//                        paramsBuilder.append("null" + separator)
                    }
                }
            }


        }
        paramsBuilder.append(')')
        return paramsBuilder.toString()
    }


}
