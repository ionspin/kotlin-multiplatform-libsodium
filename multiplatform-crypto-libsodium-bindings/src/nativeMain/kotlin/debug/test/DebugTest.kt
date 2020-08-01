package debug.test

import kotlin.Int
import kotlin.UByteArray
import kotlinx.cinterop.convert
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
    println("Debug")
    libsodium.crypto_hash_sha256_update(state.ptr, input.toCValues(), input.size.convert())
  }

  actual fun crypto_hash_sha256_final(state: Sha256State, out: UByteArray) {
    println("Debug")
    libsodium.crypto_hash_sha256_final(state.ptr, out.toCValues())
  }

  actual fun crypto_hash_sha512_init(state: Sha512State): Int {
    println("Debug")
    return libsodium.crypto_hash_sha512_init(state.ptr)
  }

  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
    println("Debug")
    libsodium.crypto_hash_sha512_update(state.ptr, input.toCValues(), input.size.convert())
  }

  actual fun crypto_hash_sha512_final(state: Sha512State, out: UByteArray) {
    println("Debug")
    libsodium.crypto_hash_sha512_final(state.ptr, out.toCValues())
  }

  actual fun crypto_generichash_init(
    state: GenericHashState,
    key: UByteArray,
    outlen: Int
  ): Int {
    println("Debug")
    return libsodium.crypto_generichash_init(state.ptr, key.toCValues(), key.size.convert(),
        outlen.convert())
  }
}
