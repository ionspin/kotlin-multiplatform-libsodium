package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

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
actual class XChaCha20Poly1305Delegated actual constructor(key: UByteArray, nonce: UByteArray) {
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
                Uint8Array(0),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            )
            return encrypted.toUByteArray()
        }

        actual fun decrypt(
            key: UByteArray,
            nonce: UByteArray,
            ciphertext: UByteArray,
            additionalData: UByteArray
        ): UByteArray {
            val decrypted = getSodium().crypto_aead_xchacha20poly1305_ietf_decrypt(
                Uint8Array(0),
                ciphertext.toUInt8Array(),
                additionalData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            )
            return decrypted.toUByteArray()
        }
    }

    init {
//        val state =
    }

    internal actual constructor(
        key: UByteArray,
        nonce: UByteArray,
        testState: UByteArray,
        testHeader: UByteArray
    ) : this(key, nonce) {

    }

    actual fun encrypt(data: UByteArray, additionalData: UByteArray): UByteArray {
//        val encrypted
        TODO()
    }

    actual fun decrypt(data: UByteArray, additionalData: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

}
