package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.FunctionDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.KotlinFileDefinition
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
object JvmLibsodiumGenerator {


    fun createJvmFile(packageName: String, fileDefinition: KotlinFileDefinition) : FileSpec {
        val fileBuilder = FileSpec.builder(packageName, fileDefinition.name)
        for (commonClassDefinition in fileDefinition.commonClassList) {
            val commonClassSpec = createClass(commonClassDefinition, MultiplatformModifier.ACTUAL, ::createJvmFunctionImplementation)
            fileBuilder.addType(commonClassSpec)
        }
        val file = fileBuilder.build()
        file.writeTo(System.out)
        return file
    }

    fun createJvmFunctionImplementation(methodDefinition: FunctionDefinition) : FunSpec {
        val methodBuilder = FunSpec.builder(methodDefinition.name)
        for (paramDefinition in methodDefinition.parameterList) {
            val parameterSpec =
                ParameterSpec.builder(paramDefinition.parameterName, paramDefinition.parameterType.typeName)
            methodBuilder.addParameter(parameterSpec.build())
        }
        methodBuilder.addStatement("val test1 = ${methodDefinition.javaName}")
        methodBuilder.returns(methodDefinition.returnType.typeName)
        return methodBuilder.build()
    }


}
