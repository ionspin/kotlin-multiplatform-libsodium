package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.FunctionDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.InnerClassDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.KotlinFileDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.TypeDefinition
import com.squareup.kotlinpoet.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */

enum class MultiplatformModifier(val modifierList: List<KModifier>) {
    EXPECT(listOf(KModifier.EXPECT)),
    ACTUAL(listOf(KModifier.ACTUAL)),
    NONE(listOf())
}

object CommonLibsodiumGenerator {

    fun createCommonFile(packageName: String, fileDefinition: KotlinFileDefinition): FileSpec {
        val fileBuilder = FileSpec.builder(packageName, fileDefinition.name)
        for (commonClassDefinition in fileDefinition.commonClassList) {
            //Create expected inner classes that will be represented by type-aliases
            commonClassDefinition.innerClasses.forEach {
                fileBuilder.addType(createCommonInnerClassSpec(it, MultiplatformModifier.EXPECT))
            }
            val commonClassSpec =
                createClass(
                    fileBuilder,
                    commonClassDefinition,
                    MultiplatformModifier.EXPECT,
                    ::createCommonMethodSpec
                )
            fileBuilder.addType(commonClassSpec.build())
        }
        val file = fileBuilder.build()
        file.writeTo(System.out)
        return file
    }

    fun createCommonInnerClassSpec(
        innerClassDefinition: InnerClassDefinition,
        multiplatformModifier: MultiplatformModifier
    ): TypeSpec {
        val innerClassBuilder = TypeSpec.classBuilder(innerClassDefinition.name)
        innerClassBuilder.modifiers += multiplatformModifier.modifierList

        return innerClassBuilder.build()
    }

    fun createCommonMethodSpec(methodDefinition: FunctionDefinition): FunSpec.Builder {
        val methodBuilder = FunSpec.builder(methodDefinition.name)
        val actualReturnTypes = mutableListOf<TypeName>()
        var actualReturnTypeFound : Boolean = false
        for (paramDefinition in methodDefinition.parameterList) {
            if ((paramDefinition.isStateType.not() || methodDefinition.isStateCreationFunction.not()) && paramDefinition.dropParameterFromDefinition.not()) {
                if (paramDefinition.parameterType != TypeDefinition.NULL) {
                    val parameterSpec =
                        ParameterSpec.builder(paramDefinition.parameterName, paramDefinition.parameterType.typeName)
                    methodBuilder.addParameter(parameterSpec.build())
                }
            }
            if (paramDefinition.isActuallyAnOutputParam) {
                actualReturnTypeFound = true
                actualReturnTypes += paramDefinition.parameterType.typeName
            }
        }
        if (actualReturnTypeFound && actualReturnTypes.size == 1) {
            methodBuilder.returns(actualReturnTypes[0])
        } else {
            methodBuilder.returns(methodDefinition.returnType.typeName)
        }
        return methodBuilder
    }

}














