package debug.test

import kotlin.Int
import kotlin.UByteArray
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.ptr
import kotlinx.cinterop.toCValues
import libsodium.crypto_generichash_blake2b_state
import libsodium.crypto_hash_sha256_state
import libsodium.crypto_hash_sha512_state

actual typealias Sha256State = crypto_hash_sha256_state

actual typealias Sha512State = crypto_hash_sha512_state

actual typealias GenericHashState = crypto_generichash_blake2b_state

actual class Crypto {
  actual fun crypto_hash_sha256_init(state: Sha256State): Int {
    println("Debug")
    return libsodium.crypto_hash_sha256_init(state.ptr)
  }

  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
    val pinnedInput = input.pin()
    libsodium.crypto_hash_sha256_update(state.ptr, pinnedInput.addressOf(0), input.size.convert())
    pinnedInput.unpin()
  }

  actual fun crypto_hash_sha256_final(state: Sha256State, out: UByteArray) {
    val pinnedOut = out.pin()
    libsodium.crypto_hash_sha256_final(state.ptr, pinnedOut.addressOf(0))
    pinnedOut.unpin()
  }

  actual fun crypto_hash_sha512_init(state: Sha512State): Int {
    println("Debug")
    return libsodium.crypto_hash_sha512_init(state.ptr)
  }

  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
    val pinnedInput = input.pin()
    libsodium.crypto_hash_sha512_update(state.ptr, pinnedInput.addressOf(0), input.size.convert())
    pinnedInput.unpin()
  }

  actual fun crypto_hash_sha512_final(state: Sha512State, out: UByteArray) {
    val pinnedOut = out.pin()
    libsodium.crypto_hash_sha512_final(state.ptr, pinnedOut.addressOf(0))
    pinnedOut.unpin()
  }

  actual fun crypto_generichash_init(
    state: GenericHashState,
    key: UByteArray,
    outlen: Int
  ): Int {
    val pinnedKey = key.pin()
    println("Debug")
    return libsodium.crypto_generichash_init(state.ptr, pinnedKey.addressOf(0), key.size.convert(),
        outlen.convert())
    pinnedKey.unpin()
  }
}
