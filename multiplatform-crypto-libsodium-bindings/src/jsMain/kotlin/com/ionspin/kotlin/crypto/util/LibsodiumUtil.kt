package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

actual object LibsodiumUtil {
    actual fun memcmp(first: UByteArray, second: UByteArray): Boolean {
        return getSodium().memcmp(first.toUInt8Array(), second.toUInt8Array())
    }

    actual fun memzero(target: UByteArray) {
        // libsodium.js does this as well, and theres no clear way at the moment of casting ubytearray to uint8array
        // although I feel like there should be a way to work around it
        (target.indices).forEach {
            index -> target[index] = 0U
        }
    }

    actual fun pad(unpaddedData: UByteArray, blocksize: Int): UByteArray {
        return getSodium().pad(unpaddedData.toUInt8Array(), blocksize).toUByteArray()
    }

    actual fun unpad(paddedData: UByteArray, blocksize: Int): UByteArray {
        return getSodium().unpad(paddedData.toUInt8Array(), blocksize).toUByteArray()
    }

    actual fun toBase64(
        data: UByteArray,
        variant: Base64Variants
    ): String {
        return getSodium().to_base64(data.toUInt8Array(), variant.value)
    }

    actual fun toHex(data: UByteArray): String {
        return getSodium().to_hex(data.toUInt8Array())
    }

    actual fun fromBase64(
        data: String,
        variant: Base64Variants
    ): UByteArray {
        return getSodium().from_base64(data, variant.value).toUByteArray()
    }

    actual fun fromHex(data: String): UByteArray {
        return getSodium().from_hex(data).toUByteArray()
    }

}
