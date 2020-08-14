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

actual typealias SecretStreamState = Any

actual class Crypto internal actual constructor() {
  /**
   * Initialize the SHA256 hash
   * returns sha 256 state
   */
  actual fun crypto_hash_sha256_init(): dynamic {
    println("Debug crypto_hash_sha256_init")
    val result  = js("getSodium().crypto_hash_sha256_init()")
        return result
  }

  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
    println("Debug crypto_hash_sha256_update")
    getSodium().crypto_hash_sha256_update(state, input.toUInt8Array(), )
  }

  actual fun crypto_hash_sha256_final(state: Sha256State): UByteArray {
    println("Debug crypto_hash_sha256_final")
    return getSodium().crypto_hash_sha256_final(state).toUByteArray()
  }

  actual fun crypto_hash_sha512_init(): dynamic {
    println("Debug crypto_hash_sha512_init")
    val result  = js("getSodium().crypto_hash_sha512_init()")
        return result
  }

  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
    println("Debug crypto_hash_sha512_update")
    getSodium().crypto_hash_sha512_update(state, input.toUInt8Array(), )
  }

  actual fun crypto_hash_sha512_final(state: Sha512State): UByteArray {
    println("Debug crypto_hash_sha512_final")
    return getSodium().crypto_hash_sha512_final(state).toUByteArray()
  }

  actual fun crypto_generichash_init(key: UByteArray, outlen: Int): dynamic {
    println("Debug crypto_generichash_init")
    return getSodium().crypto_generichash_init(key.toUInt8Array(), outlen)
  }

  actual fun crypto_secretstream_xchacha20poly1305_init_push(key: UByteArray): dynamic {
    println("Debug crypto_secretstream_xchacha20poly1305_init_push")

  }
}
