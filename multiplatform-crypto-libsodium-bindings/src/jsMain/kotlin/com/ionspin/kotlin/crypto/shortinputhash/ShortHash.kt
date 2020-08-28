package com.ionspin.kotlin.crypto.shortinputhash

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual object ShortHash {
    actual fun shortHash(data: UByteArray, key: UByteArray): UByteArray {
        return getSodium().crypto_shorthash(data.toUInt8Array(), key.toUInt8Array()).toUByteArray()
    }

    actual fun shortHashKeygen(): UByteArray {
        return getSodium().crypto_shorthash_keygen().toUByteArray()
    }

}
