package com.ionspin.kotlin.crypto.hash.blake2b
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.cinterop.*
import libsodium.*
import platform.posix.malloc
/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */


actual class Blake2bDelegated actual constructor(key: UByteArray?, hashLength: Int) : Blake2b {
    override val MAX_HASH_BYTES: Int = 64

    val requestedHashLength : Int
    val state : crypto_generichash_state
    init {
        println("Initializing libsodium hash")
        requestedHashLength = hashLength
        println("Size ${crypto_generichash_state.size}")
        println("Align ${crypto_generichash_state.align}")
        println("Using sodium malloc for state")
        val allocated = sodium_malloc(crypto_generichash_state.size.convert())!!
        state = allocated.reinterpret<crypto_generichash_state>().pointed
        println("allocated state")
        crypto_generichash_init(state.ptr, key?.run { this.toUByteArray().toCValues() }, key?.size?.convert() ?: 0UL.convert(), hashLength.convert())
        println("Initialized libsodium hash")
    }

    override fun update(data: UByteArray) {
        crypto_generichash_update(state.ptr, data.toCValues(), data.size.convert())
    }

    override fun digest(): UByteArray {
        val hashResult = UByteArray(requestedHashLength)
        val hashResultPinned = hashResult.pin()
        crypto_generichash_final(state.ptr, hashResultPinned.addressOf(0), requestedHashLength.convert())
        sodium_free(state.ptr)
        return hashResult
    }

}

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")
actual object Blake2bDelegatedStateless : Blake2bStateless {

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        val hashResult = UByteArray(MAX_HASH_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_generichash(
            hashResultPinned.addressOf(0),
            hashLength.convert(),
            inputMessage.toCValues(),
            inputMessage.size.convert(),
            key.toCValues(),
            key.size.convert()
        )
        hashResultPinned.unpin()
        return hashResult

    }


}