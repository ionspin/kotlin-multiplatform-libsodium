package com.ionspin.kotlin.crypto.aead

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

actual object AuthenticatedEncryptionWithAssociatedData {

    // Ietf

    // Original chacha20poly1305
    actual fun xChaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        return getSodium().crypto_aead_xchacha20poly1305_ietf_encrypt(
            message.toUInt8Array(),
            associatedData.toUInt8Array(),
            null,
            nonce.toUInt8Array(),
            key.toUInt8Array(),
        ).toUByteArray()
    }

    actual fun xChaCha20Poly1305IetfDecrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_aead_xchacha20poly1305_ietf_decrypt(
                null,
                ciphertextAndTag.toUInt8Array(),
                associatedData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            ).toUByteArray()
        } catch (error: Throwable) {
            throw AeadCorrupedOrTamperedDataException()
        }
    }

    actual fun xChaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        val result = getSodium().crypto_aead_xchacha20poly1305_ietf_encrypt_detached(
            message.toUInt8Array(),
            associatedData.toUInt8Array(),
            null,
            nonce.toUInt8Array(),
            key.toUInt8Array(),
        )
        return AeadEncryptedDataAndTag(
            (result.ciphertext as Uint8Array).toUByteArray(),
            (result.mac as Uint8Array).toUByteArray()
        )
    }

    actual fun xChaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_aead_xchacha20poly1305_ietf_decrypt_detached(
                null,
                ciphertext.toUInt8Array(),
                tag.toUInt8Array(),
                associatedData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            ).toUByteArray()
        } catch (error: Throwable) {
            throw AeadCorrupedOrTamperedDataException()
        }
    }

    actual fun chaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        return getSodium().crypto_aead_chacha20poly1305_ietf_encrypt(
            message.toUInt8Array(),
            associatedData.toUInt8Array(),
            null,
            nonce.toUInt8Array(),
            key.toUInt8Array(),
        ).toUByteArray()
    }

    actual fun chaCha20Poly1305IetfDecrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_aead_chacha20poly1305_ietf_decrypt(
                null,
                ciphertextAndTag.toUInt8Array(),
                associatedData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            ).toUByteArray()
        } catch (error: Throwable) {
            throw AeadCorrupedOrTamperedDataException()
        }
    }

    actual fun chaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        val result = getSodium().crypto_aead_chacha20poly1305_ietf_encrypt_detached(
            message.toUInt8Array(),
            associatedData.toUInt8Array(),
            null,
            nonce.toUInt8Array(),
            key.toUInt8Array(),
        )
        return AeadEncryptedDataAndTag(
            (result.ciphertext as Uint8Array).toUByteArray(),
            (result.mac as Uint8Array).toUByteArray()
        )
    }

    actual fun chaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_aead_chacha20poly1305_ietf_decrypt_detached(
                null,
                ciphertext.toUInt8Array(),
                tag.toUInt8Array(),
                associatedData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            ).toUByteArray()
        } catch (error: Throwable) {
            throw AeadCorrupedOrTamperedDataException()
        }
    }

    actual fun chaCha20Poly1305Encrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        return getSodium().crypto_aead_chacha20poly1305_encrypt(
            message.toUInt8Array(),
            associatedData.toUInt8Array(),
            null,
            nonce.toUInt8Array(),
            key.toUInt8Array(),
        ).toUByteArray()
    }

    actual fun chaCha20Poly1305Decrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_aead_chacha20poly1305_decrypt(
                null,
                ciphertextAndTag.toUInt8Array(),
                associatedData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            ).toUByteArray()
        } catch (error: Throwable) {
            throw AeadCorrupedOrTamperedDataException()
        }
    }

    actual fun chaCha20Poly1305EncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        val result = getSodium().crypto_aead_chacha20poly1305_encrypt_detached(
            message.toUInt8Array(),
            associatedData.toUInt8Array(),
            null,
            nonce.toUInt8Array(),
            key.toUInt8Array(),
        )
        return AeadEncryptedDataAndTag(
            (result.ciphertext as Uint8Array).toUByteArray(),
            (result.mac as Uint8Array).toUByteArray()
        )
    }

    actual fun chaCha20Poly1305DecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_aead_chacha20poly1305_decrypt_detached(
                null,
                ciphertext.toUInt8Array(),
                tag.toUInt8Array(),
                associatedData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            ).toUByteArray()
        } catch (error: Throwable) {
            throw AeadCorrupedOrTamperedDataException()
        }
    }

    actual fun xChaCha20Poly1305IetfKeygen(): UByteArray {
        return getSodium().crypto_aead_xchacha20poly1305_ietf_keygen().toUByteArray()
    }

    actual fun chaCha20Poly1305IetfKeygen(): UByteArray {
        return getSodium().crypto_aead_chacha20poly1305_ietf_keygen().toUByteArray()
    }

    actual fun chaCha20Poly1305Keygen(): UByteArray {
        return getSodium().crypto_aead_chacha20poly1305_keygen().toUByteArray()
    }

}
