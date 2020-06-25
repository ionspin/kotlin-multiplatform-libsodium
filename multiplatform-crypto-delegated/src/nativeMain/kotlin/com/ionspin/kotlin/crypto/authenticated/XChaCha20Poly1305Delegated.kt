package com.ionspin.kotlin.crypto.authenticated

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.toCValues
import libsodium.crypto_aead_xchacha20poly1305_IETF_ABYTES
import libsodium.crypto_aead_xchacha20poly1305_ietf_encrypt

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
            val ciphertextLength = message.size + crypto_aead_xchacha20poly1305_IETF_ABYTES.toInt()
            val ciphertext = UByteArray(ciphertextLength)
            val ciphertextPinned = ciphertext.pin()
            crypto_aead_xchacha20poly1305_ietf_encrypt(
                ciphertextPinned.addressOf(0),
                ciphertextLength.convert(),
                message.toCValues(),
                message.size.convert(),
                additionalData.toCValues(),
                additionalData.size.convert(),
                null,
                nonce.toCValues(),
                key.toCValues()
            )
            ciphertextPinned.unpin()
            return ciphertext
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
