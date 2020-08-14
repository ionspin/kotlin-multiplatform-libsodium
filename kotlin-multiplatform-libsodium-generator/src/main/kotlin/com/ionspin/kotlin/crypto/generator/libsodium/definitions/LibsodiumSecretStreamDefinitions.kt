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
    +dataClassDef(
        "SecretStreamStateAndHeader",
        """
        This data class wraps the state and header objects returned when initializing secret stream encryption
        """.trimIndent(),
        listOf(
            ParameterDefinition(
                parameterName = "state",
                parameterType = CustomTypeDefinition(withPackageName("SecretStreamState"))
            ),
            ParameterDefinition(
                parameterName = "header",
                parameterType = TypeDefinition.ARRAY_OF_UBYTES
            )

        )

    )
    +funcDef(
        "crypto_secretstream_xchacha20poly1305_init_push",
        returnType = TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE,
        dynamicJsReturn = true,
        isStateCreationFunction = true
    ) {
        +ParameterDefinition(
            "key",
            parameterType = TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE
        )
    }


}
