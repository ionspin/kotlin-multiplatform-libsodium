package com.ionspin.kotlin.crypto.generator.libsodium.definitions

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 14/Aug/2020
 */
fun ClassDefinition.defineSecretStreamFunctions() {
    +innerClassDef(
        "SecretStreamState",
        "com.goterl.lazycode.lazysodium.interfaces.SecretStream.State",
        "SecretStreamState",
        "crypto_secretstream_xchacha20poly1305_state"
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
        val pinnedKey = key.pin()
        val state = sodium_malloc(libsodium.crypto_secretstream_xchacha20poly1305_state.size.convert())!!
            .reinterpret<libsodium.crypto_secretstream_xchacha20poly1305_state>()
            .pointed
        val header = UByteArray(libsodium.crypto_secretstream_xchacha20poly1305_HEADERBYTES.toInt()) { 0U }
        val pinnedHeader = header.pin()
        libsodium.crypto_secretstream_xchacha20poly1305_init_push(state.ptr, pinnedHeader.addressOf(0), pinnedKey.addressOf(0))
        pinnedHeader.unpin()
        pinnedKey.unpin()
        return SecretStreamStateAndHeader(state, header)
        """.trimIndent(),
        setOf(TargetPlatform.NATIVE)
    )
    +funcDef(
        "crypto_secretstream_xchacha20poly1305_init_push",
        codeDocumentation = """
            Initialize a state and generate a random header. Both are returned inside `SecretStreamStateAndHeader` object.
        """.trimIndent(),
        returnType = CustomTypeDefinition(withPackageName("SecretStreamStateAndHeader")),
        dynamicJsReturn = true,
        isStateCreationFunction = true,
        customCodeBlockReplacesFunctionBody = listOf(jsSecretStreamInit, jvmSecretStreamInit, nativeSecretStreamInit)
    ) {
        +ParameterDefinition(
            "key",
            parameterType = TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE
        )
    }


    +funcDef(
        "crypto_secretstream_xchacha20poly1305_init_pull",
        codeDocumentation = """
            Initialize state from header and key. The state can then be used for decryption.
        """.trimIndent(),
        returnType = CustomTypeDefinition(withPackageName("SecretStreamState")),
        dynamicJsReturn = true,
        isStateCreationFunction = true,
    ) {
        +ParameterDefinition(
            "state",
            parameterType = CustomTypeDefinition(withPackageName("SecretStreamState")),
            dropParameterFromDefinition = true,
            isStateType = true
        )
        +ParameterDefinition(
            "header",
            parameterType = TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE
        )
        +ParameterDefinition(
            "key",
            parameterType = TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE
        )
    }

    +funcDef(
        name = "crypto_secretstream_xchacha20poly1305_push",
        codeDocumentation = """
            Encrypt next block of data using the previously initialized state. Returns encrypted block.
        """.trimIndent(),
        returnType = TypeDefinition.ARRAY_OF_UBYTES
    ) {
        +ParameterDefinition(
            "state",
            CustomTypeDefinition(withPackageName("SecretStreamState"))
        )
        +ParameterDefinition(
            "c",
            TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE,
            isActuallyAnOutputParam = true,
            dropParameterFromDefinition = true
        )
        +ParameterDefinition(
            "clen",
            TypeDefinition.NULL,
            dropParameterFromDefinition = true
        )
        +ParameterDefinition(
            "m",
            TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE,
            modifiesReturnObjectSize = true,
            specificReturnModification = "m.size + 17"
        )
        +ParameterDefinition(
            "ad",
            TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE
        )
        +ParameterDefinition(
            "tag",
            TypeDefinition.UBYTE
        )
    }

    +funcDef(
        name = "crypto_secretstream_xchacha20poly1305_pull",
        codeDocumentation = """
            Decrypt next block of data using the previously initialized state. Returns decrypted block.
        """.trimIndent(),
        returnType = TypeDefinition.ARRAY_OF_UBYTES
    ) {
        +ParameterDefinition(
            "state",
            CustomTypeDefinition(withPackageName("SecretStreamState"))
        )
        +ParameterDefinition(
            "m",
            TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE,
            isActuallyAnOutputParam = true,
            dropParameterFromDefinition = true
        )
        +ParameterDefinition(
            "mlen",
            TypeDefinition.NULL,
            dropParameterFromDefinition = true
        )
        +ParameterDefinition(
            "tag_p",
            TypeDefinition.NULL
        )
        +ParameterDefinition(
            "c",
            TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE,
            modifiesReturnObjectSize = true,
            specificReturnModification = "c.size - 17"
        )
        +ParameterDefinition(
            "ad",
            TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE
        )

    }



}
