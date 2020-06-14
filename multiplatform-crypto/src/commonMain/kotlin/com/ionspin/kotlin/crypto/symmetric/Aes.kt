package com.ionspin.kotlin.crypto.symmetric

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 13-Jun-2020
 */
internal sealed class InternalAesKey(val key: String, val keyLength: Int) {
    val keyArray: UByteArray = key.chunked(2).map { it.toUByte(16) }.toUByteArray()
    val numberOf32BitWords = keyLength / 32

    class Aes128Key(key: String) : InternalAesKey(key, 128)
    class Aes192Key(key: String) : InternalAesKey(key, 192)
    class Aes256Key(key: String) : InternalAesKey(key, 256)

    init {
        checkKeyLength(key, keyLength)
    }

    fun checkKeyLength(key: String, expectedLength: Int) {
        if ((key.length / 2) != expectedLength / 8) {
            throw RuntimeException("Invalid key length")
        }
    }
}

