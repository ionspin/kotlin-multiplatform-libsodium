package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.ClassDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.DataClassDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.FunctionDefinition
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.InnerClassDefinition
import com.squareup.kotlinpoet.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
fun createClass(
    fileBuilder: FileSpec.Builder,
    classDefinition: ClassDefinition,
    multiplatformModifier: MultiplatformModifier,
    methodCreator: (FunctionDefinition) -> FunSpec.Builder
): TypeSpec.Builder {
    val commonClassBuilder = TypeSpec.classBuilder(classDefinition.name)
    // Ugly
    val primaryConstructor = FunSpec.constructorBuilder()
    if (multiplatformModifier == MultiplatformModifier.EXPECT) {
        primaryConstructor.addModifiers(KModifier.INTERNAL)
        for (dataClassDefinition in classDefinition.dataClasses) {
            generateDataClass(fileBuilder, dataClassDefinition)
        }
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

fun generateDataClass(fileBuilder: FileSpec.Builder, dataClassDefinition: DataClassDefinition) {
    val dataClassBuilder = TypeSpec.classBuilder(dataClassDefinition.name)
    dataClassBuilder.addModifiers(KModifier.DATA)
    val dataClassConstructor = FunSpec.constructorBuilder()
    for (parameter in dataClassDefinition.parameters) {
        val parameterBuilder = ParameterSpec.builder(parameter.parameterName, parameter.parameterType.typeName)
        val annotationBuilder =
            AnnotationSpec.builder(ClassName("kotlin.js", "JsName"))
                .addMember("\"${parameter.parameterName}\"")
        parameterBuilder.addAnnotation(annotationBuilder.build())
        dataClassConstructor.addParameter(parameterBuilder.build())
    }
    dataClassBuilder.primaryConstructor(dataClassConstructor.build())
    for (parameter in dataClassDefinition.parameters) {
        dataClassBuilder.addProperty(
            PropertySpec.builder(parameter.parameterName, parameter.parameterType.typeName)
                .initializer(parameter.parameterName)
                .build()
        )
    }
    fileBuilder.addType(dataClassBuilder.build())
}

fun generateDocumentationForMethod(builder: FunSpec.Builder, methodSpec: FunctionDefinition) {
    builder.addKdoc(methodSpec.codeDocumentation)
}


