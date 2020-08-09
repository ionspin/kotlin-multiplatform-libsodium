package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 26-Jul-2020
 */
fun UByteArray.fromLittleEndianUByteArrayToBigEndianUByteArray() : UByteArray {
    return this.reversedArray()
}
