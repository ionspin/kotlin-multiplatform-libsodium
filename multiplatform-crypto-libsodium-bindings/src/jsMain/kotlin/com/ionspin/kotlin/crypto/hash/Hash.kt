package com.ionspin.kotlin.crypto.hash

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

actual typealias Sha256State = Any
actual typealias Sha512State = Any

actual object Hash {

    //Not present in LazySodium
    //fun hash(data: UByteArray) : UByteArray
    actual fun sha256(data: UByteArray): UByteArray {
        return getSodium().crypto_hash_sha256(data.toUInt8Array()).toUByteArray()
    }

    actual fun sha256Init(): Sha256State {
        return getSodium().crypto_hash_sha256_init()
    }

    actual fun sha256Update(state: Sha256State, data: UByteArray) {
        getSodium().crypto_hash_sha256_update(state, data.toUInt8Array())
    }

    actual fun sha256Final(state: Sha256State): UByteArray {
        return getSodium().crypto_hash_sha256_final(state).toUByteArray()
    }

    actual fun sha512(data: UByteArray): UByteArray {
        return getSodium().crypto_hash_sha512(data.toUInt8Array()).toUByteArray()
    }

    actual fun sha512Init(): Sha512State {
        return getSodium().crypto_hash_sha512_init()
    }

    actual fun sha512Update(state: Sha512State, data: UByteArray) {
        getSodium().crypto_hash_sha512_update(state, data.toUInt8Array())
    }

    actual fun sha512Final(state: Sha512State): UByteArray {
        return getSodium().crypto_hash_sha512_final(state).toUByteArray()
    }


}
