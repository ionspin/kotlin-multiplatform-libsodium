package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegatedStateless
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.toCValues
import libsodium.crypto_hash_sha256

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha256Delegated actual constructor(key: UByteArray?, hashLength: Int) : Sha256 {

    override fun update(data: UByteArray) {
        TODO("not implemented yet")
    }

    override fun update(data: String) {
        TODO("not implemented yet")
    }

    override fun digest(): UByteArray {
        TODO("not implemented yet")
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