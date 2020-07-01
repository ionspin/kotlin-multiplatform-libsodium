package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.bignum.integer.util.hexColumsPrint
import com.ionspin.kotlin.crypto.util.toLittleEndianUByteArray
import kotlinx.cinterop.*
import libsodium.*
import platform.posix.malloc
import platform.posix.memset

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
actual class XChaCha20Poly1305Delegated actual constructor(val key: UByteArray,val additionalData: UByteArray) {
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


    actual internal constructor(key: UByteArray, additionalData: UByteArray, testState : UByteArray, testHeader: UByteArray) : this(key, additionalData) {
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

    var state =
        malloc(crypto_secretstream_xchacha20poly1305_state.size.convert())!!
            .reinterpret<crypto_secretstream_xchacha20poly1305_state>()
            .pointed

    val header = UByteArray(crypto_secretstream_xchacha20poly1305_HEADERBYTES.toInt()) { 0U }

    init {
        val pinnedHeader = header.pin()
        crypto_secretstream_xchacha20poly1305_init_push(state.ptr, pinnedHeader.addressOf(0), key.toCValues())
        println("state-----------")
        state.ptr.readBytes(crypto_secretstream_xchacha20poly1305_state.size.toInt()).toUByteArray().hexColumsPrint()
        println("state-----------")
        println("--------header-----------")
        header.hexColumsPrint()
        println("--------header-----------")

    }

    actual fun encryptPartialData(data: UByteArray): UByteArray {
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

    actual fun verifyPartialData(data: UByteArray) {
        val ciphertextWithTag = UByteArray(data.size + crypto_secretstream_xchacha20poly1305_ABYTES.toInt())
        val ciphertextWithTagPinned = ciphertextWithTag.pin()
        val blockUB = UByteArray(64) { 0U }
        val slenUB = UByteArray(8) { 0U }
        val block = blockUB.pin().addressOf(0)
        val slen = slenUB.pin().addressOf(0)

        var poly1305_state =
            malloc(crypto_onetimeauth_state.size.convert())!!
                .reinterpret<crypto_onetimeauth_state>()
                .pointed
        val key = state.ptr.readBytes(32).toUByteArray()
        val nonce = state.ptr.readBytes(44).sliceArray(32 until 44).toUByteArray()
        println("--block")
        blockUB.hexColumsPrint()
        println("--block")
        println("--key")
        key.hexColumsPrint()
        println("--key")
        println("--nonce")
        nonce.hexColumsPrint()
        println("--nonce")
        println("--state before")
        state.ptr.readBytes(52).toUByteArray().hexColumsPrint()
        println("--state before")
        crypto_stream_chacha20_ietf(block, 64, state.ptr.readBytes(44).sliceArray(32 until 44).toUByteArray().toCValues(), state.ptr.readBytes(32).toUByteArray().toCValues());
        println("--state after")
        state.ptr.readBytes(52).toUByteArray().hexColumsPrint()
        println("--state after")
        println("--block")
        blockUB.hexColumsPrint()
        println("--block")


        crypto_onetimeauth_poly1305_init(poly1305_state.ptr, block);
        memset(block, 0, 64);
        block[0] = 0U


        crypto_stream_chacha20_ietf_xor_ic(block, block, 64,
            state.ptr.readBytes(44).sliceArray(32 until 44).toUByteArray().toCValues(), 1U, state.ptr.readBytes(32).toUByteArray().toCValues());
        crypto_onetimeauth_poly1305_update(poly1305_state.ptr, block, 64);

        //Poly result
        val polyResult = UByteArray(16)
        val polyResultPin = polyResult.pin()
        val cipherText = UByteArray(data.size)
        val ciphertextPinned = cipherText.pin()

        crypto_stream_chacha20_ietf_xor_ic(ciphertextPinned.addressOf(0), data.toCValues(), data.size.convert(),
            state.ptr.readBytes(44).sliceArray(32 until 44).toUByteArray().toCValues(), 2U, state.ptr.readBytes(32).toUByteArray().toCValues());
        val paddedCipherText = cipherText + UByteArray(16 - ((data.size) % 16)) { 0U }
        val paddedCipherTextPinned = paddedCipherText.pin()
        println("paddedCipherText--")
        paddedCipherText.hexColumsPrint()
        println("paddedCipherText--")
        crypto_onetimeauth_poly1305_update(poly1305_state.ptr, paddedCipherTextPinned.addressOf(0), (data.size + (16 - ((data.size) % 16))).convert());



        val finalMac = additionalData.size.toULong().toLittleEndianUByteArray() + (data.size + 64).toULong().toLittleEndianUByteArray()
        crypto_onetimeauth_poly1305_update(poly1305_state.ptr, finalMac.pin().addressOf(0), 16);
        crypto_onetimeauth_poly1305_final(poly1305_state.ptr, polyResultPin.addressOf(0));
        println("-- poly 1")
        polyResult.hexColumsPrint()
        println("-- poly 1")
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
