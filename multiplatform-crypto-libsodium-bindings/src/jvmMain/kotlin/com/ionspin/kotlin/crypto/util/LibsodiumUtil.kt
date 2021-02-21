package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna
import com.sun.jna.ptr.IntByReference
import java.lang.RuntimeException
import java.util.*

actual object LibsodiumUtil {
    actual fun memcmp(first: UByteArray, second: UByteArray): Boolean {
        if (first.size != second.size) {
            throw RuntimeException("Sodium memcmp() only supports comparing same length arrays")
        }
        return sodiumJna.sodium_memcmp(first.asByteArray(), second.asByteArray(), first.size) == 0
    }

    actual fun memzero(target: UByteArray) {
        sodiumJna.sodium_memzero(target.asByteArray(), target.size)
    }

    actual fun pad(unpaddedData: UByteArray, blocksize: Int): UByteArray {

        val newPadSizeReference = IntByReference(0)
        val newSize = ((unpaddedData.size / blocksize) + 1) * blocksize
        val paddedArray = UByteArray(newSize) {
            if (it < unpaddedData.size) {
                unpaddedData[it]
            } else {
                0U
            }
        }
        val resultCode = sodiumJna.sodium_pad(
            newPadSizeReference.pointer,
            paddedArray.asByteArray(),
            unpaddedData.size,
            blocksize,
            newSize
        )
        if (resultCode != 0) {
            throw RuntimeException("Padding failed")
        }

        return paddedArray.slice(0 until newPadSizeReference.value).map { it.toByte().toUByte()}.toUByteArray()

    }

    actual fun unpad(paddedData: UByteArray, blocksize: Int): UByteArray {

        val paddedDataCopy = paddedData.copyOf().asByteArray()
        var unpaddedSize =  IntByReference(0)

        sodiumJna.sodium_unpad(
            unpaddedSize.pointer,
            paddedDataCopy,
            paddedData.size,
            blocksize
        )

        val unpadded = paddedDataCopy.sliceArray(0 until unpaddedSize.value).asUByteArray()

        return unpadded
    }

    actual fun toBase64(
        data: UByteArray,
        variant: Base64Variants
    ): String {
        val maxlen = sodiumJna.sodium_base64_encoded_len(data.size, variant.value)
        val result = ByteArray(maxlen) { 0 }
        sodiumJna.sodium_bin2base64(
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
        sodiumJna.sodium_bin2hex(
            result,
            hexLen,
            data.asByteArray(),
            data.size
        )

        //Drop terminating char \0
        return String(result.sliceArray(0 until result.size - 1))
    }

    actual fun fromHex(data: String): UByteArray {
        val binLenReference = IntByReference(0)
        val binSize = (data.length + 1) / 2 // -1 for terminator char
        val hex = data.toCharArray().map { it.toByte() }.toByteArray()
        val result = ByteArray(binSize)
        val resultCode = sodiumJna.sodium_hex2bin(
            result,
            binSize,
            hex,
            hex.size,
            null,
            binLenReference.pointer,
            null
        )
        if (resultCode != 0) {
            throw ConversionException()
        }
        return result.slice(0 until binLenReference.value).toByteArray().asUByteArray()
    }

    actual fun fromBase64(
        data: String,
        variant: Base64Variants
    ): UByteArray {
        val binLengthReference = IntByReference(0)
        val maxLength = (data.length * 3) / 4
        val intermediaryResult = UByteArray(maxLength) { 0U }

        val resultCode = sodiumJna.sodium_base642bin(
            intermediaryResult.asByteArray(),
            maxLength,
            data.encodeToByteArray(),
            data.length,
            null,
            binLengthReference.pointer,
            null,
            variant.value
        )

        if (resultCode != 0) {
            throw ConversionException()
        }

        return intermediaryResult.sliceArray(0 until binLengthReference.value)



    }



}
