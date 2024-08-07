package com.ionspin.kotlin.crypto.auth

import com.ionspin.kotlin.crypto.GeneralLibsodiumException.Companion.ensureLibsodiumSuccess
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna
import com.ionspin.kotlin.crypto.util.isLibsodiumSuccessCode

actual object Auth {
    actual fun authKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_KEYBYTES)
        sodiumJna.crypto_auth_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun auth(message: UByteArray, key: UByteArray): UByteArray {
        val mac = UByteArray(crypto_auth_BYTES)
        sodiumJna.crypto_auth(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ).ensureLibsodiumSuccess()
        return mac
    }

    actual fun authVerify(tag: UByteArray, message: UByteArray, key: UByteArray): Boolean {
        return sodiumJna.crypto_auth_verify(
            tag.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ).isLibsodiumSuccessCode()
    }

    actual fun authHmacSha256Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_hmacsha256_KEYBYTES)
        sodiumJna.crypto_auth_hmacsha256_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun authHmacSha256(message: UByteArray, key: UByteArray): UByteArray {
        val mac = UByteArray(crypto_auth_hmacsha256_BYTES)
        sodiumJna.crypto_auth_hmacsha256(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ).ensureLibsodiumSuccess()
        return mac
    }

    actual fun authHmacSha256Verify(
        tag: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        return sodiumJna.crypto_auth_hmacsha256_verify(
            tag.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ).isLibsodiumSuccessCode()
    }

    actual fun authHmacSha512Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_hmacsha512_KEYBYTES)
        sodiumJna.crypto_auth_hmacsha512_keygen(generatedKey.asByteArray())
        return generatedKey
    }

    actual fun authHmacSha512(message: UByteArray, key: UByteArray): UByteArray {
        val mac = UByteArray(crypto_auth_hmacsha512_BYTES)
        sodiumJna.crypto_auth_hmacsha512(
            mac.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ).ensureLibsodiumSuccess()
        return mac
    }

    actual fun authHmacSha512Verify(
        tag: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        return sodiumJna.crypto_auth_hmacsha512_verify(
            tag.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            key.asByteArray()
        ).isLibsodiumSuccessCode()
    }

}
