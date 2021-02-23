package com.ionspin.kotlin.crypto.stream

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object Stream {
    actual fun chacha20(clen: Int, nonce: UByteArray, key: UByteArray): UByteArray {
        val result = UByteArray(clen)

        sodiumJna.crypto_stream_chacha20(result.asByteArray(), clen.toLong(), nonce.asByteArray(), key.asByteArray())

        return result
    }

    actual fun chacha20IetfXor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)

        sodiumJna.crypto_stream_chacha20_ietf_xor(
            result.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray()
        )

        return result
    }

    actual fun chacha20IetfXorIc(
        message: UByteArray,
        nonce: UByteArray,
        initialCounter: ULong,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)

        sodiumJna.crypto_stream_chacha20_ietf_xor_ic(
            result.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            initialCounter.toLong(),
            key.asByteArray()
        )

        return result
    }

    actual fun chacha20Keygen(): UByteArray {
        val result = UByteArray(crypto_stream_chacha20_KEYBYTES)

        sodiumJna.crypto_stream_chacha20_keygen(result.asByteArray())

        return result
    }

    actual fun chacha20Xor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)

        sodiumJna.crypto_stream_chacha20_xor(
            result.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            key.asByteArray()
        )

        return result
    }

    actual fun chacha20XorIc(
        message: UByteArray,
        nonce: UByteArray,
        initialCounter: ULong,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)

        sodiumJna.crypto_stream_chacha20_xor_ic(
            result.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            initialCounter.toLong(),
            key.asByteArray()
        )


        return result
    }

//    actual fun xChacha20Keygen(): UByteArray {
//        val result = UByteArray(crypto_stream_chacha20_KEYBYTES)
//
//        sodiumJna.crypto_stream_xchacha20_keygen(result.asByteArray())
//
//        return result
//    }
//
//    actual fun xChacha20Xor(
//        message: UByteArray,
//        nonce: UByteArray,
//        key: UByteArray
//    ): UByteArray {
//        val result = UByteArray(message.size)
//
//        sodiumJna.crypto_stream_xchacha20_xor(
//            result.asByteArray(),
//            message.asByteArray(),
//            message.size.toLong(),
//            nonce.asByteArray(),
//            key.asByteArray()
//        )
//
//        return result
//    }
//
//    actual fun xChacha20XorIc(
//        message: UByteArray,
//        nonce: UByteArray,
//        initialCounter: ULong,
//        key: UByteArray
//    ): UByteArray {
//        val result = UByteArray(message.size)
//
//        sodiumJna.crypto_stream_xchacha20_xor_ic(
//            result.asByteArray(),
//            message.asByteArray(),
//            message.size.toLong(),
//            nonce.asByteArray(),
//            initialCounter.toLong(),
//            key.asByteArray()
//        )
//
//        return result
//    }
}
