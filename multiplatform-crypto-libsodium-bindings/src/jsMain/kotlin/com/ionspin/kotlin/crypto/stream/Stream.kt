package com.ionspin.kotlin.crypto.stream

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

actual object Stream {
    actual fun chacha20(clen: Int, nonce: UByteArray, key: UByteArray): UByteArray {
        //Note, unlike the other ones, here the positions of key and nonce are reversed.
        val result = getSodium().crypto_stream_chacha20(clen.toUInt(), key.toUInt8Array(), nonce.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun chacha20IetfXor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = getSodium().crypto_stream_chacha20_ietf_xor(
            message.toUInt8Array(),
            nonce.toUInt8Array(),
            key.toUInt8Array()
        )

        return result.toUByteArray()
    }

    actual fun chacha20IetfXorIc(
        message: UByteArray,
        nonce: UByteArray,
        initialCounter: ULong,
        key: UByteArray
    ): UByteArray {

        val result = getSodium().crypto_stream_chacha20_ietf_xor_ic(
            message.toUInt8Array(),
            nonce.toUInt8Array(),
            initialCounter.toUInt(),
            key.toUInt8Array()
        )

        return result.toUByteArray()
    }

    actual fun chacha20Keygen(): UByteArray {
        val result = getSodium().crypto_stream_chacha20_keygen()

        return result.toUByteArray()
    }

    actual fun chacha20Xor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = getSodium().crypto_stream_chacha20_xor(
            message.toUInt8Array(),
            nonce.toUInt8Array(),
            key.toUInt8Array()
        )

        return result.toUByteArray()
    }

    actual fun chacha20XorIc(
        message: UByteArray,
        nonce: UByteArray,
        initialCounter: ULong,
        key: UByteArray
    ): UByteArray {
        val result = getSodium().crypto_stream_chacha20_xor_ic(
            message.toUInt8Array(),
            nonce.toUInt8Array(),
            initialCounter.toUInt(),
            key.toUInt8Array()
        )


        return result.toUByteArray()
    }

//    actual fun xChacha20Keygen(): UByteArray {
//        val result = getSodium().crypto_stream_xchacha20_keygen()
//
//        return result.toUByteArray()
//    }
//
//    actual fun xChacha20Xor(
//        message: UByteArray,
//        nonce: UByteArray,
//        key: UByteArray
//    ): UByteArray {
//        val result = getSodium().crypto_stream_xchacha20_xor(
//            message.toUInt8Array(),
//            nonce.toUInt8Array(),
//            key.toUInt8Array()
//        )
//
//        return result.toUByteArray()
//    }
//
//    actual fun xChacha20XorIc(
//        message: UByteArray,
//        nonce: UByteArray,
//        initialCounter: ULong,
//        key: UByteArray
//    ): UByteArray {
//        val result = getSodium().crypto_stream_xchacha20_xor_ic(
//            message.toUInt8Array(),
//            nonce.toUInt8Array(),
//            initialCounter.toUInt(),
//            key.toUInt8Array()
//        )
//
//        return result.toUByteArray()
//    }


}
