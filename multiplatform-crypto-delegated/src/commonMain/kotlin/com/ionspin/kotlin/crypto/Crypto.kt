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
            return Blake2bDelegated(key, hashLength)
        }

        fun stateless(message: UByteArray, key: UByteArray = ubyteArrayOf(), hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): UByteArray {
            return Blake2bDelegatedStateless.digest(message, key, hashLength)
        }
    }

    object Sha256 {
        fun updateable(): com.ionspin.kotlin.crypto.hash.sha.Sha256 {
            return Sha256Delegated()
        }

        fun stateless(message: UByteArray, key: UByteArray? = null, hashLength: Int = Sha256Properties.MAX_HASH_BYTES) {

        }
    }

    object Sha512 {
        fun updateable(): com.ionspin.kotlin.crypto.hash.sha.Sha512 {
            return Sha512Delegated()
        }

        fun stateless(message: UByteArray, key: UByteArray? = null, hashLength: Int = Sha512Properties.MAX_HASH_BYTES) {

        }
    }

}


object SimpleCrypto {
    fun hash(message: String): UByteArray {
        return ubyteArrayOf(0U)
    }

}

expect object Initializer {
    suspend fun initialize()

    fun initializeWithCallback(done: () -> (Unit))
}

