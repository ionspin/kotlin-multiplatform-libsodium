package debug.test

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import kotlin.Any
import kotlin.Int
import kotlin.UByteArray

actual typealias Sha256State = Any

actual typealias Sha512State = Any

actual typealias GenericHashState = Any

actual class Crypto {
  actual fun crypto_hash_sha256_init(state: Sha256State): Int {
    println("Debug")
    return getSodium().crypto_hash_sha256_init(state)
  }

  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
    println("Debug")
    getSodium().crypto_hash_sha256_update(state, input.toUInt8Array(), input.size.toLong())
  }

  actual fun crypto_hash_sha256_final(state: Sha256State, out: UByteArray) {
    println("Debug")
    getSodium().crypto_hash_sha256_final(state, out.toUInt8Array())
  }

  actual fun crypto_hash_sha512_init(state: Sha512State): Int {
    println("Debug")
    return getSodium().crypto_hash_sha512_init(state)
  }

  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
    println("Debug")
    getSodium().crypto_hash_sha512_update(state, input.toUInt8Array(), input.size.toLong())
  }

  actual fun crypto_hash_sha512_final(state: Sha512State, out: UByteArray) {
    println("Debug")
    getSodium().crypto_hash_sha512_final(state, out.toUInt8Array())
  }

  actual fun crypto_generichash_init(
    state: GenericHashState,
    key: UByteArray,
    outlen: Int
  ): Int {
    println("Debug")
    return getSodium().crypto_generichash_init(state, key.toUInt8Array(), key.size, outlen)
  }
}
