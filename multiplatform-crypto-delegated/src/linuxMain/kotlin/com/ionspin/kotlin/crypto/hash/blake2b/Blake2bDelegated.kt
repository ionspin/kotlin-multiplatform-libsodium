package com.ionspin.kotlin.crypto.hash.blake2b
import interop.*
import kotlinx.cinterop.*
/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */

@ExperimentalUnsignedTypes
actual class Blake2bDelegated actual constructor(key: UByteArray?, hashLength: Int) : Blake2b {
    override val MAX_HASH_BYTES: Int
        get() = TODO("not implemented yet")

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

actual class Blake2bStateless : Blake2bStatelessInterface {
    override fun digest(inputString: String, key: String?, hashLength: Int): UByteArray {
        TODO("not implemented yet")
    }

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        TODO("not implemented yet")
    }

    override val MAX_HASH_BYTES: Int
        get() = TODO("not implemented yet")

}