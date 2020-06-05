package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bStateless
import com.ionspin.kotlin.crypto.hash.sha.Sha256Pure
import com.ionspin.kotlin.crypto.hash.sha.Sha512Pure

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */


typealias Sha256Stateless = Sha256Pure.Companion

typealias Sha512Stateless = Sha512Pure.Companion


object Crypto : CryptoProvider {
    override suspend fun initialize() {
        Initializer.initialize()
    }

    fun initializeWithCallback(done: () -> Unit) {
        Initializer.initializeWithCallback(done)
    }


    object Blake2b {
        fun updateable(): com.ionspin.kotlin.crypto.hash.blake2b.Blake2b {
            return Blake2bDelegated()
        }

        fun stateless(message: String): UByteArray {
            return Blake2bStateless.digest(message)
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

