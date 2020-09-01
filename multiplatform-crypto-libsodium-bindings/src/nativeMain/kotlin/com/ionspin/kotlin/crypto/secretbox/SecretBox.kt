package com.ionspin.kotlin.crypto.secretbox

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_secretbox_detached
import libsodium.crypto_secretbox_easy
import libsodium.crypto_secretbox_keygen
import libsodium.crypto_secretbox_open_detached
import libsodium.crypto_secretbox_open_easy

actual object SecretBox {
    actual fun easy(message: UByteArray, nonce: UByteArray, key: UByteArray): UByteArray {
        val ciphertext = UByteArray(message.size + crypto_secretbox_MACBYTES)
        val ciphertextPinned = ciphertext.pin()
        val messagePinned  = message.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()
        crypto_secretbox_easy(
            ciphertextPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )
        ciphertextPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()
        return ciphertext
    }

    actual fun openEasy(
        ciphertext: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size - crypto_secretbox_MACBYTES)
        val messagePinned  = message.pin()
        val ciphertextPinned = ciphertext.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()
        val verificationResult = crypto_secretbox_open_easy(
            messagePinned.toPtr(),
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )
        ciphertextPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()
        if (verificationResult != 0) {
            throw SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey()
        }
        return message
    }

    actual fun detached(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): SecretBoxEncryptedDataAndTag {
        val ciphertext = UByteArray(message.size)
        val authenticationTag = UByteArray(crypto_secretbox_MACBYTES)
        val ciphertextPinned = ciphertext.pin()
        val authenticationTagPinned = authenticationTag.pin()
        val messagePinned  = message.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()
        crypto_secretbox_detached(
            ciphertextPinned.toPtr(),
            authenticationTagPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )
        ciphertextPinned.unpin()
        authenticationTagPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()
        return SecretBoxEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun openDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size)
        val messagePinned  = message.pin()
        val ciphertextPinned = ciphertext.pin()
        val tagPinned = tag.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()
        val verificationResult = crypto_secretbox_open_detached(
            messagePinned.toPtr(),
            ciphertextPinned.toPtr(),
            tagPinned.toPtr(),
            ciphertext.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )
        ciphertextPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()
        if (verificationResult != 0) {
            throw SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey()
        }
        return message
    }

    actual fun keygen() :UByteArray {
        val generatedKey = UByteArray(crypto_secretbox_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_secretbox_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

}
