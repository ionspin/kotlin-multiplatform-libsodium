package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.bignum.integer.util.hexColumsPrint
import kotlinx.cinterop.*
import libsodium.*
import platform.posix.malloc

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
actual class XChaCha20Poly1305Delegated internal actual constructor() {
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
                ulongArrayOf(ciphertextLength.convert()).toCValues(),
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
            val messageLength = ciphertext.size - crypto_aead_xchacha20poly1305_IETF_ABYTES.toInt()
            val message = UByteArray(messageLength)
            val messagePinned = message.pin()
            crypto_aead_xchacha20poly1305_ietf_decrypt(
                messagePinned.addressOf(0),
                ulongArrayOf(messageLength.convert()).toCValues(),
                null,
                ciphertext.toCValues(),
                ciphertext.size.convert(),
                additionalData.toCValues(),
                additionalData.size.convert(),
                nonce.toCValues(),
                key.toCValues()
            )
            messagePinned.unpin()
            return message
        }
    }

    var state =
        malloc(crypto_secretstream_xchacha20poly1305_state.size.convert())!!
            .reinterpret<crypto_secretstream_xchacha20poly1305_state>()
            .pointed

    val header = UByteArray(crypto_secretstream_xchacha20poly1305_HEADERBYTES.toInt()) { 0U }

    actual internal constructor(
        key: UByteArray,
        testState: UByteArray,
        testHeader: UByteArray
    ) : this() {
        val pointer = state.ptr.reinterpret<UByteVar>()
        for (i in 0 until crypto_secretstream_xchacha20poly1305_state.size.toInt()) {
            pointer[i] = testState[i]
        }
        println("state after setting-----------")
        state.ptr.readBytes(crypto_secretstream_xchacha20poly1305_state.size.toInt()).toUByteArray().hexColumsPrint()
        println("state after setting-----------")
        println("header after setting-----------")
        testHeader.copyInto(header)
        header.hexColumsPrint()
        println("header after setting-----------")
    }



    actual fun initializeForEncryption(key: UByteArray) : UByteArray {
        val pinnedHeader = header.pin()
        crypto_secretstream_xchacha20poly1305_init_push(state.ptr, pinnedHeader.addressOf(0), key.toCValues())
        println("state-----------")
        state.ptr.readBytes(crypto_secretstream_xchacha20poly1305_state.size.toInt()).toUByteArray().hexColumsPrint()
        println("state-----------")
        println("--------header-----------")
        header.hexColumsPrint()
        println("--------header-----------")
        pinnedHeader.unpin()
        return header
    }

    actual fun initializeForDecryption(key: UByteArray, header: UByteArray) {

    }


    actual fun encrypt(data: UByteArray, additionalData: UByteArray): UByteArray {
        val ciphertextWithTag = UByteArray(data.size + crypto_secretstream_xchacha20poly1305_ABYTES.toInt())
        val ciphertextWithTagPinned = ciphertextWithTag.pin()
        crypto_secretstream_xchacha20poly1305_push(
            state.ptr,
            ciphertextWithTagPinned.addressOf(0),
            null,
            data.toCValues(),
            data.size.convert(),
            null,
            0U,
            0U,
        )
        println("Encrypt partial")
        ciphertextWithTag.hexColumsPrint()
        println("Encrypt partial end")
        ciphertextWithTagPinned.unpin()
        return ciphertextWithTag
    }

    actual fun decrypt(data: UByteArray, additionalData: UByteArray): UByteArray {
        TODO("not implemented yet")
    }




}
