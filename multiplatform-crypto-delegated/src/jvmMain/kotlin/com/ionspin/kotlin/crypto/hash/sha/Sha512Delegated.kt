package com.ionspin.kotlin.crypto.hash.sha

import com.goterl.lazycode.lazysodium.interfaces.Hash
import com.ionspin.kotlin.crypto.Initializer

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha512Delegated : Sha512Multipart {

    val state = Hash.State512()

    init {
        Initializer.sodium.crypto_hash_sha512_init(state)
    }

    override fun update(data: UByteArray) {
        Initializer.sodium.crypto_hash_sha512_update(state, data.toByteArray(), data.size.toLong())
    }

    override fun digest(): UByteArray {
        val hashed = ByteArray(Sha512Properties.MAX_HASH_BYTES)
        Initializer.sodium.crypto_hash_sha512_final(state, hashed)
        return hashed.toUByteArray()
    }

}

actual object Sha512StatelessDelegated : Sha512 {

    override fun digest(inputMessage: UByteArray): UByteArray {
        val hashed = ByteArray(Sha512Properties.MAX_HASH_BYTES)
        Initializer.sodium.crypto_hash_sha512(hashed, inputMessage.toByteArray(), inputMessage.size.toLong())
        return hashed.toUByteArray()
    }
}
