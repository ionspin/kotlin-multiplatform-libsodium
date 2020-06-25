package com.ionspin.kotlin.crypto.hash.blake2b
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.cinterop.*
import libsodium.*
import platform.posix.free
import platform.posix.malloc
/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */


actual class Blake2bDelegated actual constructor(key: UByteArray?, hashLength: Int) : Blake2bMultipart {
    override val MAX_HASH_BYTES: Int = 64

    val requestedHashLength : Int
    val state : crypto_generichash_state
    init {
        requestedHashLength = hashLength
        val allocated = malloc(crypto_generichash_state.size.convert())!!
        state = allocated.reinterpret<crypto_generichash_state>().pointed
        crypto_generichash_init(state.ptr, key?.run { this.toUByteArray().toCValues() }, key?.size?.convert() ?: 0UL.convert(), hashLength.convert())
    }

    override fun update(data: UByteArray) {
        crypto_generichash_update(state.ptr, data.toCValues(), data.size.convert())
    }

    override fun digest(): UByteArray {
        val hashResult = UByteArray(requestedHashLength)
        val hashResultPinned = hashResult.pin()
        crypto_generichash_final(state.ptr, hashResultPinned.addressOf(0), requestedHashLength.convert())
        free(state.ptr)
        return hashResult
    }

}

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")
actual object Blake2bDelegatedStateless : Blake2b {

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
