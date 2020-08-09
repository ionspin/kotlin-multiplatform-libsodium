package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegatedStateless
import kotlinx.cinterop.*
import libsodium.*
import platform.posix.free
import platform.posix.malloc

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha256Delegated : Sha256 {

    val state : crypto_hash_sha256_state

    init {
        val allocated = sodium_malloc(crypto_hash_sha256_state.size.convert())!!
        state = allocated.reinterpret<crypto_hash_sha256_state>().pointed
        crypto_hash_sha256_init(state.ptr)
    }

    override fun update(data: UByteArray) {
        crypto_hash_sha256_update(state.ptr, data.toCValues(), data.size.convert())
    }



    override fun digest(): UByteArray {
        val hashResult = UByteArray(Sha256Properties.MAX_HASH_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_hash_sha256_final(state.ptr, hashResultPinned.addressOf(0))
        sodium_free(state.ptr)
        return hashResult
    }




}
actual object Sha256StatelessDelegated : StatelessSha256 {

    override fun digest(inputMessage: UByteArray): UByteArray {
        val hashResult = UByteArray(MAX_HASH_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_hash_sha256(hashResultPinned.addressOf(0), inputMessage.toCValues(), inputMessage.size.convert())
        hashResultPinned.unpin()
        return hashResult
    }

}
