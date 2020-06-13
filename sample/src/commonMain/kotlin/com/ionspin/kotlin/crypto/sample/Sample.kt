package com.ionspin.kotlin.crypto.sample

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.CryptoProvider
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Sample {
    fun runSample() {
        println("Initializing crypto library")
        Crypto.initializeWithCallback {
            blake2b()
        }


    }

    fun blake2b() {
        println("Blake2b updateable")
        val blake2bUpdateable = Crypto.Blake2b.updateable()
        blake2bUpdateable.update("test".encodeToUByteArray())
        println(blake2bUpdateable.digest().toHexString())
        println("Blake2b stateless")
        val statelessResult = Crypto.Blake2b.stateless("test".encodeToByteArray().toUByteArray())
        println("Blake2b stateless: ${statelessResult.toHexString()}")
    }
}