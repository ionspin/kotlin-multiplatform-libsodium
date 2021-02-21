package com.ionspin.kotlin.crypto.generichash

import com.ionspin.kotlin.crypto.Blake2bState
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual typealias GenericHashStateInternal = Blake2bState

actual object GenericHash {
    actual fun genericHash(
        message: UByteArray,
        requestedHashLength: Int,
        key: UByteArray?
    ): UByteArray {
        val hash = UByteArray(requestedHashLength)
        sodiumJna.crypto_generichash(
            hash.asByteArray(),
            requestedHashLength,
            message.asByteArray(),
            message.size.toLong(),
            key?.asByteArray() ?: ByteArray(0),
            (key?.size ?: 0)
        )
        return hash
    }

    actual fun genericHashInit(
        requestedHashLength: Int,
        key: UByteArray?
    ): GenericHashState {
        val state = GenericHashStateInternal()
        sodiumJna.crypto_generichash_init(state, key?.asByteArray() ?: ByteArray(0), key?.size ?: 0, requestedHashLength)
        return GenericHashState(requestedHashLength, state)
    }

    actual fun genericHashUpdate(
        state: GenericHashState,
        messagePart: UByteArray
    ) {
        sodiumJna.crypto_generichash_update(state.internalState, messagePart.asByteArray(), messagePart.size.toLong())
    }

    actual fun genericHashFinal(state: GenericHashState): UByteArray {
        val hashResult = ByteArray(state.hashLength)
        sodiumJna.crypto_generichash_final(state.internalState, hashResult, state.hashLength)
        return hashResult.asUByteArray()
    }

    actual fun genericHashKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_generichash_BYTES)
        sodiumJna.crypto_generichash_keygen(generatedKey.asByteArray())
        return generatedKey
    }


}
