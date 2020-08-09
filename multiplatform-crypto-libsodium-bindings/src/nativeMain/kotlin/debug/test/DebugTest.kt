package debug.test

import kotlin.Byte
import kotlin.ByteArray
import kotlin.Int
import kotlin.UByteArray
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import libsodium.crypto_generichash_blake2b_state
import libsodium.crypto_hash_sha256_state
import libsodium.crypto_hash_sha512_state
import libsodium.sodium_malloc

actual typealias Sha256State = crypto_hash_sha256_state

actual typealias Sha512State = crypto_hash_sha512_state

actual typealias GenericHashState = crypto_generichash_blake2b_state

actual class Crypto internal actual constructor() {
  val _emitByte: Byte = 0

  val _emitByteArray: ByteArray = ByteArray(0)

  actual fun crypto_hash_sha256_init(): Sha256State {
    val allocated = sodium_malloc(debug.test.Sha256State.size.convert())!!
    val state = allocated.reinterpret<debug.test.Sha256State>().pointed
    println("Debug crypto_hash_sha256_init")
    libsodium.crypto_hash_sha256_init(state.ptr)
    return state
  }

  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
    println("Debug crypto_hash_sha256_update")
    val pinnedInput = input.pin()
    libsodium.crypto_hash_sha256_update(state.ptr, pinnedInput.addressOf(0), input.size.convert())
    pinnedInput.unpin()
  }

  actual fun crypto_hash_sha256_final(state: Sha256State): UByteArray {
    val out = UByteArray(32)
    println("Debug crypto_hash_sha256_final")
    val pinnedOut = out.pin()
    libsodium.crypto_hash_sha256_final(state.ptr, pinnedOut.addressOf(0))
    pinnedOut.unpin()
    return out
  }

  actual fun crypto_hash_sha512_init(): Sha512State {
    val allocated = sodium_malloc(debug.test.Sha512State.size.convert())!!
    val state = allocated.reinterpret<debug.test.Sha512State>().pointed
    println("Debug crypto_hash_sha512_init")
    libsodium.crypto_hash_sha512_init(state.ptr)
    return state
  }

  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
    println("Debug crypto_hash_sha512_update")
    val pinnedInput = input.pin()
    libsodium.crypto_hash_sha512_update(state.ptr, pinnedInput.addressOf(0), input.size.convert())
    pinnedInput.unpin()
  }

  actual fun crypto_hash_sha512_final(state: Sha512State): UByteArray {
    val out = UByteArray(64)
    println("Debug crypto_hash_sha512_final")
    val pinnedOut = out.pin()
    libsodium.crypto_hash_sha512_final(state.ptr, pinnedOut.addressOf(0))
    pinnedOut.unpin()
    return out
  }

  actual fun crypto_generichash_init(key: UByteArray, outlen: Int): GenericHashState {
    val allocated = sodium_malloc(debug.test.GenericHashState.size.convert())!!
    val state = allocated.reinterpret<debug.test.GenericHashState>().pointed
    println("Debug crypto_generichash_init")
    val pinnedKey = key.pin()
    libsodium.crypto_generichash_init(state.ptr, pinnedKey.addressOf(0), key.size.convert(),
        outlen.convert())
    pinnedKey.unpin()
    return state
  }
}