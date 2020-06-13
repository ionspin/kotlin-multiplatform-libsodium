package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.getSodium
import com.ionspin.kotlin.crypto.getSodium

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha512Delegated : Sha512 {
    val state : dynamic

    init {
        state = getSodium().crypto_hash_sha512_init()
    }

    override fun update(data: UByteArray) {
        getSodium().crypto_hash_sha512_update(state, Uint8Array(data.toByteArray().toTypedArray()))
    }

    override fun digest(): UByteArray {
        val hashed = getSodium().crypto_hash_sha512_final(state)
        val hash = UByteArray(Sha512StatelessDelegated.MAX_HASH_BYTES)
        for (i in 0 until Sha512StatelessDelegated.MAX_HASH_BYTES) {
            hash[i] = hashed[i].toUByte()
        }
        return hash
    }

}

actual object Sha512StatelessDelegated : StatelessSha512 {

    override fun digest(inputMessage: UByteArray): UByteArray {
        val hashed = getSodium().crypto_hash_sha512(Uint8Array(inputMessage.toByteArray().toTypedArray()))
        val hash = UByteArray(Sha512StatelessDelegated.MAX_HASH_BYTES)
        for (i in 0 until Sha512StatelessDelegated.MAX_HASH_BYTES) {
            hash[i] = hashed[i].toUByte()
        }
        return hash
    }
}