package com.ionspin.kotlin.crypto.hash

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import libsodium.crypto_hash
import libsodium.crypto_hash_sha256
import libsodium.crypto_hash_sha256_final
import libsodium.crypto_hash_sha256_init
import libsodium.crypto_hash_sha256_state
import libsodium.crypto_hash_sha256_update
import libsodium.crypto_hash_sha512
import libsodium.crypto_hash_sha512_final
import libsodium.crypto_hash_sha512_init
import libsodium.crypto_hash_sha512_state
import libsodium.crypto_hash_sha512_update
import platform.posix.malloc

actual typealias Sha256State = crypto_hash_sha256_state
actual typealias Sha512State = crypto_hash_sha512_state

actual object Hash {
    //Not present in Lazy Sodium
//    actual fun hash(data: UByteArray): UByteArray {
//        val hashResult = UByteArray(crypto_hash_BYTES)
//        val hashResultPinned = hashResult.pin()
//        val dataPinned = data.pin()
//        crypto_hash(hashResultPinned.toPtr(), dataPinned.toPtr(), data.size.convert())
//        hashResultPinned.unpin()
//        dataPinned.unpin()
//
//        return hashResult
//    }

    actual fun sha256(data: UByteArray): UByteArray {
        val hashResult = UByteArray(crypto_hash_sha256_BYTES)
        val hashResultPinned = hashResult.pin()
        val dataPinned = data.pin()
        crypto_hash_sha256(hashResultPinned.toPtr(), dataPinned.toPtr(), data.size.convert())
        hashResultPinned.unpin()
        dataPinned.unpin()

        return hashResult
    }

    actual fun sha256Init(): Sha256State {
        val stateAllocated = malloc(Sha256State.size.convert())
        val statePointed = stateAllocated!!.reinterpret<Sha256State>().pointed
        crypto_hash_sha256_init(statePointed.ptr)
        return statePointed
    }

    actual fun sha256Update(state: Sha256State, data: UByteArray) {
        val dataPinned = data.pin()
        crypto_hash_sha256_update(state.ptr, dataPinned.toPtr(), data.size.convert())
    }

    actual fun sha256Final(state: Sha256State): UByteArray {
        val hashResult = UByteArray(crypto_hash_sha256_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_hash_sha256_final(state.ptr, hashResultPinned.toPtr())
        return hashResult
    }

    actual fun sha512(data: UByteArray): UByteArray {
        val hashResult = UByteArray(crypto_hash_sha512_BYTES)
        val hashResultPinned = hashResult.pin()
        val dataPinned = data.pin()
        crypto_hash_sha512(hashResultPinned.toPtr(), dataPinned.toPtr(), data.size.convert())
        hashResultPinned.unpin()
        dataPinned.unpin()

        return hashResult
    }

    actual fun sha512Init(): Sha512State {
        val stateAllocated = malloc(Sha512State.size.convert())
        val statePointed = stateAllocated!!.reinterpret<Sha512State>().pointed
        crypto_hash_sha512_init(statePointed.ptr)
        return statePointed
    }

    actual fun sha512Update(state: Sha512State, data: UByteArray) {
        val dataPinned = data.pin()
        crypto_hash_sha512_update(state.ptr, dataPinned.toPtr(), data.size.convert())
    }

    actual fun sha512Final(state: Sha512State): UByteArray {
        val hashResult = UByteArray(crypto_hash_sha512_BYTES)
        val hashResultPinned = hashResult.pin()
        crypto_hash_sha512_final(state.ptr, hashResultPinned.toPtr())
        return hashResult
    }


}
