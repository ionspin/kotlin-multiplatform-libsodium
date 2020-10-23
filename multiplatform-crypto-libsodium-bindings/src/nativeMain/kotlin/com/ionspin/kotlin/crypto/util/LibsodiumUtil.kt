package com.ionspin.kotlin.crypto.util

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.sodium_base642bin
import libsodium.sodium_base64_encoded_len
import libsodium.sodium_bin2base64
import libsodium.sodium_bin2hex
import libsodium.sodium_hex2bin
import libsodium.sodium_memcmp
import libsodium.sodium_memzero
import libsodium.sodium_pad
import libsodium.sodium_unpad
import platform.posix.size_tVar

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Aug-2020
 */


actual object LibsodiumUtil {

    actual fun memcmp(first: UByteArray, second: UByteArray): Boolean {
        val firstPinned = first.pin()
        val secondPinned = second.pin()
        val result = sodium_memcmp(firstPinned.toPtr(), secondPinned.toPtr(), first.size.convert())
        firstPinned.unpin()
        secondPinned.unpin()
        return result == 0
    }

    actual fun memzero(target: UByteArray) {
        val targetPinned = target.pin()
        sodium_memzero(targetPinned.toPtr(), target.size.convert())
    }

    actual fun pad(unpaddedData : UByteArray, blocksize: Int) : UByteArray {
        val resultingSize = if (unpaddedData.size % blocksize != 0 ) {
            ((unpaddedData.size / blocksize) + 1 ) * blocksize
        } else {
            unpaddedData.size + blocksize
        }
        val paddedData = UByteArray(resultingSize)
        unpaddedData.copyInto(paddedData, 0, 0)
        val paddedDataPinned = paddedData.pin()

        sodium_pad(
            null,
            paddedDataPinned.toPtr(),
            unpaddedData.size.convert(),
            blocksize.convert(),
            resultingSize.convert()
        )
        paddedDataPinned.unpin()
        return paddedData
    }

    actual fun unpad(paddedData: UByteArray, blocksize: Int) : UByteArray {
        val paddedDataCopy = paddedData.copyOf()
        val paddedDataCopyPinned = paddedDataCopy.pin()
        var newSizeULong = ULongArray(1) { 0UL }
        val newSizePinned = newSizeULong.pin()
        sodium_unpad(
            newSizePinned.addressOf(0) as kotlinx.cinterop.CValuesRef<platform.posix.size_tVar>, // TODO Find a better solution for this!
            paddedDataCopyPinned.toPtr(),
            paddedData.size.convert(),
            blocksize.convert()
        )
        val unpaddedSize = newSizeULong[0]
        if (unpaddedSize > Int.MAX_VALUE.toULong()) {
            throw RuntimeException("Unsupported array size (larger than Integer max value) $unpaddedSize")
        }
        val unpadded = paddedDataCopy.sliceArray(0 until unpaddedSize.toInt())
        paddedDataCopyPinned.unpin()
        newSizePinned.unpin()
        return unpadded
    }

    actual fun toBase64(data: UByteArray, variant : Base64Variants): String {
        val maxlen = sodium_base64_encoded_len(data.size.convert(), variant.value)
        val dataPinned = data.pin()
        val result = ByteArray(maxlen.toInt()) { 0 }
        val resultPinned = result.pin()
        sodium_bin2base64(
            resultPinned.addressOf(0),
            maxlen,
            dataPinned.toPtr(),
            data.size.convert(),
            variant.value
        )
        dataPinned.unpin()
        resultPinned.unpin()
        //Drop '\0'
        return result.map { it.toChar() }.dropLast(1).joinToString("")
    }

    actual fun toHex(data: UByteArray): String {
        val hexLen = (data.size * 2) + 1 // +1 for terminator char
        val result = ByteArray(hexLen)
        val resultPinned = result.pin()
        val dataPinned = data.pin()
        sodium_bin2hex(
            resultPinned.addressOf(0),
            hexLen.convert(),
            dataPinned.toPtr(),
            data.size.convert()
        )
        resultPinned.unpin()
        dataPinned.unpin()
        //Drop \0 termination
        return result.map { it.toChar() }.dropLast(1).joinToString("")
    }

    actual fun fromBase64(data: String, variant : Base64Variants): UByteArray {
        val maxLength = (data.length * 3) / 4
        val intermediaryResult = UByteArray(maxLength) { 0U }

        val intermediaryResultPinned = intermediaryResult.pin()
        var binLen = ULongArray(1) { 0UL }
        var binLenPinned = binLen.pin()
        sodium_base642bin(
            intermediaryResultPinned.toPtr(),
            maxLength.convert(),
            data,
            data.length.convert(),
            null,
            binLenPinned.addressOf(0) as kotlinx.cinterop.CValuesRef<platform.posix.size_tVar>, // TODO Find a better solution for this!
            null,
            variant.value

        )
        binLenPinned.unpin()
        intermediaryResultPinned.unpin()

        return if (binLen[0].toInt() != maxLength) {
            intermediaryResult.sliceArray(0 until binLen[0].toInt())
        } else {
            intermediaryResult
        }
    }

    actual fun fromHex(data: String): UByteArray {
        val expectedSize = (data.length + 1) / 2
        val result = UByteArray(expectedSize)
        val resultPinned = result.pin()
        sodium_hex2bin(
            resultPinned.toPtr(),
            expectedSize.convert(),
            data,
            data.length.convert(),
            null,
            null,
            null
        )
        resultPinned.unpin()
        return result
    }

}
