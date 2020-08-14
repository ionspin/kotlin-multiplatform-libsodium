package com.ionspin.kotlin.crypto.generator.libsodium.definitions

import com.squareup.kotlinpoet.ClassName

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 14/Aug/2020
 */
fun ClassDefinition.defineSecretStreamFunctions() {
    +innerClassDef(
        "SecretStreamState",
        "com.goterl.lazycode.lazysodium.interfaces.SecretStream.State",
        "SecretStreamState",
        "crypto_hash_sha256_state"
    )
    +funcDef(
        "crypto_secretstream_xchacha20poly1305_init_push",
        returnType = TypeDefinition.ARRAY_OF_UBYTES,
        dynamicJsReturn = true,
        isStateCreationFunction = true
    ) {
        +ParameterDefinition(
            "state",
            CustomTypeDefinition((withPackageName("SecretStreamState"))),
            dropParameterFromDefinition = true,
            isActuallyAnOutputParam = true
        )
    }


}
