package com.ionspin.kotlin.crypto.shortinputhash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
const val crypto_shorthash_KEYBYTES = 16
const val crypto_shorthash_BYTES = 8

expect object ShortHash {

    fun shortHash(data : UByteArray, key: UByteArray) : UByteArray
    fun shortHashKeygen() : UByteArray
}
