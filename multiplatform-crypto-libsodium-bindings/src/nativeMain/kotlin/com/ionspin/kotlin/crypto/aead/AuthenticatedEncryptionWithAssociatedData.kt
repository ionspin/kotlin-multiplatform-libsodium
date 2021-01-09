package com.ionspin.kotlin.crypto.aead

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_aead_chacha20poly1305_decrypt
import libsodium.crypto_aead_chacha20poly1305_decrypt_detached
import libsodium.crypto_aead_chacha20poly1305_encrypt
import libsodium.crypto_aead_chacha20poly1305_encrypt_detached
import libsodium.crypto_aead_chacha20poly1305_ietf_decrypt
import libsodium.crypto_aead_chacha20poly1305_ietf_decrypt_detached
import libsodium.crypto_aead_chacha20poly1305_ietf_encrypt
import libsodium.crypto_aead_chacha20poly1305_ietf_encrypt_detached
import libsodium.crypto_aead_chacha20poly1305_ietf_keygen
import libsodium.crypto_aead_chacha20poly1305_keygen
import libsodium.crypto_aead_xchacha20poly1305_ietf_decrypt
import libsodium.crypto_aead_xchacha20poly1305_ietf_decrypt_detached
import libsodium.crypto_aead_xchacha20poly1305_ietf_encrypt
import libsodium.crypto_aead_xchacha20poly1305_ietf_encrypt_detached
import libsodium.crypto_aead_xchacha20poly1305_ietf_keygen

