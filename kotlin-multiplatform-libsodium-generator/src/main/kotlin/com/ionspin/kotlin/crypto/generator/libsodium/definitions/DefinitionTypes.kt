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
    val innerClasses: MutableList<InnerClassDefinition> = mutableListOf(),
    val methods: MutableList<FunctionDefinition> = mutableListOf()
) {
    operator fun InnerClassDefinition.unaryPlus() {
        innerClasses.add(this)
    }

    operator fun FunctionDefinition.unaryPlus() {
        methods.add(this)
    }
}

class InnerClassDefinition (
    val name: String,
    val javaName: String,
    val jsName: String,
    val nativeName: String,
    val functions: MutableList<FunctionDefinition> = mutableListOf()
)

class FunctionDefinition(
    val name: String,
    val javaName: String,
    val jsName: String,
    val nativeName: String,
    val parameterList: MutableList<ParameterDefinition> = mutableListOf(),
    val returnType: GeneralTypeDefinition
) {
    operator fun ParameterDefinition.unaryPlus() {
        parameterList.add(this)
    }
}

class ParameterDefinition(
    val parameterName: String,
    val parameterType: GeneralTypeDefinition,
    val modifiesReturn: Boolean = false
)

interface GeneralTypeDefinition {
    val typeName : TypeName
}

data class CustomTypeDefinition(override val typeName: TypeName) : GeneralTypeDefinition

enum class TypeDefinition(override val typeName: TypeName) : GeneralTypeDefinition {
    ARRAY_OF_UBYTES(UByteArray::class.asTypeName()),
    ARRAY_OF_UBYTES_LONG_SIZE(UByteArray::class.asTypeName()),
    ARRAY_OF_UBYTES_NO_SIZE(UByteArray::class.asTypeName()),
    LONG(Long::class.asTypeName()),
    INT(Int::class.asTypeName()),
    STRING(String::class.asTypeName()),
    UNIT(Unit::class.asTypeName())
}

fun fileDef(name: String, body: KotlinFileDefinition.() -> Unit) : KotlinFileDefinition {
    val commonKotlinFileInstance = KotlinFileDefinition(name)
    commonKotlinFileInstance.body()
    return commonKotlinFileInstance
}


fun classDef(name: String, body: ClassDefinition.() -> Unit): ClassDefinition {
    val commonClass = ClassDefinition(name)
    commonClass.body()
    return commonClass
}

fun innerClassDef(
    name: String,
    javaName: String,
    jsName: String,
    nativeName: String,
    body: InnerClassDefinition.() -> Unit = {}
) : InnerClassDefinition {
    val genClass = InnerClassDefinition(
        name,
        javaName,
        jsName,
        nativeName
    )
    genClass.body()
    return genClass
}

fun funcDef(
    name: String,
    javaName: String,
    jsName: String,
    nativeName: String,
    returnType: GeneralTypeDefinition,
    body: FunctionDefinition.() -> Unit
): FunctionDefinition {
    val function = FunctionDefinition(name, javaName, jsName, nativeName, returnType = returnType)
    function.body()
    return function
}

fun funcDef(
    name: String,
    returnType: GeneralTypeDefinition,
    body: FunctionDefinition.() -> Unit
): FunctionDefinition {
    val function = FunctionDefinition(name, name, name, name, returnType = returnType)
    function.body()
    return function
}



