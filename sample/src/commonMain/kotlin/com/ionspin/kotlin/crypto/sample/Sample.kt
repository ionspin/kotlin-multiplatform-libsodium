package com.ionspin.kotlin.crypto.sample

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.CryptoInitializerDelegated
import com.ionspin.kotlin.crypto.CryptoPrimitives
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString

object Sample {
    fun runSample() {
        println("Initializing crypto library")
        CryptoInitializerDelegated.initializeWithCallback {
            blake2b()
        }


    }

    fun blake2b() {
        println("Blake2b updateable")
        val blake2bUpdateable = CryptoPrimitives.Blake2b.updateable()
        blake2bUpdateable.update("test".encodeToUByteArray())
        println(blake2bUpdateable.digest().toHexString())
        println("Blake2b stateless")
        val statelessResult = CryptoPrimitives.Blake2b.stateless("test".encodeToByteArray().toUByteArray())
        println("Blake2b stateless: ${statelessResult.toHexString()}")
    }
}
