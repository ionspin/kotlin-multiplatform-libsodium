package com.ionspin.kotlin.crypto.auth

actual object Auth {
    actual fun authKeygen(): UByteArray {
        TODO("not implemented yet")
    }

    actual fun auth(message: UByteArray, key: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun authVerify(mac: UByteArray, message: UByteArray, key: UByteArray): Boolean {
        TODO("not implemented yet")
    }

    actual fun authHmacSha256Keygen(): UByteArray {
        TODO("not implemented yet")
    }

    actual fun authHmacSha256(message: UByteArray, key: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun authHmacSha256Verify(
        mac: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        TODO("not implemented yet")
    }

    actual fun authHmacSha512Keygen(): UByteArray {
        TODO("not implemented yet")
    }

    actual fun authHmacSha512(message: UByteArray, key: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun authHmacSha512Verify(
        mac: UByteArray,
        message: UByteArray,
        key: UByteArray
    ): Boolean {
        TODO("not implemented yet")
    }

}
