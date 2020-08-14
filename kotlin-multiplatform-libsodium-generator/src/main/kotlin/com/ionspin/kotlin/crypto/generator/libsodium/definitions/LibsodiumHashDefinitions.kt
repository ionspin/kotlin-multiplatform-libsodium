package com.ionspin.kotlin.crypto.generator.libsodium.definitions

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 04-Aug-2020
 */
fun ClassDefinition.defineHashFunctions() {
    /*
    --------------- SHA256
     */
    +innerClassDef(
        "Sha256State",
        "com.goterl.lazycode.lazysodium.interfaces.Hash.State256",
        "Sha256State",
        "crypto_hash_sha256_state"
    )
    +funcDef(
        "crypto_hash_sha256_init",
        codeDocumentation = """
            Initialize the SHA256 hash
            returns sha 256 state
        """.trimIndent(),
        returnType = CustomTypeDefinition(ClassName(packageName, "Sha256State")),
        dynamicJsReturn = true,
        isStateCreationFunction = true
    ) {
        +ParameterDefinition(
            "state",
            CustomTypeDefinition((withPackageName("Sha256State"))),
            dropParameterFromDefinition = true,
            isStateType = true
        )
    }

    +funcDef("crypto_hash_sha256_update", returnType = TypeDefinition.UNIT) {
        +ParameterDefinition(
            "state",
            CustomTypeDefinition((withPackageName("Sha256State"))),
            isStateType = true
        )
        +ParameterDefinition("input", TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE)
    }

    +funcDef("crypto_hash_sha256_final", returnType = TypeDefinition.ARRAY_OF_UBYTES, outputLengthWhenArray = 32) {
        +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha256State"))))
        +ParameterDefinition("out", TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE, isActuallyAnOutputParam = true, dropParameterFromDefinition = true)
    }

    /*
   --------------- SHA512
    */
    +innerClassDef(
        "Sha512State",
        "com.goterl.lazycode.lazysodium.interfaces.Hash.State512",
        "Sha512State",
        "crypto_hash_sha512_state"
    )
    +funcDef(
        "crypto_hash_sha512_init",
        returnType = CustomTypeDefinition(ClassName(packageName, "Sha512State")),
        dynamicJsReturn = true,
        isStateCreationFunction = true
    ) {
        +ParameterDefinition(
            "state",
            CustomTypeDefinition((withPackageName("Sha512State"))),
            dropParameterFromDefinition = true,
            isStateType = true
        )
    }

    +funcDef("crypto_hash_sha512_update", returnType = TypeDefinition.UNIT) {
        +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha512State"))))
        +ParameterDefinition("input", TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE)
    }

    +funcDef("crypto_hash_sha512_final", returnType = TypeDefinition.ARRAY_OF_UBYTES, outputLengthWhenArray = 64) {
        +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha512State"))))
        +ParameterDefinition("out", TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE, isActuallyAnOutputParam = true, dropParameterFromDefinition = true)
    }
}
