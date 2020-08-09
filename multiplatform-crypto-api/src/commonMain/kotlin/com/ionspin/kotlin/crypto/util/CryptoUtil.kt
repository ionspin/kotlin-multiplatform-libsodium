package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Jun-2020
 */
val _emit = IntArray(0)

fun UByteArray.overwriteWithZeroes() {
    for (i in 0 until size) {
        this[i] = 0U
    }
}

fun UIntArray.overwriteWithZeroes() {
    for (i in 0 until size) {
        this[i] = 0U
    }
}
