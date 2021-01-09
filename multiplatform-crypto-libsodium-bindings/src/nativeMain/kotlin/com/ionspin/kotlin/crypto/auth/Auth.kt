package com.ionspin.kotlin.crypto.auth

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_auth
import libsodium.crypto_auth_hmacsha256
import libsodium.crypto_auth_hmacsha256_keygen
import libsodium.crypto_auth_hmacsha256_verify
import libsodium.crypto_auth_hmacsha512
import libsodium.crypto_auth_hmacsha512_keygen
import libsodium.crypto_auth_hmacsha512_verify
import libsodium.crypto_auth_keygen
import libsodium.crypto_auth_verify

actual object Auth {
    actual fun authKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_auth_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

    actual fun auth(message: UByteArray, key: UByteArray): UByteArray {
        val messagePinned = message.pin()
        val keyPinned = key.pin()

        val mac = UByteArray(crypto_auth_BYTES)
        val macPinned = mac.pin()

        crypto_auth(
            macPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            keyPinned.toPtr()
        )

        macPinned.unpin()
        messagePinned.unpin()
        keyPinned.unpin()

        return mac
    }

    actual fun authVerify(tag: UByteArray, message: UByteArray, key: UByteArray): Boolean {
        val macPinned = tag.pin()
        val messagePinned = message.pin()
        val keyPinned = key.pin()
        val verify = crypto_auth_verify(
            macPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            keyPinned.toPtr(),
        )

        keyPinned.unpin()
        messagePinned.unpin()
        macPinned.unpin()
        return verify == 0
    }

    actual fun authHmacSha256Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_hmacsha256_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_auth_hmacsha256_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

    actual fun authHmacSha256(message: UByteArray, key: UByteArray): UByteArray {
        val messagePinned = message.pin()
        val keyPinned = key.pin()

        val mac = UByteArray(crypto_auth_hmacsha256_BYTES)
        val macPinned = mac.pin()

        crypto_auth_hmacsha256(
            macPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            keyPinned.toPtr()
        )

        macPinned.unpin()
        messagePinned.unpin()
        keyPinned.unpin()

        return mac
    }

    actual fun authHmacSha256Verify(
        tag: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        val macPinned = tag.pin()
        val messagePinned = message.pin()
        val keyPinned = key.pin()

        val verify = crypto_auth_hmacsha256_verify(
            macPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            keyPinned.toPtr(),
        )

        keyPinned.unpin()
        messagePinned.unpin()
        macPinned.unpin()
        return verify == 0
    }

    actual fun authHmacSha512Keygen(): UByteArray {
        val generatedKey = UByteArray(crypto_auth_hmacsha512_KEYBYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_auth_hmacsha512_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }

    actual fun authHmacSha512(message: UByteArray, key: UByteArray): UByteArray {
        val messagePinned = message.pin()
        val keyPinned = key.pin()

        val mac = UByteArray(crypto_auth_hmacsha512_BYTES)
        val macPinned = mac.pin()

        crypto_auth_hmacsha512(
            macPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            keyPinned.toPtr()
        )

        macPinned.unpin()
        messagePinned.unpin()
        keyPinned.unpin()

        return mac
    }

    actual fun authHmacSha512Verify(
        tag: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        val macPinned = tag.pin()
        val messagePinned = message.pin()
        val keyPinned = key.pin()

        val verify = crypto_auth_hmacsha512_verify(
            macPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            keyPinned.toPtr(),
        )

        keyPinned.unpin()
        messagePinned.unpin()
        macPinned.unpin()
        return verify == 0
    }

}
