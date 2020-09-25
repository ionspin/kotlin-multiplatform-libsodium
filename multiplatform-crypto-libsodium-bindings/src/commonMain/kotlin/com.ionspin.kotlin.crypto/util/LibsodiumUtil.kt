package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Aug-2020
 */
expect object LibsodiumUtil {

    fun memcmp(first: UByteArray, second: UByteArray) : Boolean
    fun memzero(target: UByteArray)

    fun pad(unpaddedData : UByteArray, blocksize: Int) : UByteArray
    fun unpad(paddedData: UByteArray, blocksize: Int) : UByteArray

    fun toBase64(data: UByteArray) : String
    fun toHex(data: UByteArray) : String
    fun toString(data : UByteArray) : String

    fun fromBase64(data: String) : UByteArray
    fun fromHex(data: String) : UByteArray
    fun fromString(data: String) : UByteArray
}
