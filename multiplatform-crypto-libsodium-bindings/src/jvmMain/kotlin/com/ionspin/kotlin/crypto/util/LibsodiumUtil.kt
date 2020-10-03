package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodium
import java.lang.RuntimeException
import java.util.*

actual object LibsodiumUtil {
    actual fun memcmp(first: UByteArray, second: UByteArray): Boolean {
        if (first.size != second.size) {
            throw RuntimeException("Sodium memcmp() only supports comparing same length arrays")
        }
        return sodium.sodium_memcmp(first.asByteArray(), second.asByteArray(), first.size) == 0
    }

    actual fun memzero(target: UByteArray) {
        sodium.sodium_memzero(target.asByteArray(), target.size)
    }

    actual fun pad(unpaddedData: UByteArray, blocksize: Int): UByteArray {

        // Pad is invalid in lazysodium-java because it uses char arrays, which are 2 bytes in java
        // while libsodium expects 1 byte char array
        // See https://github.com/terl/lazysodium-java/issues/85

        //This is temporary solution until lazysodium is fixed
        if (blocksize == 0) {
            throw RuntimeException("Invalid block size: $blocksize")
        }
        val padAmount = blocksize - (unpaddedData.size % blocksize)
        val result = if (padAmount == blocksize) {
            unpaddedData + UByteArray(blocksize) {
                when (it) {
                    0 -> 0x80U
                    else -> 0x00U
                }
            }
        } else {
            unpaddedData + UByteArray(padAmount) {
                when (it) {
                    0 -> 0x80U
                    else -> 0x00U
                }
            }
        }

        return result

//        val newPadSizeReference = IntByReference(0)
//        val newSize = ((unpaddedData.size / blocksize) + 1) * blocksize
//        val charArray = CharArray(newSize) {
//            if (it < unpaddedData.size) {
//                unpaddedData[it].toByte().toChar()
//            } else {
//                '\u0000'
//            }
//        }
//        sodium.sodium_pad(
//            newPadSizeReference.pointer,
//            charArray,
//            unpaddedData.size + 1,
//            blocksize,
//            newSize
//        )
//
//        return charArray.slice(0 until newPadSizeReference.value).map { it.toByte().toUByte()}.toUByteArray()

    }

    actual fun unpad(paddedData: UByteArray, blocksize: Int): UByteArray {

        // Pad is invalid in lazysodium-java because it uses char arrays, which are 2 bytes in java
        // while libsodium expects 1 byte char array
        // See https://github.com/terl/lazysodium-java/issues/85

        //This is temporary solution until lazysodium is fixed

        val unpaddedData = paddedData.dropLastWhile { it == 0U.toUByte() }
        if (unpaddedData.last() != 0x80U.toUByte()) {
            throw RuntimeException("Invalid padding!")
        }
        return unpaddedData.dropLast(1).toUByteArray()


//        val paddedDataCopy = paddedData.copyOf().asByteArray().map { it.toChar() }.toCharArray()
//        var unpaddedSize =  IntByReference(0)
//
//        sodium.sodium_unpad(
//            unpaddedSize.pointer,
//            paddedDataCopy,
//            paddedData.size,
//            blocksize
//        )
//
//        val unpadded = paddedDataCopy.sliceArray(0 until unpaddedSize.value).map { it.toByte().toUByte() }.toUByteArray()
//
//        return unpadded
    }

    actual fun toBase64(
        data: UByteArray,
        variant: Base64Variants
    ): String {
        val maxlen = sodium.sodium_base64_encoded_len(data.size, variant.value)
        val result = ByteArray(maxlen) { 0 }
        sodium.sodium_bin2base64(
            result,
            maxlen,
            data.asByteArray(),
            data.size,
            variant.value
        )
        //Drop terminating char \0
        return String(result.sliceArray(0 until result.size - 1))
    }

    actual fun toHex(data: UByteArray): String {
        val hexLen = (data.size * 2) + 1 // +1 for terminator char
        val result = ByteArray(hexLen)
        sodium.sodium_bin2hex(
            result,
            hexLen,
            data.asByteArray(),
            data.size
        )
        //Drop terminating char \0
        return String(result.sliceArray(0 until result.size - 1))
    }

    actual fun fromBase64(
        data: String,
        variant: Base64Variants
    ): UByteArray {
        // from base64 is currently broken in lazysodium-java
        // see https://github.com/terl/lazysodium-java/issues/83
//        val maxLength = (data.length * 3) / 4
//        val intermediaryResult = UByteArray(maxLength) { 0U }
//
//        sodium.sodium_base642bin(
//            intermediaryResult.asByteArray(),
//            maxLength,
//            data.encodeToByteArray(),
//            data.length,
//            null,
//            binLenPinned.addressOf(0),
//            null,
//            variant.value
//
//        )
        val decoder = when(variant) {
            Base64Variants.ORIGINAL -> Base64.getDecoder()
            Base64Variants.ORIGINAL_NO_PADDING -> Base64.getDecoder()
            Base64Variants.URLSAFE -> Base64.getUrlDecoder()
            Base64Variants.URLSAFE_NO_PADDING -> Base64.getUrlDecoder()
        }
        return decoder.decode(data).asUByteArray()
    }

    actual fun fromHex(data: String): UByteArray {
        // from hex is currently broken in lazysodium-java
        // see https://github.com/terl/lazysodium-java/issues/83
        return data.hexStringToUByteArray()
    }

}
