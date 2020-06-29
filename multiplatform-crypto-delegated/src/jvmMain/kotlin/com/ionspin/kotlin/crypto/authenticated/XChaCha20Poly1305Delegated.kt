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
            val message = ByteArray(ciphertext.size - 16)
            SodiumJava().crypto_aead_xchacha20poly1305_ietf_decrypt(
                message,
                longArrayOf(ciphertext.size.toLong()),
                null,
                ciphertext.toByteArray(),
                ciphertext.size.toLong(),
                additionalData.toByteArray(),
                additionalData.size.toLong(),
                nonce.toByteArray(),
                key.toByteArray()

            )
            return message.toUByteArray()
        }
    }

    internal actual constructor(key: UByteArray, additionalData: UByteArray, testState : UByteArray, testHeader: UByteArray) {

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
