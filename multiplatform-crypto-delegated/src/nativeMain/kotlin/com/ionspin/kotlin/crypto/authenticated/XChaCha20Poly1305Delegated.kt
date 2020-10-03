package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.bignum.integer.util.hexColumsPrint
import com.ionspin.kotlin.crypto.InvalidTagException
import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.*
import libsodium.*

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
            associatedData: UByteArray
        ): UByteArray {
            val ciphertextLength = message.size + crypto_aead_xchacha20poly1305_IETF_ABYTES.toInt()
            val ciphertext = UByteArray(ciphertextLength)
            val ciphertextPinned = ciphertext.pin()
            crypto_aead_xchacha20poly1305_ietf_encrypt(
                ciphertextPinned.toPtr(),
                ulongArrayOf(ciphertextLength.convert()).toCValues(),
                message.toCValues(),
                message.size.convert(),
                associatedData.toCValues(),
                associatedData.size.convert(),
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
            associatedData: UByteArray
        ): UByteArray {
            val messageLength = ciphertext.size - crypto_aead_xchacha20poly1305_IETF_ABYTES.toInt()
            val message = UByteArray(messageLength)
            val messagePinned = message.pin()
            crypto_aead_xchacha20poly1305_ietf_decrypt(
                messagePinned.toPtr(),
                ulongArrayOf(messageLength.convert()).toCValues(),
                null,
                ciphertext.toCValues(),
                ciphertext.size.convert(),
                associatedData.toCValues(),
                associatedData.size.convert(),
                nonce.toCValues(),
                key.toCValues()
            )
            messagePinned.unpin()
            return message
        }
    }

    var state =
        sodium_malloc(crypto_secretstream_xchacha20poly1305_state.size.convert())!!
            .reinterpret<crypto_secretstream_xchacha20poly1305_state>()
            .pointed
    val header = UByteArray(crypto_secretstream_xchacha20poly1305_HEADERBYTES.toInt()) { 0U }

    var isInitialized = false
    var isEncryptor = false

    actual internal constructor(
        key: UByteArray,
        testState: UByteArray,
        testHeader: UByteArray,
        isDecryptor: Boolean
    ) : this() {
        val pointer = state.ptr.reinterpret<UByteVar>()
        for (i in 0 until crypto_secretstream_xchacha20poly1305_state.size.toInt()) {
            pointer[i] = testState[i]
        }
        println("state after setting-----------")
        state.ptr.readBytes(crypto_secretstream_xchacha20poly1305_state.size.toInt()).asUByteArray().hexColumsPrint()
        println("state after setting-----------")
        println("header after setting-----------")
        testHeader.copyInto(header)
        header.hexColumsPrint()
        println("header after setting-----------")
        isInitialized = true
        isEncryptor = !isDecryptor
    }



    actual fun initializeForEncryption(key: UByteArray) : UByteArray {
        val pinnedHeader = header.pin()
        crypto_secretstream_xchacha20poly1305_init_push(state.ptr, pinnedHeader.toPtr(), key.toCValues())
        println("state-----------")
        state.ptr.readBytes(crypto_secretstream_xchacha20poly1305_state.size.toInt()).asUByteArray().hexColumsPrint()
        println("state-----------")
        println("--------header-----------")
        header.hexColumsPrint()
        println("--------header-----------")
        pinnedHeader.unpin()
        return header
    }

    actual fun initializeForDecryption(key: UByteArray, header: UByteArray) {
        crypto_secretstream_xchacha20poly1305_init_pull(state.ptr, header.toCValues(), key.toCValues())
    }


    actual fun encrypt(data: UByteArray, associatedData: UByteArray): UByteArray {
        val ciphertextWithTag = UByteArray(data.size + crypto_secretstream_xchacha20poly1305_ABYTES.toInt())
        val ciphertextWithTagPinned = ciphertextWithTag.pin()
        crypto_secretstream_xchacha20poly1305_push(
            state.ptr,
            ciphertextWithTagPinned.toPtr(),
            null,
            data.toCValues(),
            data.size.convert(),
            associatedData.toCValues(),
            associatedData.size.convert(),
            0U,
        )
        println("Encrypt partial")
        ciphertextWithTag.hexColumsPrint()
        println("Encrypt partial end")
        ciphertextWithTagPinned.unpin()
        return ciphertextWithTag
    }

    actual fun decrypt(data: UByteArray, associatedData: UByteArray): UByteArray {
        val plaintext = UByteArray(data.size - crypto_secretstream_xchacha20poly1305_ABYTES.toInt())
        val plaintextPinned = plaintext.pin()
        val validTag = crypto_secretstream_xchacha20poly1305_pull(
            state.ptr,
            plaintextPinned.toPtr(),
            null,
            null,
            data.toCValues(),
            data.size.convert(),
            associatedData.toCValues(),
            associatedData.size.convert()
        )
        plaintextPinned.unpin()
        println("tag: $validTag")
        if (validTag != 0) {
            println("Tag validation failed")
            throw InvalidTagException()
        }
        return plaintext
    }

    actual fun cleanup() {
        sodium_free(state.ptr)
    }




}
