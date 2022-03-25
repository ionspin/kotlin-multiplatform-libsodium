package com.ionspin.kotlin.crypto.secretstream

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

actual typealias SecretStreamState = Any

actual object SecretStream {
    actual fun xChaCha20Poly1305InitPush(key: UByteArray): SecretStreamStateAndHeader {
        val state = getSodium().crypto_secretstream_xchacha20poly1305_init_push(key.toUInt8Array())
        return SecretStreamStateAndHeader(state.state, (state.header as Uint8Array).toUByteArray())
    }

    actual fun xChaCha20Poly1305Push(
        state: SecretStreamState,
        message: UByteArray,
        associatedData: UByteArray,
        tag: UByte
    ): UByteArray {
        return getSodium().crypto_secretstream_xchacha20poly1305_push(
            state, message.toUInt8Array(), associatedData.toUInt8Array(), tag
        ).toUByteArray()
    }

    actual fun xChaCha20Poly1305InitPull(
        key: UByteArray,
        header: UByteArray
    ): SecretStreamStateAndHeader {
        val state = getSodium().crypto_secretstream_xchacha20poly1305_init_pull(header.toUInt8Array(), key.toUInt8Array())
        return SecretStreamStateAndHeader(state, header)
    }

    actual fun xChaCha20Poly1305Pull(
        state: SecretStreamState,
        ciphertext: UByteArray,
        associatedData: UByteArray
    ): DecryptedDataAndTag {
        val dataAndTag = getSodium().crypto_secretstream_xchacha20poly1305_pull(
            state, ciphertext.toUInt8Array(), associatedData.toUInt8Array()
        )
        if (dataAndTag == false) {
            throw SecretStreamCorruptedOrTamperedDataException()
        }
        return DecryptedDataAndTag((dataAndTag.message as Uint8Array).toUByteArray(), dataAndTag.tag)

    }

    actual fun xChaCha20Poly1305Keygen(): UByteArray {
        return getSodium().crypto_shorthash_keygen().toUByteArray()
    }

    actual fun xChaCha20Poly1305Rekey(state: SecretStreamState) {
        getSodium().crypto_secretstream_xchacha20poly1305_rekey(state)
    }

}
