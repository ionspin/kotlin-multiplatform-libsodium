package com.ionspin.kotlin.crypto.secretstream

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna
import com.ionspin.kotlin.crypto.SecretStreamXChaCha20Poly1305State

actual typealias SecretStreamState = SecretStreamXChaCha20Poly1305State

actual object SecretStream {
    actual fun xChaCha20Poly1305InitPush(key: UByteArray): SecretStreamStateAndHeader {
        val state = SecretStreamState()
        val header = UByteArray(sodiumJna.crypto_secretstream_xchacha20poly1305_headerbytes())
        sodiumJna.crypto_secretstream_xchacha20poly1305_init_push(state, header.asByteArray(), key.asByteArray())
        return SecretStreamStateAndHeader(state, header)
    }

    actual fun xChaCha20Poly1305Push(
        state: SecretStreamState,
        message: UByteArray,
        associatedData: UByteArray,
        tag: UByte
    ): UByteArray {
        val ciphertext = UByteArray(message.size + crypto_secretstream_xchacha20poly1305_ABYTES)
        sodiumJna.crypto_secretstream_xchacha20poly1305_push(
            state,
            ciphertext.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            tag.toByte()
        )
        return ciphertext
    }

    actual fun xChaCha20Poly1305InitPull(
        key: UByteArray,
        header: UByteArray
    ): SecretStreamStateAndHeader {
        val state = SecretStreamState()
        sodiumJna.crypto_secretstream_xchacha20poly1305_init_pull(state, header.asByteArray(), key.asByteArray())
        return SecretStreamStateAndHeader(state, header)
    }

    actual fun xChaCha20Poly1305Pull(
        state: SecretStreamState,
        ciphertext: UByteArray,
        associatedData: UByteArray
    ): DecryptedDataAndTag {
        val result = UByteArray(ciphertext.size - crypto_secretstream_xchacha20poly1305_ABYTES)
        val tagArray = UByteArray(1) { 0U }
        val validationResult = sodiumJna.crypto_secretstream_xchacha20poly1305_pull(
            state,
            result.asByteArray(),
            null,
            tagArray.asByteArray(),
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong()
        )
        if (validationResult != 0) {
            throw SecretStreamCorruptedOrTamperedDataException()
        }
        return DecryptedDataAndTag(result, tagArray[0])
    }

    actual fun xChaCha20Poly1305Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_secretstream_xchacha20poly1305_KEYBYTES)
        sodiumJna.crypto_secretstream_xchacha20poly1305_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun xChaCha20Poly1305Rekey(state: SecretStreamState) {
        sodiumJna.crypto_secretstream_xchacha20poly1305_rekey(state)
    }

}
