package com.ionspin.kotlin.crypto.secretstream

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import libsodium.crypto_secretstream_xchacha20poly1305_ABYTES
import libsodium.crypto_secretstream_xchacha20poly1305_headerbytes
import libsodium.crypto_secretstream_xchacha20poly1305_init_pull
import libsodium.crypto_secretstream_xchacha20poly1305_init_push
import libsodium.crypto_secretstream_xchacha20poly1305_keygen
import libsodium.crypto_secretstream_xchacha20poly1305_pull
import libsodium.crypto_secretstream_xchacha20poly1305_push
import libsodium.crypto_secretstream_xchacha20poly1305_rekey
import platform.posix.malloc

actual typealias SecretStreamState = libsodium.crypto_secretstream_xchacha20poly1305_state

actual object SecretStream {
    actual fun xChaCha20Poly1305InitPush(key: UByteArray): SecretStreamStateAndHeader {
        val stateAllocated = malloc(SecretStreamState.size.convert())
        val statePointed = stateAllocated!!.reinterpret<SecretStreamState>().pointed

        val header = UByteArray(crypto_secretstream_xchacha20poly1305_headerbytes().convert()) { 0U }
        val headerPinned = header.pin()
        val keyPinned = key.pin()
        crypto_secretstream_xchacha20poly1305_init_push(
            statePointed.ptr,
            headerPinned.toPtr(),
            keyPinned.toPtr()
        )
        headerPinned.unpin()
        keyPinned.unpin()
        return SecretStreamStateAndHeader(statePointed, header)

    }

    actual fun xChaCha20Poly1305Push(
        state: SecretStreamState,
        message: UByteArray,
        associatedData: UByteArray,
        tag: UByte
    ): UByteArray {
        val ciphertext = UByteArray(message.size + crypto_secretstream_xchacha20poly1305_ABYTES.toInt()) { 0U }
        val ciphertextPinned = ciphertext.pin()
        val messagePinned = message.pin()
        val associatedDataPinned = if (associatedData.isNotEmpty()) {
            associatedData.pin()
        } else {
            null
        }
        crypto_secretstream_xchacha20poly1305_push(
            state.ptr,
            ciphertextPinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            associatedDataPinned?.toPtr(),
            associatedData.size.convert(),
            tag
        )

        ciphertextPinned.unpin()
        messagePinned.unpin()
        associatedDataPinned?.unpin()
        return ciphertext
    }

    actual fun xChaCha20Poly1305InitPull(
        key: UByteArray,
        header: UByteArray
    ): SecretStreamStateAndHeader {
        val stateAllocated = malloc(SecretStreamState.size.convert())
        val statePointed = stateAllocated!!.reinterpret<SecretStreamState>().pointed
        val keyPinned = key.pin()
        val headerPinned = header.pin()
        crypto_secretstream_xchacha20poly1305_init_pull(
            statePointed.ptr,
            headerPinned.toPtr(),
            keyPinned.toPtr()
        )
        headerPinned.unpin()
        keyPinned.unpin()
        return SecretStreamStateAndHeader(statePointed, header)
    }

    actual fun xChaCha20Poly1305Pull(
        state: SecretStreamState,
        ciphertext: UByteArray,
        associatedData: UByteArray
    ): DecryptedDataAndTag {
        val message = UByteArray(ciphertext.size - crypto_secretstream_xchacha20poly1305_ABYTES.toInt())
        val messagePinned = message.pin()
        val ciphertextPinned = ciphertext.pin()
        val associatedDataPinned = if (associatedData.isNotEmpty()) {
            associatedData.pin()
        } else {
            null
        }
        val tag = UByteArray(1) { 0U }
        val tagPinned = tag.pin()
        val validationResult = crypto_secretstream_xchacha20poly1305_pull(
            state.ptr,
            messagePinned.toPtr(),
            null,
            tagPinned.toPtr(),
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            associatedDataPinned?.toPtr(),
            associatedData.size.convert()
            )
        ciphertextPinned.unpin()
        messagePinned.unpin()
        associatedDataPinned?.unpin()
        tagPinned.unpin()
        if (validationResult != 0) {
            throw SecretStreamCorruptedOrTamperedDataException()
        }
        return DecryptedDataAndTag(message, tag[0])
    }

    actual fun xChaCha20Poly1305Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_secretstream_xchacha20poly1305_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_secretstream_xchacha20poly1305_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

    actual fun xChaCha20Poly1305Rekey(state: SecretStreamState) {
        crypto_secretstream_xchacha20poly1305_rekey(state.ptr)
    }

}
