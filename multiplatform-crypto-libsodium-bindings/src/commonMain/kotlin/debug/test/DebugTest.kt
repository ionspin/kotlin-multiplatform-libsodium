package debug.test

import kotlin.Int
import kotlin.UByteArray

expect class Sha256State

expect class Hashing {
  fun crypto_hash_sha256_init(state: Sha256State): Int
}

expect class GenericHash {
  fun crypto_generichash_init(
    state: UByteArray,
    key: UByteArray,
    outlen: Int
  ): Int
}
