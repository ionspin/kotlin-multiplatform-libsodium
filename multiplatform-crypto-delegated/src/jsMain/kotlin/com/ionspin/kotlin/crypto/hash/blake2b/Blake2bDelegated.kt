package com.ionspin.kotlin.crypto.hash.blake2b

import crypto_generichash
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import kotlin.js.Promise

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */

@ExperimentalUnsignedTypes
actual class Blake2bDelegated actual constructor(key: UByteArray?, hashLength: Int) : Blake2b {
    override val MAX_HASH_BYTES: Int = 64


    override fun update(data: UByteArray) {
        TODO("not implemented yet")
    }

    override fun update(data: String) {
        TODO("not implemented yet")
    }

    override fun digest(): UByteArray {
        TODO("not implemented yet")
    }

    override fun digestString(): String {
        TODO("not implemented yet")
    }
}

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
actual object Blake2bStateless : Blake2bStatelessInterface {
    override val MAX_HASH_BYTES: Int = 64

    override fun digest(inputString: String, key: String?, hashLength: Int): UByteArray {
//        val hashed = crypto_generichash(64, Uint8Array(inputString.encodeToByteArray().toTypedArray()), null)
//        return UByteArray(MAX_HASH_BYTES) { hashed[it].toUByte() }

        val hash = crypto_generichash(64, Uint8Array(arrayOf(0U.toByte())));
        println("Hash $hash")
        return ubyteArrayOf(0U)
    }

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        TODO("not implemented yet")
    }



}