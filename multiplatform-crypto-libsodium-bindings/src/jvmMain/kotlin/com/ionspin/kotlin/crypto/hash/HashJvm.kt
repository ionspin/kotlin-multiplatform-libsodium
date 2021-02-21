package com.ionspin.kotlin.crypto.hash

import com.ionspin.kotlin.crypto.Hash256State
import com.ionspin.kotlin.crypto.Hash512State
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual typealias Sha256State = Hash256State
actual typealias Sha512State = Hash512State

actual object Hash {

    actual fun sha256(data: UByteArray): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha256_BYTES)
        sodiumJna.crypto_hash_sha256(resultHash.asByteArray(), data.asByteArray(),  data.size.toLong())
        return resultHash
    }

    actual fun sha256Init(): Sha256State {
        val state = Hash256State()
        sodiumJna.crypto_hash_sha256_init(state)
        return state
    }

    actual fun sha256Update(state: Sha256State, data: UByteArray) {
        sodiumJna.crypto_hash_sha256_update(state, data.asByteArray(), data.size.toLong())
    }

    actual fun sha256Final(state: Sha256State): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha256_BYTES)
        sodiumJna.crypto_hash_sha256_final(state, resultHash.asByteArray())
        return resultHash
    }

    actual fun sha512(data: UByteArray): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha512_BYTES)
        sodiumJna.crypto_hash_sha512(resultHash.asByteArray(), data.asByteArray(), data.size.toLong())
        return resultHash
    }

    actual fun sha512Init(): Sha512State {
        val state = Hash512State()
        sodiumJna.crypto_hash_sha512_init(state)
        return state
    }

    actual fun sha512Update(state: Sha512State, data: UByteArray) {
        sodiumJna.crypto_hash_sha512_update(state, data.asByteArray(), data.size.toLong())
    }

    actual fun sha512Final(state: Sha512State): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha512_BYTES)
        sodiumJna.crypto_hash_sha512_final(state, resultHash.asByteArray())
        return resultHash
    }


}
