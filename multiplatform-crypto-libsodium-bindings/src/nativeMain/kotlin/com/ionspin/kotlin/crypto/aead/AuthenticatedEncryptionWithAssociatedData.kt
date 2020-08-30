package com.ionspin.kotlin.crypto.aead

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_aead_chacha20poly1305_encrypt
import libsodium.crypto_aead_chacha20poly1305_ietf_encrypt
import libsodium.crypto_aead_xchacha20poly1305_ietf_encrypt

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
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun xChaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        TODO("not implemented yet")
    }

    actual fun xChaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
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
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
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
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305EncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305DecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

}
