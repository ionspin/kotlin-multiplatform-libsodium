package com.ionspin.kotlin.crypto.hash

import com.goterl.lazycode.lazysodium.interfaces.Hash
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodium

actual typealias Sha256State = Hash.State256
actual typealias Sha512State = Hash.State512

actual object Hash {

    actual fun sha256(data: UByteArray): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha256_BYTES)
        sodium.crypto_hash_sha256(resultHash.asByteArray(), data.asByteArray(), data.size.toLong())
        return resultHash
    }

    actual fun sha256Init(): Sha256State {
        val state = Hash.State256()
        sodium.crypto_hash_sha256_init(state)
        return state
    }

    actual fun sha256Update(state: Sha256State, data: UByteArray) {
        sodium.crypto_hash_sha256_update(state, data.asByteArray(), data.size.toLong())
    }

    actual fun sha256Final(state: Sha256State): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha256_BYTES)
        sodium.crypto_hash_sha256_final(state, resultHash.asByteArray())
        return resultHash
    }

    actual fun sha512(data: UByteArray): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha512_BYTES)
        sodium.crypto_hash_sha512(resultHash.asByteArray(), data.asByteArray(), data.size.toLong())
        return resultHash
    }

    actual fun sha512Init(): Sha512State {
        val state = Hash.State512()
        sodium.crypto_hash_sha512_init(state)
        return state
    }

    actual fun sha512Update(state: Sha512State, data: UByteArray) {
        sodium.crypto_hash_sha512_update(state, data.asByteArray(), data.size.toLong())
    }

    actual fun sha512Final(state: Sha512State): UByteArray {
        val resultHash = UByteArray(crypto_hash_sha512_BYTES)
        sodium.crypto_hash_sha512_final(state, resultHash.asByteArray())
        return resultHash
    }


}
