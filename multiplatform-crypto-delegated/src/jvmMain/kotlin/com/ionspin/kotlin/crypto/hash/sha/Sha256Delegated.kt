package com.ionspin.kotlin.crypto.hash.sha

import com.goterl.lazycode.lazysodium.interfaces.Hash
import com.ionspin.kotlin.crypto.Initializer.sodium


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha256Delegated actual constructor() : Sha256 {

    val state = Hash.State256()

    init {
        sodium.crypto_hash_sha256_init(state)
    }

    override fun update(data: UByteArray) {
        sodium.crypto_hash_sha256_update(state, data.toByteArray(), data.size.toLong())
    }

    override fun digest(): UByteArray {
        val hashed = ByteArray(Sha256Properties.MAX_HASH_BYTES)
        sodium.crypto_hash_sha256_final(state, hashed)
        return hashed.asUByteArray()
    }

}

actual object Sha256StatelessDelegated : StatelessSha256 {

    override fun digest(inputMessage: UByteArray): UByteArray {
        val hashed = ByteArray(Sha256Properties.MAX_HASH_BYTES)
        sodium.crypto_hash_sha256(hashed, inputMessage.toByteArray(), inputMessage.size.toLong())
        return hashed.asUByteArray()
    }
}
