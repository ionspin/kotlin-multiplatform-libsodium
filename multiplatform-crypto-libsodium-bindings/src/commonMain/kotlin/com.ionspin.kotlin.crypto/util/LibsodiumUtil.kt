package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Aug-2020
 */

enum class Base64Variants(val value: Int) {
    ORIGINAL(1), ORIGINAL_NO_PADDING(3), URLSAFE(5), URLSAFE_NO_PADDING(7)
}

class ConversionException() : RuntimeException("Conversion failed")
class PaddingException() : RuntimeException("Padding failed")

expect object LibsodiumUtil {

    fun memcmp(first: UByteArray, second: UByteArray) : Boolean
    fun memzero(target: UByteArray)

    fun pad(unpaddedData : UByteArray, blocksize: Int) : UByteArray
    fun unpad(paddedData: UByteArray, blocksize: Int) : UByteArray

    fun toBase64(data: UByteArray, variant : Base64Variants = Base64Variants.ORIGINAL) : String
    fun toHex(data: UByteArray) : String

    fun fromBase64(data: String, variant : Base64Variants = Base64Variants.ORIGINAL) : UByteArray
    fun fromHex(data: String) : UByteArray

}
