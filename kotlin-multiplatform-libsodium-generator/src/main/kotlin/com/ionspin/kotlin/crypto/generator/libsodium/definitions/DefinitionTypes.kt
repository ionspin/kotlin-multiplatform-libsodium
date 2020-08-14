package com.ionspin.kotlin.crypto.generator.libsodium.definitions

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 28-Jul-2020
 */

val packageName = "debug.test"

fun withPackageName(name: String) = ClassName(packageName, name)

class KotlinFileDefinition(
    val name: String,
    val commonClassList: MutableList<ClassDefinition> = mutableListOf()
) {
    operator fun ClassDefinition.unaryPlus() {
        commonClassList.add(this)
    }
}

class ClassDefinition(
    val name: String,
    val codeDocumentation: String = "",
    val innerClasses: MutableList<InnerClassDefinition> = mutableListOf(),
    val methods: MutableList<FunctionDefinition> = mutableListOf(),
    val dataClasses: MutableList<DataClassDefinition> = mutableListOf()
) {
    operator fun InnerClassDefinition.unaryPlus() {
        innerClasses.add(this)
    }

    operator fun FunctionDefinition.unaryPlus() {
        methods.add(this)
    }

    operator fun DataClassDefinition.unaryPlus() {
        dataClasses.add(this)
    }

    operator fun List<FunctionDefinition>.unaryPlus() {
        methods.addAll(this)
    }
}

class InnerClassDefinition(
    val name: String,
    val javaName: String,
    val jsName: String,
    val nativeName: String,
    val codeDocumentation: String = "",
    val functions: MutableList<FunctionDefinition> = mutableListOf()
)

class DataClassDefinition(
    val name: String,
    val codeDocumentation: String = "",
    val parameters : List<ParameterDefinition>
)

/**
 * outputLengthWhenArray - if output is an a array and there is no parameter that modifies output length we need
 * to tell the function what to expect (think SHA256 32byte output always, and Blake2 dynamic output controlled
 * by outputLen parameter)
 */
class FunctionDefinition(
    val name: String,
    val javaName: String,
    val jsName: String,
    val nativeName: String,
    val codeDocumentation: String = "",
    val parameterList: MutableList<ParameterDefinition> = mutableListOf(),
    val returnType: GeneralTypeDefinition,
    val dynamicJsReturn: Boolean = false,
    val isStateCreationFunction: Boolean = false,
    val outputLengthWhenArray: Int = -1,
    val customCodeBlockReplacesFunctionBody: List<CodeBlockDefinition>? = null
) {
    operator fun ParameterDefinition.unaryPlus() {
        parameterList.add(this)
    }
}

/**
 *
 * isActuallyAnOutputParam - drop this parameter from the generated param list and provide it as output. Param
 * will be automatically generated inside function body block
 * isStateType - provides special handling when type is a non-primitive state type
 * dropParameterFromDefinition - don't show this parameter in method definition
 */
class ParameterDefinition(
    val parameterName: String,
    val parameterType: GeneralTypeDefinition,
    val modifiesReturn: Boolean = false,
    val isActuallyAnOutputParam: Boolean = false,
    val isStateType: Boolean = false,
    val dropParameterFromDefinition: Boolean = false,
    val specificJvmInitializer: String? = null,
)

class CodeBlockDefinition(
    val codeBlock: String,
    val applyOnTargets: Set<TargetPlatform> = setOf(
        TargetPlatform.COMMON,
        TargetPlatform.JVM,
        TargetPlatform.JS,
        TargetPlatform.NATIVE
    )
)

interface GeneralTypeDefinition {
    val typeName: TypeName
}

data class CustomTypeDefinition(override val typeName: TypeName) : GeneralTypeDefinition

