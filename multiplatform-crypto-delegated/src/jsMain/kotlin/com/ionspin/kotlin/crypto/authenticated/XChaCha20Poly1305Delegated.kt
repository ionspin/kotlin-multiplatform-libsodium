package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-Jun-2020
 */

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
            val encrypted = getSodium().crypto_aead_xchacha20poly1305_ietf_encrypt(
                message.toUInt8Array(),
                additionalData.toUInt8Array(),
                key.toUInt8Array(),
                nonce.toUInt8Array()
            )
            return encrypted.toUByteArray()
        }

        actual fun decrypt(
            key: UByteArray,
            nonce: UByteArray,
            ciphertext: UByteArray,
            additionalData: UByteArray
        ): UByteArray {
            TODO("not implemented yet")
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
