package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.getSodium
import com.ionspin.kotlin.crypto.util.toHexString
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */


actual class Blake2bDelegated actual constructor(key: UByteArray?, hashLength: Int) : Blake2b {
    override val MAX_HASH_BYTES: Int = 64


    override fun update(data: UByteArray) {
        TODO("not implemented yet")
    }



    override fun digest(): UByteArray {
        TODO("not implemented yet")
    }

}



actual object Blake2bDelegatedStateless : Blake2bStateless {
    override val MAX_HASH_BYTES: Int = 64

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        val hashed =  getSodium().crypto_generichash(64, Uint8Array(inputMessage.toByteArray().toTypedArray()))
        val hash = UByteArray(MAX_HASH_BYTES)
        for (i in 0 until MAX_HASH_BYTES) {
            hash[i] = hashed[i].toUByte()
        }
        return hash
    }


}