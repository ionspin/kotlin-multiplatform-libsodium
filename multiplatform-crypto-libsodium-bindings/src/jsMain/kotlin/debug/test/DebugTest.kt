package debug.test

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import kotlin.Any
import kotlin.Int
import kotlin.UByteArray

actual typealias Sha256State = Any

actual typealias Sha512State = Any

actual typealias GenericHashState = Any

actual class Crypto internal actual constructor() {
  actual fun crypto_hash_sha256_init_spec(): dynamic {
    println("Debug")
    return getSodium().crypto_hash_sha256_init()
  }

  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
    println("Debug")
    getSodium().crypto_hash_sha256_update(state, input.toUInt8Array(), )
  }

  actual fun crypto_hash_sha256_final(state: Sha256State): UByteArray {
    println("Debug")
    return getSodium().crypto_hash_sha256_final(state).toUByteArray()
  }

  actual fun crypto_hash_sha512_init(): dynamic {
    println("Debug")
    return getSodium().crypto_hash_sha512_init()
  }

  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
    println("Debug")
    getSodium().crypto_hash_sha512_update(state, input.toUInt8Array(), )
  }

  actual fun crypto_hash_sha512_final(state: Sha512State): UByteArray {
    println("Debug")
    return getSodium().crypto_hash_sha512_final(state).toUByteArray()
  }

  actual fun crypto_generichash_init(key: UByteArray, outlen: Int): dynamic {
    println("Debug")
    return getSodium().crypto_generichash_init(key.toUInt8Array(), outlen)
  }
}
