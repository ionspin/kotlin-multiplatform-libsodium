package com.ionspin.kotlin.crypto.generichash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */

val crypto_generichash_BYTES = 32
val crypto_generichash_blake2b_BYTES_MIN = 16
val crypto_generichash_blake2b_BYTES_MAX = 64
val crypto_generichash_blake2b_BYTES = 32
val crypto_generichash_blake2b_KEYBYTES_MIN = 16
val crypto_generichash_blake2b_KEYBYTES_MAX = 64
val crypto_generichash_blake2b_KEYBYTES = 32
val crypto_generichash_blake2b_SALTBYTES = 16

expect class GenericHashStateInternal

data class GenericHashState(val hashLength: Int, val internalState: GenericHashStateInternal)


expect object GenericHash {
    /**
     * Request computing a hash of message, with a specific hash length and optional key. The specific hash length can be
     * between [crypto_generichash_blake2b_BYTES_MIN] and [crypto_generichash_blake2b_BYTES_MAX]. If the key is provided
     * it needs the hash will be different for each different key.
     */
    fun genericHash(message : UByteArray, requestedHashLength: Int = crypto_generichash_BYTES, key : UByteArray? = null) : UByteArray

    /**
     * Prepare a Generic Hash State object that will be used to compute hash of data with arbitrary length. Secific hash length
     * can be requested
     */
    fun genericHashInit(requestedHashLength: Int = crypto_generichash_BYTES, key : UByteArray? = null) : GenericHashState

    /**
     * Feed another chunk of message to the updateable hash object
     */
    fun genericHashUpdate(state: GenericHashState, messagePart : UByteArray)

    /**
     * Feed the last chunk of message to the updateable hash object. This returns the actual hash.
     */
    fun genericHashFinal(state : GenericHashState) : UByteArray

    /**
     * Generate a key of length [crypto_generichash_blake2b_KEYBYTES] that can be used with the generic hash funciton
     */
    fun genericHashKeygen() : UByteArray

//      ---- Not present in LazySodium nor libsodium.js
//    fun blake2b(message : UByteArray, requestedHashLength: Int, key : UByteArray? = null) : UByteArray
//
//    fun blake2bInit(requestedHashLength: Int, key : UByteArray? = null) : Blake2bState
//    fun blake2bUpdate(state: GenericHashState, messagePart : UByteArray)
//    fun blake2bFinal(state : GenericHashState) : UByteArray
//
//    fun blake2bKeygen() : UByteArray


}


