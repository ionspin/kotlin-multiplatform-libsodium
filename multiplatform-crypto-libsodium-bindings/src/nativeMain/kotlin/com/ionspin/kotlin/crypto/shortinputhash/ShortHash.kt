package com.ionspin.kotlin.crypto.shortinputhash

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_shorthash
import libsodium.crypto_shorthash_keygen

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual object ShortHash {
    actual fun shortHash(data: UByteArray, key: UByteArray): UByteArray {
        val hashResult = UByteArray(crypto_shorthash_BYTES)
        val hashResultPinned = hashResult.pin()
        val dataPinned = data.pin()
        val keyPinned = key.pin()
        crypto_shorthash(hashResultPinned.toPtr(), dataPinned.toPtr(), data.size.convert(), keyPinned.toPtr())
        hashResultPinned.unpin()
        dataPinned.unpin()
        keyPinned.unpin()
        return hashResult
    }

    actual fun shortHashKeygen(): UByteArray {
        val keyResult = UByteArray(crypto_shorthash_KEYBYTES)
        val keyResultPinned = keyResult.pin()
        crypto_shorthash_keygen(keyResultPinned.toPtr())
        keyResultPinned.unpin()
        return keyResult
    }

}
