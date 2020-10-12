package com.ionspin.kotlin.crypto.stream

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodium

actual object Stream {
    actual fun chacha20(clen: Int, nonce: UByteArray, key: UByteArray): UByteArray {
        val result = UByteArray(clen)

        sodium.crypto_stream_chacha20(result.asByteArray(), clen.toLong(), nonce.asByteArray(), key.asByteArray())

        return result
    }

    actual fun chacha20IetfXor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)

        sodium.crypto_stream_chacha20_ietf_xor(
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

        sodium.crypto_stream_chacha20_ietf_xor_ic(
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

        sodium.crypto_stream_chacha20_keygen(result.asByteArray())

        return result
    }

    actual fun chacha20Xor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)

        sodium.crypto_stream_chacha20_xor(
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

        sodium.crypto_stream_chacha20_xor_ic(
            result.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            initialCounter.toLong(),
            key.asByteArray()
        )


        return result
    }
}
