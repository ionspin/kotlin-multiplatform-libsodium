package com.ionspin.kotlin.crypto.hash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Aug-2020
 */

val crypto_hash_BYTES = 64
val crypto_hash_sha256_BYTES = 32
val crypto_hash_sha512_BYTES = 64

expect class Sha256State
expect class Sha512State

expect object Hash {

    //Not present in LazySodium
    //fun hash(data: UByteArray) : UByteArray

    fun sha256(data: UByteArray) : UByteArray
    fun sha256Init() : Sha256State
    fun sha256Update(state: Sha256State, data : UByteArray)
    fun sha256Final(state : Sha256State) : UByteArray

    fun sha512(data: UByteArray) : UByteArray
    fun sha512Init() : Sha512State
    fun sha512Update(state: Sha512State, data : UByteArray)
    fun sha512Final(state : Sha512State) : UByteArray


}
