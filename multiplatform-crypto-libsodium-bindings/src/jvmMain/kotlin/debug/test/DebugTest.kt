package debug.test

import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.Hash
import kotlin.Int
import kotlin.UByteArray

val sodium: SodiumJava = SodiumJava()

actual typealias Sha256State = Hash.State256

actual class Hashing {
  actual fun crypto_hash_sha256_init(state: Sha256State): Int {
    println("Debug")
    return sodium.crypto_hash_sha256_init(state)
  }
}

actual class GenericHash {
  actual fun crypto_generichash_init(
    state: UByteArray,
    key: UByteArray,
    outlen: Int
  ): Int {
    println("Debug")
    return sodium.crypto_generichash_init(state.asByteArray(), key.asByteArray(), key.size, outlen)
  }
}
