package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.getSodium
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */


actual class Blake2bDelegated actual constructor(key: UByteArray?, val hashLength: Int) : Blake2bMultipart {
    override val MAX_HASH_BYTES: Int = 64


    val state : dynamic

    init {
        state = getSodium().crypto_generichash_init(
            Uint8Array(key?.toByteArray()?.toTypedArray() ?: arrayOf()),
            hashLength
        )
    }

    override fun update(data: UByteArray) {
        getSodium().crypto_generichash_update(state, Uint8Array(data.toByteArray().toTypedArray()))
    }

    override fun digest(): UByteArray {
        val hashed = getSodium().crypto_generichash_final(state, hashLength)
        val hash = UByteArray(hashLength)
        for (i in 0 until hashLength) {
            hash[i] = hashed[i].toUByte()
        }
        return hash
    }

}



actual object Blake2bDelegatedStateless : Blake2b {
    override val MAX_HASH_BYTES: Int = 64

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        val hashed =  getSodium().crypto_generichash(hashLength,
            Uint8Array(inputMessage.toByteArray().toTypedArray()),
            Uint8Array(key.toByteArray().toTypedArray())
        )
        val hash = UByteArray(hashLength)
        for (i in 0 until hashLength) {
            hash[i] = hashed[i].toUByte()
        }
        return hash
    }


}
