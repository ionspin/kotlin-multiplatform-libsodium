package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.ClassDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.FunctionDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.InnerClassDefinition
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
fun createClass(
    classDefinition: ClassDefinition,
    multiplatformModifier: MultiplatformModifier,
    methodCreator: (FunctionDefinition) -> FunSpec.Builder
): TypeSpec.Builder {
    val commonClassBuilder = TypeSpec.classBuilder(classDefinition.name)
    // Ugly
    val primaryConstructor = FunSpec.constructorBuilder()
    if (multiplatformModifier == MultiplatformModifier.EXPECT) {
        primaryConstructor.addModifiers(KModifier.INTERNAL)
    } else {
        primaryConstructor.addModifiers(KModifier.INTERNAL, KModifier.ACTUAL)
    }

    commonClassBuilder.primaryConstructor(primaryConstructor.build())
    commonClassBuilder.modifiers += multiplatformModifier.modifierList
    for (methodDefinition in classDefinition.methods) {
        val builder = methodCreator(methodDefinition)
        generateDocumentationForMethod(builder, methodDefinition)
        commonClassBuilder.addFunction(builder.build())
    }
    return commonClassBuilder
}

fun generateDocumentationForMethod(builder: FunSpec.Builder, methodSpec: FunctionDefinition) {
    builder.addKdoc(methodSpec.codeDocumentation)
}


