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
    val jsSecretStreamInit = CodeBlockDefinition(
        """
        val stateAndHeader = getSodium().crypto_secretstream_xchacha20poly1305_init_push(key.toUInt8Array())
        val state = stateAndHeader.state
        val header = (stateAndHeader.header as Uint8Array).toUByteArray()
        return SecretStreamStateAndHeader(state, header) 
        """.trimIndent(),
        setOf(TargetPlatform.JS)
    )

    val jvmSecretStreamInit = CodeBlockDefinition(
        """
            val header = UByteArray(24)
            val state = SecretStream.State()
            sodium.crypto_secretstream_xchacha20poly1305_init_push(state, header.asByteArray(), key.asByteArray())
            return SecretStreamStateAndHeader(state, header)
        """.trimIndent(),
        setOf(TargetPlatform.JVM)
    )

    val nativeSecretStreamInit = CodeBlockDefinition(
        """
            val state = libsodium.sodium_malloc(libsodium.crypto_secretstream_xchacha20poly1305_state.size.convert())!!
                .reinterpret<libsodium.crypto_secretstream_xchacha20poly1305_state>()
                .pointed
            val header = UByteArray(crypto_secretstream_xchacha20poly1305_HEADERBYTES.toInt()) { 0U }
            val pinnedHeader = header.pin()
            libsodium.crypto_secretstream_xchacha20poly1305_init_push(state.ptr, pinnedHeader.addressOf(0), key.toCValues())
            pinnedHeader.unpin()
            return SecretStreamStateAndHeader(state, header)
        """.trimIndent(),
        setOf(TargetPlatform.NATIVE)
    )
    +funcDef(
        "crypto_secretstream_xchacha20poly1305_init_push",
        returnType = TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE,
        dynamicJsReturn = true,
        isStateCreationFunction = true,
        customCodeBlockReplacesFunctionBody = listOf(jsSecretStreamInit, jvmSecretStreamInit, nativeSecretStreamInit)
    ) {
        +ParameterDefinition(
            "key",
            parameterType = TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE
        )
    }


}
