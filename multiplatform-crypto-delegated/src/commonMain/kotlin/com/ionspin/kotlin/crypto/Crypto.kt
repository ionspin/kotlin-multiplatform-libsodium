package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bProperties
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegatedStateless
import com.ionspin.kotlin.crypto.hash.sha.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */

object Crypto : CryptoProvider {
    override suspend fun initialize() {
        Initializer.initialize()
    }

    fun initializeWithCallback(done: () -> Unit) {
        Initializer.initializeWithCallback(done)
    }


    object Blake2b {
        fun updateable(key: UByteArray? = null, hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): com.ionspin.kotlin.crypto.hash.blake2b.Blake2b {
            checkInitialization()
            return Blake2bDelegated(key, hashLength)
        }

        fun stateless(message: UByteArray, key: UByteArray = ubyteArrayOf(), hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): UByteArray {
            checkInitialization()
            return Blake2bDelegatedStateless.digest(message, key, hashLength)
        }
    }

    object Sha256 {
        fun updateable(): com.ionspin.kotlin.crypto.hash.sha.Sha256 {
            checkInitialization()
            return Sha256Delegated()
        }

        fun stateless(message: UByteArray) : UByteArray{
            checkInitialization()
            return Sha256StatelessDelegated.digest(inputMessage =  message)
        }
    }

    object Sha512 {
        fun updateable(): com.ionspin.kotlin.crypto.hash.sha.Sha512 {
            checkInitialization()
            return Sha512Delegated()
        }

        fun stateless(message: UByteArray) : UByteArray {
            checkInitialization()
            return Sha512StatelessDelegated.digest(inputMessage =  message)
        }
    }

    private fun checkInitialization() {
        if (!Initializer.isInitialized()) {
            throw RuntimeException("Platform library not initialized, check if you called Initializer.initialize()")
        }
    }

}


object SimpleCrypto {
    fun hash(message: String): UByteArray {
        return ubyteArrayOf(0U)
    }

}

expect object Initializer {
    fun isInitialized() : Boolean

    suspend fun initialize()

    fun initializeWithCallback(done: () -> (Unit))
}

