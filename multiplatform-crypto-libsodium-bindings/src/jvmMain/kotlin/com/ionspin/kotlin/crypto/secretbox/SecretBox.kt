package com.ionspin.kotlin.crypto.secretbox

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object SecretBox {
    actual fun easy(message: UByteArray, nonce: UByteArray, key: UByteArray): UByteArray {
        val ciphertext = UByteArray(message.size + crypto_secretbox_MACBYTES)
        sodiumJna.crypto_secretbox_easy(
            ciphertext.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray()
        )
        return ciphertext
    }

    actual fun openEasy(
        ciphertext: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val decrypted = UByteArray(ciphertext.size - crypto_secretbox_MACBYTES)
        val validationResult = sodiumJna.crypto_secretbox_open_easy(
            decrypted.asByteArray(),
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray()
            )
        if (validationResult != 0) {
            throw SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey()
        }
        return decrypted
    }

    actual fun detached(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): SecretBoxEncryptedDataAndTag {
        val ciphertext = UByteArray(message.size)
        val authenticationTag = UByteArray(crypto_secretbox_MACBYTES)
        sodiumJna.crypto_secretbox_detached(
            ciphertext.asByteArray(),
            authenticationTag.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray()
        )
        return SecretBoxEncryptedDataAndTag(ciphertext, authenticationTag)
    }

    actual fun openDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size)
        val validationResult = sodiumJna.crypto_secretbox_open_detached(
            message.asByteArray(),
            ciphertext.asByteArray(),
            tag.asByteArray(),
            ciphertext.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray()
        )
        if (validationResult != 0) {
            throw SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey()
        }
        return message
    }

    actual fun keygen() : UByteArray {
        val generatedKey = UByteArray(crypto_secretbox_KEYBYTES)
        sodiumJna.crypto_secretbox_keygen(generatedKey.asByteArray())
        return generatedKey
    }

}
