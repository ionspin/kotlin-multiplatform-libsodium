package com.ionspin.kotlin.crypto.generator.libsodium.definitions

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 01-Aug-2020
 */
object LibSodiumDefinitions {
    val testKotlinFile = fileDef("DebugTest") {
        +classDef("Crypto") {
            defineHashFunctions()
            defineGenericHashFunctions()
        }
    }
}
