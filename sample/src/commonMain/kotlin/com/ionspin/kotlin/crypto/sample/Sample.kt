package com.ionspin.kotlin.crypto.sample

import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.LibsodiumRandom
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString


object Sample {
    fun runSample() {
        val random = LibsodiumRandom.buf(32)
        println("Random: ${random.toHexString()}")
    }

    fun hashSomething() : String {
        val hash = Hash.sha512("123".encodeToUByteArray())
        return "Hash (SHA512) of 123: ${hash.toHexString()}"
    }
}