actual object AuthenticatedEncryptionWithAssociatedData {

    // Ietf

    // Original chacha20poly1305
    actual fun xChaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val messagePinned = message.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val ciphertext = UByteArray(message.size + crypto_aead_xchacha20poly1305_ietf_ABYTES)
        val ciphertextPinned = ciphertext.pin()

        crypto_aead_xchacha20poly1305_ietf_encrypt(
            ciphertextPinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            null, // nsec not used in this construct
            noncePinned.toPtr(),
            keyPinned.toPtr()

        )

        ciphertextPinned.unpin()

        messagePinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return ciphertext
    }

    actual fun xChaCha20Poly1305IetfDecrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val ciphertextPinned = ciphertextAndTag.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val message = UByteArray(ciphertextAndTag.size - crypto_aead_xchacha20poly1305_ietf_ABYTES)
        val messagePinned = message.pin()

        val validationResult = crypto_aead_xchacha20poly1305_ietf_decrypt(
            messagePinned.toPtr(),
            null,
            null,
            ciphertextPinned.toPtr(),
            ciphertextAndTag.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()

        ciphertextPinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        if (validationResult != 0) {
            throw AeadCorrupedOrTamperedDataException()
        }

        return message
    }

    actual fun xChaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        val messagePinned = message.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val ciphertext = UByteArray(message.size)
        val ciphertextPinned = ciphertext.pin()

        val authenticationTag = UByteArray(crypto_aead_xchacha20poly1305_ietf_ABYTES)
        val authenticationTagPinned = authenticationTag.pin()

        crypto_aead_xchacha20poly1305_ietf_encrypt_detached(
            ciphertextPinned.toPtr(),
            authenticationTagPinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            null, // nsec not used in this construct
            noncePinned.toPtr(),
            keyPinned.toPtr()

        )

        ciphertextPinned.unpin()

        messagePinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return AeadEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun xChaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val ciphertextPinned = ciphertext.pin()
        val tagPinned = tag.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val message = UByteArray(ciphertext.size)
        val messagePinned = message.pin()

        val validationResult = crypto_aead_xchacha20poly1305_ietf_decrypt_detached(
            messagePinned.toPtr(),
            null,
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            tagPinned.toPtr(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()

        ciphertextPinned.unpin()
        tagPinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        if (validationResult != 0) {
            throw AeadCorrupedOrTamperedDataException()
        }

        return message
    }

    actual fun chaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val messagePinned = message.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val ciphertext = UByteArray(message.size + crypto_aead_chacha20poly1305_ietf_ABYTES)
        val ciphertextPinned = ciphertext.pin()

        crypto_aead_chacha20poly1305_ietf_encrypt(
            ciphertextPinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            null, // nsec not used in this construct
            noncePinned.toPtr(),
            keyPinned.toPtr()

        )

        ciphertextPinned.unpin()

        messagePinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return ciphertext
    }

    actual fun chaCha20Poly1305IetfDecrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val ciphertextPinned = ciphertextAndTag.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val message = UByteArray(ciphertextAndTag.size - crypto_aead_chacha20poly1305_ietf_ABYTES)
        val messagePinned = message.pin()

        val validationResult = crypto_aead_chacha20poly1305_ietf_decrypt(
            messagePinned.toPtr(),
            null,
            null,
            ciphertextPinned.toPtr(),
            ciphertextAndTag.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()

        ciphertextPinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        if (validationResult != 0) {
            throw AeadCorrupedOrTamperedDataException()
        }

        return message
    }

    actual fun chaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        val messagePinned = message.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val ciphertext = UByteArray(message.size)
        val ciphertextPinned = ciphertext.pin()

        val authenticationTag = UByteArray(crypto_aead_chacha20poly1305_ietf_ABYTES)
        val authenticationTagPinned = authenticationTag.pin()

        crypto_aead_chacha20poly1305_ietf_encrypt_detached(
            ciphertextPinned.toPtr(),
            authenticationTagPinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            null, // nsec not used in this construct
            noncePinned.toPtr(),
            keyPinned.toPtr()

        )

        ciphertextPinned.unpin()

        messagePinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return AeadEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun chaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val ciphertextPinned = ciphertext.pin()
        val tagPinned = tag.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val message = UByteArray(ciphertext.size)
        val messagePinned = message.pin()

        val validationResult = crypto_aead_chacha20poly1305_ietf_decrypt_detached(
            messagePinned.toPtr(),
            null,
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            tagPinned.toPtr(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()

        ciphertextPinned.unpin()
        tagPinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        if (validationResult != 0) {
            throw AeadCorrupedOrTamperedDataException()
        }

        return message
    }

    actual fun chaCha20Poly1305Encrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val messagePinned = message.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val ciphertext = UByteArray(message.size + crypto_aead_chacha20poly1305_ABYTES)
        val ciphertextPinned = ciphertext.pin()

        crypto_aead_chacha20poly1305_encrypt(
            ciphertextPinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            null, // nsec not used in this construct
            noncePinned.toPtr(),
            keyPinned.toPtr()

        )

        ciphertextPinned.unpin()

        messagePinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return ciphertext
    }

    actual fun chaCha20Poly1305Decrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val ciphertextPinned = ciphertextAndTag.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val message = UByteArray(ciphertextAndTag.size - crypto_aead_chacha20poly1305_ABYTES)
        val messagePinned = message.pin()

        val validationResult = crypto_aead_chacha20poly1305_decrypt(
            messagePinned.toPtr(),
            null,
            null,
            ciphertextPinned.toPtr(),
            ciphertextAndTag.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()

        ciphertextPinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        if (validationResult != 0) {
            throw AeadCorrupedOrTamperedDataException()
        }

        return message
    }

    actual fun chaCha20Poly1305EncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        val messagePinned = message.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val ciphertext = UByteArray(message.size)
        val ciphertextPinned = ciphertext.pin()

        val authenticationTag = UByteArray(crypto_aead_chacha20poly1305_ABYTES)
        val authenticationTagPinned = authenticationTag.pin()

        crypto_aead_chacha20poly1305_encrypt_detached(
            ciphertextPinned.toPtr(),
            authenticationTagPinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            null, // nsec not used in this construct
            noncePinned.toPtr(),
            keyPinned.toPtr()

        )

        ciphertextPinned.unpin()

        messagePinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return AeadEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun chaCha20Poly1305DecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val ciphertextPinned = ciphertext.pin()
        val tagPinned = tag.pin()
        val associatedDataPinned = associatedData.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        val message = UByteArray(ciphertext.size)
        val messagePinned = message.pin()

        val validationResult = crypto_aead_chacha20poly1305_decrypt_detached(
            messagePinned.toPtr(),
            null,
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            tagPinned.toPtr(),
            associatedDataPinned.toPtr(),
            associatedData.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()

        ciphertextPinned.unpin()
        tagPinned.unpin()
        associatedDataPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        if (validationResult != 0) {
            throw AeadCorrupedOrTamperedDataException()
        }

        return message
    }

    actual fun xChaCha20Poly1305IetfKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_aead_xchacha20poly1305_ietf_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_aead_xchacha20poly1305_ietf_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

    actual fun chaCha20Poly1305IetfKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_aead_chacha20poly1305_ietf_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_aead_chacha20poly1305_ietf_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

    actual fun chaCha20Poly1305Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_aead_chacha20poly1305_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_aead_chacha20poly1305_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

}
