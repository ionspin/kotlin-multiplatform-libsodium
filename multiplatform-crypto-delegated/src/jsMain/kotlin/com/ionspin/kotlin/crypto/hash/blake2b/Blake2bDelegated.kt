package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.util.toHexString
import ext.libsodium.com.ionspin.kotlin.crypto.JsSodiumInterface
import ext.libsodium.com.ionspin.kotlin.crypto.JsSodiumLoader
import ext.libsodium.crypto_generichash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */

@ExperimentalUnsignedTypes
actual class Blake2bDelegated actual constructor(key: UByteArray?, hashLength: Int) : Blake2b {
    override val MAX_HASH_BYTES: Int = 64


    override fun update(data: UByteArray) {
        TODO("not implemented yet")
    }

    override fun update(data: String) {
        TODO("not implemented yet")
    }

    override fun digest(): UByteArray {
        TODO("not implemented yet")
    }

    override fun digestString(): String {
        TODO("not implemented yet")
    }
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
actual object Blake2bStateless : Blake2bStatelessInterface {
    override val MAX_HASH_BYTES: Int = 64

    override suspend fun digest(inputString: String, key: String?, hashLength: Int): UByteArray {
        val sodium = JsSodiumLoader.load()
        val hashed = sodium.crypto_generichash(64, inputString)
        val hash = UByteArray(MAX_HASH_BYTES)
        for (i in 0 until MAX_HASH_BYTES) {
            js(
                """
                    hash[i] = hashed[i]
                """
            )
        }
        println("Hash ${hash.toHexString()}")
        return ubyteArrayOf(0U)
    }

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        TODO("not implemented yet")
    }


}