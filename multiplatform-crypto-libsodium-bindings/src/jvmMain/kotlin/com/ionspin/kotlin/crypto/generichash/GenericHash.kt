package com.ionspin.kotlin.crypto.generichash

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodium

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual class GenericHashStateInternal(internal val data: ByteArray)

actual object GenericHash {
    actual fun genericHash(
        message: UByteArray,
        requestedHashLength: Int,
        key: UByteArray?
    ): UByteArray {
        val hash = UByteArray(requestedHashLength)
        sodium.crypto_generichash(
            hash.asByteArray(),
            requestedHashLength,
            message.asByteArray(),
            message.size.toLong(),
            key?.asByteArray(),
            (key?.size ?: 0)
        )
        return hash
    }

    actual fun genericHashInit(
        requestedHashLength: Int,
        key: UByteArray?
    ): GenericHashState {
        val state = GenericHashStateInternal(ByteArray(sodium.crypto_generichash_statebytes()))
        sodium.crypto_generichash_init(state.data, key?.asByteArray(), key?.size ?: 0, requestedHashLength)
        return GenericHashState(requestedHashLength, state)
    }

    actual fun genericHashUpdate(
        state: GenericHashState,
        messagePart: UByteArray
    ) {
        sodium.crypto_generichash_update(state.internalState.data, messagePart.asByteArray(), messagePart.size.toLong())
    }

    actual fun genericHashFinal(state: GenericHashState): UByteArray {
        val hashResult = ByteArray(state.hashLength)
        sodium.crypto_generichash_final(state.internalState.data, hashResult, state.hashLength)
        return hashResult.asUByteArray()
    }


}
