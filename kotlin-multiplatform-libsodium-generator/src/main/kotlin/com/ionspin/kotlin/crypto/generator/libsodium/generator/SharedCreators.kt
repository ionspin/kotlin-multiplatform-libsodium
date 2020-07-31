package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.ClassDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.FunctionDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.KotlinFileDefinition
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
fun createClass(classDefinition: ClassDefinition, multiplatformModifier: MultiplatformModifier, methodCreator : (FunctionDefinition) -> FunSpec) : TypeSpec {
    val commonClassBuilder = TypeSpec.classBuilder(classDefinition.name)
    commonClassBuilder.modifiers += multiplatformModifier.modifierList
    for (innerClassDefinition in classDefinition.innerClasses) {
        val innerClassBuilder = TypeSpec.classBuilder(innerClassDefinition.name)
        innerClassBuilder.modifiers += multiplatformModifier.modifierList
        commonClassBuilder.addType(innerClassBuilder.build())
    }
    for (methodDefinition in classDefinition.methods) {
        commonClassBuilder.addFunction(methodCreator(methodDefinition))
    }
    return commonClassBuilder.build()
}


