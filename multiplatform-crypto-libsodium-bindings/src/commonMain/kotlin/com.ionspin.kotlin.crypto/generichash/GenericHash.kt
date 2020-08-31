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

    fun genericHash(message : UByteArray, requestedHashLength: Int, key : UByteArray? = null) : UByteArray

    fun genericHashInit(requestedHashLength: Int, key : UByteArray? = null) : GenericHashState
    fun genericHashUpdate(state: GenericHashState, messagePart : UByteArray)
    fun genericHashFinal(state : GenericHashState) : UByteArray

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


