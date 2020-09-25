package com.ionspin.kotlin.crypto.util

import kotlinx.cinterop.StableRef
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.reinterpret
import libsodium.sodium_memcmp
import libsodium.sodium_memzero
import libsodium.sodium_pad
import libsodium.sodium_unpad
import platform.posix.size_t

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
            unpaddedData.size
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
        var newSize = ULongArray(1) { 99UL }
        val newSizePinned = newSize.pin()
        sodium_unpad(
            newSizePinned.addressOf(0),
            paddedDataCopyPinned.toPtr(),
            paddedData.size.convert(),
            blocksize.convert()
        )
        val unpaddedSize = newSize[0]
        if (unpaddedSize > Int.MAX_VALUE.toULong()) {
            throw RuntimeException("Unsupported array size (larger than Integer max value) $unpaddedSize")
        }
        val unpadded = paddedDataCopy.sliceArray(0 until unpaddedSize.toInt())
        paddedDataCopyPinned.unpin()
        newSizePinned.unpin()
        return unpadded
    }

    actual fun toBase64(data: UByteArray): String {
        TODO("not implemented yet")
    }

    actual fun toHex(data: UByteArray): String {
        TODO("not implemented yet")
    }

    actual fun toString(data: UByteArray): String {
        TODO("not implemented yet")
    }

    actual fun fromBase64(data: String): UByteArray {
        TODO("not implemented yet")
    }

    actual fun fromHex(data: String): UByteArray {
        TODO("not implemented yet")
    }

    actual fun fromString(data: String): UByteArray {
        TODO("not implemented yet")
    }

}
