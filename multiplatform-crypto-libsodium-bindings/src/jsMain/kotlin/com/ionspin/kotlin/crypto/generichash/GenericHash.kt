package com.ionspin.kotlin.crypto.generichash

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */

actual typealias GenericHashStateInternal = Any

actual object GenericHash {
    actual fun genericHash(
        message: UByteArray,
        requestedHashLength: Int,
        key: UByteArray?
    ): UByteArray {
        return getSodium().crypto_generichash(
            requestedHashLength,
            message.toUInt8Array(),
            key?.toUInt8Array() ?: Uint8Array(0)
        ).toUByteArray()
    }

    actual fun genericHashInit(
        requestedHashLength: Int,
        key: UByteArray?
    ): GenericHashState {
        val state = getSodium().crypto_generichash_init(key.toUInt8Array(), requestedHashLength)
        return GenericHashState(requestedHashLength, state)
    }

    actual fun genericHashUpdate(
        state: GenericHashState,
        messagePart: UByteArray
    ) {
        getSodium().crypto_generichash_update(state.internalState, messagePart.toUInt8Array())
    }

    actual fun genericHashFinal(state: GenericHashState): UByteArray {
        return getSodium().crypto_generichash_final(state.internalState, state.hashLength).toUByteArray()
    }
}