enum class TypeDefinition(override val typeName: TypeName) : GeneralTypeDefinition {
    ARRAY_OF_UBYTES(UByteArray::class.asTypeName()),
    ARRAY_OF_UBYTES_LONG_SIZE(UByteArray::class.asTypeName()),
    ARRAY_OF_UBYTES_NO_SIZE(UByteArray::class.asTypeName()),
    LONG(Long::class.asTypeName()),
    INT(Int::class.asTypeName()),
    STRING(String::class.asTypeName()),
    UNIT(Unit::class.asTypeName()),
    UBYTE(UByte::class.asTypeName())
}

enum class TargetPlatform {
    JVM, NATIVE, JS, COMMON
}

fun fileDef(name: String, body: KotlinFileDefinition.() -> Unit): KotlinFileDefinition {
    val commonKotlinFileInstance = KotlinFileDefinition(name)
    commonKotlinFileInstance.body()
    return commonKotlinFileInstance
}


fun classDef(name: String, codeDocumentation: String = "", body: ClassDefinition.() -> Unit): ClassDefinition {
    val commonClass = ClassDefinition(name, codeDocumentation)
    commonClass.body()
    return commonClass
}

fun dataClassDef(name : String, codeDocumentation: String = "", parameters: List<ParameterDefinition>) : DataClassDefinition {
    return DataClassDefinition(name, codeDocumentation, parameters)
}

fun codeBlock(
    codeBlock: String,
    applyOnTargets: Set<TargetPlatform> = setOf(
        TargetPlatform.COMMON,
        TargetPlatform.JVM,
        TargetPlatform.JS,
        TargetPlatform.NATIVE
    )
): CodeBlockDefinition {
    val codeBlockDefinition = CodeBlockDefinition(codeBlock, applyOnTargets)
    return codeBlockDefinition

}

fun innerClassDef(
    name: String,
    javaName: String,
    jsName: String,
    nativeName: String,
    codeDocumentation: String = "",
    specificConstructor: String? = null,
    body: InnerClassDefinition.() -> Unit = {}
): InnerClassDefinition {
    val genClass = InnerClassDefinition(
        name,
        javaName,
        jsName,
        nativeName,
        codeDocumentation
    )
    genClass.body()
    return genClass
}

fun funcDef(
    name: String,
    javaName: String,
    jsName: String,
    nativeName: String,
    codeDocumentation: String = "",
    returnType: GeneralTypeDefinition,
    dynamicJsReturn: Boolean = false,
    isStateCreationFunction: Boolean = false,
    outputLengthWhenArray: Int = -1,
    customCodeBlockReplacesFunctionBody: List<CodeBlockDefinition>? = null,
    body: FunctionDefinition.() -> Unit
): FunctionDefinition {
    val function = FunctionDefinition(
        name,
        javaName,
        jsName,
        nativeName,
        codeDocumentation = codeDocumentation,
        returnType = returnType,
        dynamicJsReturn = dynamicJsReturn,
        isStateCreationFunction = isStateCreationFunction,
        outputLengthWhenArray = outputLengthWhenArray,
        customCodeBlockReplacesFunctionBody = customCodeBlockReplacesFunctionBody
    )
    function.body()
    return function
}

fun funcDef(
    name: String,
    codeDocumentation: String = "",
    returnType: GeneralTypeDefinition,
    dynamicJsReturn: Boolean = false,
    isStateCreationFunction: Boolean = false,
    outputLengthWhenArray: Int = -1,
    customCodeBlockReplacesFunctionBody: List<CodeBlockDefinition>? = null,
    body: FunctionDefinition.() -> Unit
): FunctionDefinition {
    val function =
        FunctionDefinition(
            name,
            name,
            name,
            name,
            codeDocumentation,
            returnType = returnType,
            dynamicJsReturn = dynamicJsReturn,
            isStateCreationFunction = isStateCreationFunction,
            outputLengthWhenArray = outputLengthWhenArray,
            customCodeBlockReplacesFunctionBody = customCodeBlockReplacesFunctionBody
        )
    function.body()
    return function
}



