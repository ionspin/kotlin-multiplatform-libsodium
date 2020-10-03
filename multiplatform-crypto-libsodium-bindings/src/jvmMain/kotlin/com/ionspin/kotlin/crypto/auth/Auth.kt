package com.ionspin.kotlin.crypto.auth

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodium

actual object Auth {
    actual fun authKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_KEYBYTES)
        sodium.crypto_auth_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun auth(message: UByteArray, key: UByteArray): UByteArray {
        val mac = UByteArray(crypto_auth_BYTES)
        sodium.crypto_auth(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        )
        return mac
    }

    actual fun authVerify(mac: UByteArray, message: UByteArray, key: UByteArray): Boolean {
        return sodium.crypto_auth_verify(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ) == 0
    }

    actual fun authHmacSha256Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_hmacsha256_KEYBYTES)
        sodium.crypto_auth_hmacsha256_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun authHmacSha256(message: UByteArray, key: UByteArray): UByteArray {
        val mac = UByteArray(crypto_auth_hmacsha256_BYTES)
        sodium.crypto_auth_hmacsha256(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        )
        return mac
    }

    actual fun authHmacSha256Verify(
        mac: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        return sodium.crypto_auth_hmacsha256_verify(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ) == 0
    }

    actual fun authHmacSha512Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_hmacsha512_KEYBYTES)
        sodium.crypto_auth_hmacsha512_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun authHmacSha512(message: UByteArray, key: UByteArray): UByteArray {
        val mac = UByteArray(crypto_auth_hmacsha512_BYTES)
        sodium.crypto_auth_hmacsha512(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        )
        return mac
    }

    actual fun authHmacSha512Verify(
        mac: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        return sodium.crypto_auth_hmacsha512_verify(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ) == 0
    }

}
