package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.*
import libsodium.*
import platform.posix.free
import platform.posix.malloc

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha512Delegated : Sha512Multipart {
    val state : crypto_hash_sha512_state

    init {
        val allocated = malloc(crypto_hash_sha512_state.size.convert())!!
        state = allocated.reinterpret<crypto_hash_sha512_state>().pointed
        crypto_hash_sha512_init(state.ptr)
    }

    override fun update(data: UByteArray) {
        crypto_hash_sha512_update(state.ptr, data.toCValues(), data.size.convert())
    }



    override fun digest(): UByteArray {
        val hashResult = UByteArray(Sha512Properties.MAX_HASH_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_hash_sha512_final(state.ptr, hashResultPinned.toPtr())
        free(state.ptr)
        return hashResult
    }

}

actual object Sha512StatelessDelegated : Sha512  {

    override fun digest(inputMessage: UByteArray): UByteArray {
        val hashResult = UByteArray(Sha512StatelessDelegated.MAX_HASH_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_hash_sha512(hashResultPinned.toPtr(), inputMessage.toCValues(), inputMessage.size.convert())
        hashResultPinned.unpin()
        return hashResult
    }
}
