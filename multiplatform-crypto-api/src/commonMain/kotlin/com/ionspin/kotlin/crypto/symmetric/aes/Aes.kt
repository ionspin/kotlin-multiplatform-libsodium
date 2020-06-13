package com.ionspin.kotlin.crypto.symmetric.aes

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 13-Jun-2020
 */
sealed class AesKey(val key: String, val keyLength: Int) {
    val keyArray: UByteArray = key.chunked(2).map { it.toUByte(16) }.toUByteArray()
    val numberOf32BitWords = keyLength / 32

    class Aes128Key(key: String) : AesKey(key, 128)
    class Aes192Key(key: String) : AesKey(key, 192)
    class Aes256Key(key: String) : AesKey(key, 256)

    init {
        checkKeyLength(key, keyLength)
    }

    fun checkKeyLength(key: String, expectedLength: Int) {
        if ((key.length / 2) != expectedLength / 8) {
            throw RuntimeException("Invalid key length")
        }
    }
}

