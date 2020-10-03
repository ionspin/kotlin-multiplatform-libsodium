package com.ionspin.kotlin.crypto.generichash

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import libsodium.crypto_generichash
import libsodium.crypto_generichash_final
import libsodium.crypto_generichash_init
import libsodium.crypto_generichash_keygen
import libsodium.crypto_generichash_update
import platform.posix.malloc

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */

actual typealias GenericHashStateInternal = libsodium.crypto_generichash_blake2b_state

actual object GenericHash {
    val _emitByte: Byte = 0
    val _emitByteArray: ByteArray = ByteArray(0)



    actual fun genericHash(message: UByteArray, requestedHashLength: Int, key: UByteArray?) : UByteArray {
        val hash = UByteArray(requestedHashLength)
        val pinnedHash = hash.pin()
        val pinnedKey = key?.pin()
        val pinnedMessage = message.pin()
        crypto_generichash(
            pinnedHash.toPtr(),
            requestedHashLength.convert(),
            pinnedMessage.toPtr(),
            message.size.convert(),
            pinnedKey?.toPtr(),
            (key?.size ?: 0).convert()
        )
        pinnedHash.unpin()
        pinnedKey?.unpin()
        pinnedMessage.unpin()
        return hash
    }

    actual fun genericHashInit(
        requestedHashLength: Int,
        key: UByteArray?
    ): GenericHashState {
        val stateAllocated = malloc(GenericHashStateInternal.size.convert())
        val statePointed = stateAllocated!!.reinterpret<GenericHashStateInternal>().pointed
        val pinnedKey = key?.pin()
        crypto_generichash_init(
            statePointed.ptr,
            pinnedKey?.toPtr(),
            (key?.size ?: 0).convert(),
            requestedHashLength.convert()
        )
        pinnedKey?.unpin()
        return GenericHashState(requestedHashLength, statePointed)
    }

    actual fun genericHashUpdate(
        state: GenericHashState,
        messagePart: UByteArray
    ) {
        val pinnedMessage = messagePart.pin()
        crypto_generichash_update(
            state.internalState.ptr,
            pinnedMessage.toPtr(),
            messagePart.size.convert()
        )
        pinnedMessage.unpin()
    }

    actual fun genericHashFinal(state: GenericHashState): UByteArray {
        val hashResult = UByteArray(state.hashLength)
        val hashResultPinned = hashResult.pin()
        crypto_generichash_final(
            state.internalState.ptr,
            hashResultPinned.toPtr(),
            state.hashLength.convert()
        )
        hashResultPinned.unpin()
        return hashResult
    }

    actual fun genericHashKeygen(): UByteArray {
        val generatedKey = UByteArray(crypto_generichash_BYTES)
        val generatedKeyPinned = generatedKey.pin()
        crypto_generichash_keygen(generatedKeyPinned.toPtr())
        generatedKeyPinned.unpin()
        return generatedKey
    }


}
