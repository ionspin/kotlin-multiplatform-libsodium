package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.getSodium
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegatedStateless
import org.khronos.webgl.Uint8Array

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
        val hashed = getSodium().crypto_hash_sha256(Uint8Array(inputMessage.toByteArray().toTypedArray()))
        val hash = UByteArray(MAX_HASH_BYTES)
        for (i in 0 until MAX_HASH_BYTES) {
            js(
                """
                    hash[i] = hashed[i]
                """
            )
        }
        return hash
    }
}