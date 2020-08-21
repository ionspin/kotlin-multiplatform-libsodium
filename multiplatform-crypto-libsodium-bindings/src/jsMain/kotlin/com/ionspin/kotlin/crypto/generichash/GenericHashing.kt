package com.ionspin.kotlin.crypto.generichash

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual object GenericHashing {
    actual fun genericHash(
        message: UByteArray,
        requestedHashLength: Int,
        key: UByteArray?
    ): UByteArray {
        return getSodium().crypto_generichash(
            requestedHashLength,
            message.toUInt8Array(),
            key?.toUInt8Array() ?: Uint8Array(0)
        ).toUByteArray()
    }
}
