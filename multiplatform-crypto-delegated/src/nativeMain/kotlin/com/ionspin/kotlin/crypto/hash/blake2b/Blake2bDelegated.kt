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
        crypto_generichash_init(state.ptr, key?.run { this.toUByteArray().toCValues() }, key?.size?.convert() ?: 0UL, hashLength.convert())
        println("Initialized libsodium hash")
    }

    override fun update(data: UByteArray) {
        crypto_generichash_update(state.ptr, data.toCValues(), data.size.convert())
    }

    override fun update(data: String) {
        val ubyteArray = data.encodeToByteArray().toUByteArray()
        crypto_generichash_update(state.ptr, ubyteArray.toCValues(), ubyteArray.size.convert())
    }

    override fun digest(): UByteArray {
        val hashResult = UByteArray(requestedHashLength)
        val hashResultPinned = hashResult.pin()
        val result = crypto_generichash_final(state.ptr, hashResultPinned.addressOf(0), requestedHashLength.convert())
        println("HashPointer: ${hashResult.toHexString()}")

        return hashResult
    }

    override fun digestString(): String {
        return digest().toHexString()
    }
}

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")
actual object Blake2bDelegatedStateless : Blake2bStateless {
    override fun digest(inputString: String, key: String?, hashLength: Int): UByteArray {
        println("Input $inputString, ${key ?: "null"}, $hashLength")
        val hashResult = UByteArray(MAX_HASH_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_generichash(
            hashResultPinned.addressOf(0),
            hashLength.convert(),
            inputString.encodeToByteArray().toUByteArray().toCValues(),
            inputString.length.convert(),
            key?.run { this.encodeToByteArray().toUByteArray().toCValues() },
            key?.length?.convert() ?: 0UL
        )
        return hashResult
    }




    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        val hashResult = UByteArray(MAX_HASH_BYTES)

        crypto_generichash(
            StableRef.create(hashResult).asCPointer().reinterpret(),
            hashLength.convert(),
            inputMessage.toCValues(),
            inputMessage.size.convert(),
            key.toCValues(),
            key.size.convert() ?: 0UL
        )
        println("HashPointer: ${hashResult.toHexString()}")
        println(hashResult.toHexString())
        return hashResult

    }


}