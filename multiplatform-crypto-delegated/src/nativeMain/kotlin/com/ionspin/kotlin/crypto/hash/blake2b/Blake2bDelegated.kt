package com.ionspin.kotlin.crypto.hash.blake2b
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.cinterop.*
import libsodium.*
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
        state = nativeHeap.alloc(crypto_generichash_state.size, 1).reinterpret()
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
//        val inputString = "test"
//        val hashLength = 64
//        val key : String? = null
//        val result2 = allocEverything(inputString, key, hashLength)
//        val result2String = result2.toHexString()
//        println(result2String)
//        return ubyteArrayOf(0U)
    }

    fun allocEverything(inputString: String, key: String?, hashLength: Int) : UByteArray {
        val res = memScoped {
            val result = allocArray<UByteVar>(hashLength)
            println("Alloced: $result")
            crypto_generichash(
                result,
                hashLength.convert(),
                inputString.encodeToByteArray().toUByteArray().toCValues(),
                inputString.length.convert(),
                key?.run { this.encodeToByteArray().toUByteArray().toCValues() },
                key?.length?.convert() ?: 0UL
            )
            println("Result: $result")
            UByteArray(hashLength) {
                result[it]
            }
        }
        return res
    }

    override fun digestString(): String {
        return digest().toHexString()
    }
}

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")
actual object Blake2bStateless : Blake2bStatelessInterface {
    override fun digest(inputString: String, key: String?, hashLength: Int): UByteArray {
//        return allocEverything(inputString, key, hashLength)
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
        println("HashPointer: ${hashResult.toHexString()}")
        println(hashResult.toHexString())
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