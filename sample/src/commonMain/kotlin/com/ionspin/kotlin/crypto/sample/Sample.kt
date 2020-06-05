package com.ionspin.kotlin.crypto.sample

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.CryptoProvider
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

object Sample {
    suspend fun runSample() {
        Crypto.initialize()
        val blake2bUpdateable = Crypto.Blake2b.updateable()
        blake2bUpdateable.update("test")
        println(blake2bUpdateable.digest().toHexString())
    }
}