package com.ionspin.kotlin.crypto.shortinputhash

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodium

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */


actual object ShortHash {
    actual fun shortHash(data: UByteArray, key: UByteArray): UByteArray {
        val hashResult = UByteArray(crypto_shorthash_BYTES)
        sodium.crypto_shorthash(hashResult.asByteArray(), data.asByteArray(), data.size.toLong(), key.asByteArray())
        return hashResult
    }

    actual fun shortHashKeygen(): UByteArray {
        val key = UByteArray(crypto_shorthash_KEYBYTES)
        sodium.crypto_shorthash_keygen(key.asByteArray())
        return key
    }

}
