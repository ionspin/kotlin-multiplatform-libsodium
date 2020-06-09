package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.getSodium
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegatedStateless

import ext.libsodium.crypto_hash_sha256_init

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha256Delegated actual constructor(key: UByteArray?, hashLength: Int) : Sha256 {

    val state : dynamic

    init {
        state = getSodium().crypto_hash_sha256_init()
    }

    override fun update(data: UByteArray) {
        getSodium().crypto_hash_sha256_update(state, Uint8Array(data.toByteArray().toTypedArray()))
    }

    override fun digest(): UByteArray {
        val hashed = getSodium().crypto_hash_sha256_final(state)
        val hash = UByteArray(Sha256StatelessDelegated.MAX_HASH_BYTES)
        console.log(hashed)
        for (i in 0 until Sha256StatelessDelegated.MAX_HASH_BYTES) {
            hash[i] = hashed[i].toUByte()
        }
        return hash
    }

}

actual object Sha256StatelessDelegated : StatelessSha256 {

    override fun digest(inputMessage: UByteArray): UByteArray {
        val hashed = getSodium().crypto_hash_sha256(Uint8Array(inputMessage.toByteArray().toTypedArray()))
        val hash = UByteArray(MAX_HASH_BYTES)
        for (i in 0 until MAX_HASH_BYTES) {
            hash[i] = hashed[i].toUByte()
        }
        return hash
    }
}