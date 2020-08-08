package debug.test

import ByteArray
import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.Hash
import kotlin.Int
import kotlin.UByteArray

val sodium: SodiumJava = SodiumJava()

actual typealias Sha256State = Hash.State256

actual typealias Sha512State = Hash.State512

actual typealias GenericHashState = ByteArray

actual class Crypto {
  actual fun crypto_hash_sha256_init(state: Sha256State): Sha256State {
    println("Debug")
    return sodium.crypto_hash_sha256_init(state)
  }

  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
    println("Debug")
    sodium.crypto_hash_sha256_update(state, input.asByteArray(), input.size.toLong())
  }

  actual fun crypto_hash_sha256_final(state: Sha256State, out: UByteArray): UByteArray {
    println("Debug")
    return sodium.crypto_hash_sha256_final(state, out.asByteArray())
  }

  actual fun crypto_hash_sha512_init(state: Sha512State): Sha512State {
    println("Debug")
    return sodium.crypto_hash_sha512_init(state)
  }

  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
    println("Debug")
    sodium.crypto_hash_sha512_update(state, input.asByteArray(), input.size.toLong())
  }

  actual fun crypto_hash_sha512_final(state: Sha512State, out: UByteArray): UByteArray {
    println("Debug")
    return sodium.crypto_hash_sha512_final(state, out.asByteArray())
  }

  actual fun crypto_generichash_init(
    state: GenericHashState,
    key: UByteArray,
    outlen: Int
  ): GenericHashState {
    println("Debug")
    return sodium.crypto_generichash_init(state, key.asByteArray(), key.size, outlen)
  }
}
