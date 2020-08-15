package com.ionspin.kotlin.crypto.generator.libsodium.definitions

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Aug-2020
 */
fun ClassDefinition.defineGenericHashFunctions() {
    /*
     * ------------- GENERIC HASH (BLAKE2B)
     */

    +innerClassDef(
        "GenericHashState",
        "kotlin.ByteArray",
        "Uint8Array",
        "crypto_generichash_blake2b_state"
    )

    +funcDef(
        "crypto_generichash_init",
        returnType = CustomTypeDefinition(ClassName(packageName, "GenericHashState")),
        dynamicJsReturn = true,
        isStateCreationFunction = true
    ) {
        +ParameterDefinition(
            "state",
            CustomTypeDefinition((withPackageName("GenericHashState"))),
            isStateType = true,
            dropParameterFromDefinition = true,
            specificJvmInitializer = "sodium.crypto_generichash_statebytes()"
        )
        +ParameterDefinition("key", TypeDefinition.ARRAY_OF_UBYTES)
        +ParameterDefinition("outlen", TypeDefinition.INT, modifiesReturnObjectSize = true)
    }
}
