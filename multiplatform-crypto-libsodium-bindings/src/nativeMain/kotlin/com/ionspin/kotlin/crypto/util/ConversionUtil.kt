package com.ionspin.kotlin.crypto.util

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.Pinned
import kotlinx.cinterop.UByteVar
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.toCPointer

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-Aug-2020
 */
fun Pinned<UByteArray>.toPtr() : CPointer<UByteVar>? {
    return try {
        addressOf(0)
    } catch (outOfBounds : ArrayIndexOutOfBoundsException) {
        null
    }
}
