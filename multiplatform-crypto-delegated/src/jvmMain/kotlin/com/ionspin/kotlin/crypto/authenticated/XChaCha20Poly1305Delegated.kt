package com.ionspin.kotlin.crypto.authenticated

import com.goterl.lazycode.lazysodium.SodiumJava
import com.ionspin.kotlin.crypto.Initializer.sodium

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
actual class XChaCha20Poly1305Delegated actual constructor(key: UByteArray, additionalData: UByteArray) {
    actual companion object {
        actual fun encrypt(
            key: UByteArray,
            nonce: UByteArray,
            message: UByteArray,
            additionalData: UByteArray
        ): UByteArray {
            val ciphertext = ByteArray(message.size + 16)
            SodiumJava().crypto_aead_xchacha20poly1305_ietf_encrypt(
                ciphertext,
                longArrayOf(ciphertext.size.toLong()),
                message.toByteArray(),
                message.size.toLong(),
                additionalData.toByteArray(),
                additionalData.size.toLong(),
                null,
                nonce.toByteArray(),
                key.toByteArray()

            )
            return ciphertext.toUByteArray()
        }

        actual fun decrypt(
            key: UByteArray,
            nonce: UByteArray,
            ciphertext: UByteArray,
            additionalData: UByteArray
        ): UByteArray {
            val message = ByteArray(ciphertext.size - sodium.crypto_secretstream_xchacha20poly1305_abytes())
            SodiumJava().crypto_aead_xchacha20poly1305_ietf_encrypt(

                message,
                longArrayOf(ciphertext.size.toLong()),
                ciphertext.toByteArray(),
                ciphertext.size.toLong(),
                additionalData.toByteArray(),
                additionalData.size.toLong(),
                null,
                nonce.toByteArray(),
                key.toByteArray()

            )
            return message.toUByteArray()
        }
    }

    actual fun encryptPartialData(data: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun verifyPartialData(data: UByteArray) {
    }

    actual fun checkTag(expectedTag: UByteArray) {
    }

    actual fun decrypt(data: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun finishEncryption(): Pair<UByteArray, UByteArray> {
        TODO("not implemented yet")
    }

}
