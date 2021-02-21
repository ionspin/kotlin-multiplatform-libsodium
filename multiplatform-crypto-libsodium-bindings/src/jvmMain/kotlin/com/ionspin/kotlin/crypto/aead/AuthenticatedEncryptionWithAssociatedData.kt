package com.ionspin.kotlin.crypto.aead

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object AuthenticatedEncryptionWithAssociatedData {

    // Ietf

    // Original chacha20poly1305
    actual fun xChaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val ciphertext = UByteArray(message.size + crypto_aead_xchacha20poly1305_ietf_ABYTES)
        sodiumJna.crypto_aead_xchacha20poly1305_ietf_encrypt(
            ciphertext.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            null,
            nonce.asByteArray(),
            key.asByteArray(),
        )
        return ciphertext
    }

    actual fun xChaCha20Poly1305IetfDecrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertextAndTag.size - crypto_aead_xchacha20poly1305_ietf_ABYTES)
        val validationResult = sodiumJna.crypto_aead_xchacha20poly1305_ietf_decrypt(
            message.asByteArray(),
            null,
            null,
            ciphertextAndTag.asByteArray(),
            ciphertextAndTag.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray(),
        )
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
        val ciphertext = UByteArray(message.size)
        val authenticationTag = UByteArray(crypto_aead_xchacha20poly1305_ietf_ABYTES)
        sodiumJna.crypto_aead_xchacha20poly1305_ietf_encrypt_detached(
            ciphertext.asByteArray(),
            authenticationTag.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            null,
            nonce.asByteArray(),
            key.asByteArray(),
        )
        return AeadEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun xChaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size)
        val validationResult = sodiumJna.crypto_aead_xchacha20poly1305_ietf_decrypt_detached(
            message.asByteArray(),
            null,
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            tag.asByteArray(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray(),
        )
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
        val ciphertext = UByteArray(message.size + crypto_aead_chacha20poly1305_ietf_ABYTES)
        sodiumJna.crypto_aead_chacha20poly1305_ietf_encrypt(
            ciphertext.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            null,
            nonce.asByteArray(),
            key.asByteArray(),
        )
        return ciphertext
    }

    actual fun chaCha20Poly1305IetfDecrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertextAndTag.size - crypto_aead_chacha20poly1305_ietf_ABYTES)
        val validationResult = sodiumJna.crypto_aead_chacha20poly1305_ietf_decrypt(
            message.asByteArray(),
            null,
            null,
            ciphertextAndTag.asByteArray(),
            ciphertextAndTag.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray(),
        )
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
        val ciphertext = UByteArray(message.size)
        val authenticationTag = UByteArray(crypto_aead_chacha20poly1305_ietf_ABYTES)
        sodiumJna.crypto_aead_chacha20poly1305_ietf_encrypt_detached(
            ciphertext.asByteArray(),
            authenticationTag.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            null,
            nonce.asByteArray(),
            key.asByteArray(),
        )
        return AeadEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun chaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size)
        val validationResult = sodiumJna.crypto_aead_chacha20poly1305_ietf_decrypt_detached(
            message.asByteArray(),
            null,
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            tag.asByteArray(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray(),
        )
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
        val ciphertext = UByteArray(message.size + crypto_aead_chacha20poly1305_ABYTES)
        sodiumJna.crypto_aead_chacha20poly1305_encrypt(
            ciphertext.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            null,
            nonce.asByteArray(),
            key.asByteArray(),
        )
        return ciphertext
    }

    actual fun chaCha20Poly1305Decrypt(
        ciphertextAndTag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertextAndTag.size - crypto_aead_chacha20poly1305_ABYTES)
        val validationResult = sodiumJna.crypto_aead_chacha20poly1305_decrypt(
            message.asByteArray(),
            null,
            null,
            ciphertextAndTag.asByteArray(),
            ciphertextAndTag.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray(),
        )
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
        val ciphertext = UByteArray(message.size)
        val authenticationTag = UByteArray(crypto_aead_chacha20poly1305_ABYTES)
        sodiumJna.crypto_aead_chacha20poly1305_encrypt_detached(
            ciphertext.asByteArray(),
            authenticationTag.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            null,
            nonce.asByteArray(),
            key.asByteArray(),
        )
        return AeadEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun chaCha20Poly1305DecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size)
        val validationResult = sodiumJna.crypto_aead_chacha20poly1305_decrypt_detached(
            message.asByteArray(),
            null,
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            tag.asByteArray(),
            associatedData.asByteArray(),
            associatedData.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray(),
        )
        if (validationResult != 0) {
            throw AeadCorrupedOrTamperedDataException()
        }
        return message
    }

    actual fun xChaCha20Poly1305IetfKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_aead_xchacha20poly1305_ietf_KEYBYTES)
        sodiumJna.crypto_aead_xchacha20poly1305_ietf_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun chaCha20Poly1305IetfKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_aead_chacha20poly1305_ietf_KEYBYTES)
        sodiumJna.crypto_aead_chacha20poly1305_ietf_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun chaCha20Poly1305Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_aead_chacha20poly1305_KEYBYTES)
        sodiumJna.crypto_aead_chacha20poly1305_keygen(generatedKey.asByteArray())
        return generatedKey
    }

}
