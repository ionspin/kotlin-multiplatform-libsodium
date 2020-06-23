package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.Initializer.sodium
/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */


actual class Blake2bDelegated actual constructor(key: UByteArray?, val hashLength: Int) : Blake2bMultipart {

    val state = ByteArray(sodium.crypto_generichash_statebytes())

    init {
        sodium.crypto_generichash_init(state,key?.toByteArray() ?: byteArrayOf(), key?.size ?: 0, hashLength)
    }

    override fun update(data: UByteArray) {
        sodium.crypto_generichash_update(state, data.toByteArray(), data.size.toLong())
    }

    override fun digest(): UByteArray {
        val hashed = ByteArray(hashLength)
        sodium.crypto_generichash_final(state, hashed, hashLength)
        return hashed.toUByteArray()
    }

}

actual object Blake2bDelegatedStateless : Blake2b {


    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        val hashed = ByteArray(hashLength)
        sodium.crypto_generichash(hashed, hashed.size, inputMessage.toByteArray(), inputMessage.size.toLong(), key.toByteArray(), key.size)
        return hashed.toUByteArray()
    }

}
