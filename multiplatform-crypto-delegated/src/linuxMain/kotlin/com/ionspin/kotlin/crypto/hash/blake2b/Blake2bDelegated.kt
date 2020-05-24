package com.ionspin.kotlin.crypto.hash.blake2b
import com.ionspin.kotlin.crypto.util.toHexString
import interop.*
import kotlinx.cinterop.*
import libsodium.*
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
        val result = sodium_init()
        println("Sodium init $result")
        return ubyteArrayOf(0U)
    }

    override fun digestString(): String {
        TODO("not implemented yet")
    }
}

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")
actual object Blake2bStateless : Blake2bStatelessInterface {
    override fun digest(inputString: String, key: String?, hashLength: Int): UByteArray {
        return memScoped {
            val hashResult = UByteArray(MAX_HASH_BYTES)
            val hashPointer = hashResult.toCValues().getPointer(this)
            crypto_generichash(
                hashPointer,
                hashLength.toULong(),
                inputString.cstr.getPointer(this).reinterpret(),
                inputString.length.toULong(), key?.cstr?.getPointer(this)?.reinterpret(),
                key?.length?.toULong() ?: 0UL
            )
            println("HashPointer: ${hashPointer.readBytes(MAX_HASH_BYTES).toUByteArray().toHexString()}")
            println(hashResult.toHexString())
            hashResult
        }

    }

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        TODO("not implemented yet")
    }

    override val MAX_HASH_BYTES: Int = 64

}