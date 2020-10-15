package com.ionspin.kotlin.crypto.sample

import com.ionspin.kotlin.crypto.util.LibsodiumRandom
import com.ionspin.kotlin.crypto.util.toHexString


object Sample {
    fun runSample() {
        val random = LibsodiumRandom.buf(32)
        println("Random: ${random.toHexString()}")
    }
}
