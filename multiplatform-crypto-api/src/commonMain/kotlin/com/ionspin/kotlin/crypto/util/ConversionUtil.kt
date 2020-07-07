package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Jun-2020
 */
@Suppress("CAST_NEVER_SUCCEEDS")
fun ByteArray.asUByteArray() : UByteArray {
    return this as UByteArray
}

@Suppress("CAST_NEVER_SUCCEEDS")
fun UByteArray.asByteArray() : ByteArray {
    return this as ByteArray
}
