package debug.test

import kotlin.Int
import kotlin.UByteArray

expect class Sha256State

expect class Sha512State

expect class GenericHashState

expect class Crypto internal constructor() {
  fun crypto_hash_sha256_init_spec(): Sha256State

  fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray)

  fun crypto_hash_sha256_final(state: Sha256State): UByteArray

  fun crypto_hash_sha512_init(): Sha512State

  fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray)

  fun crypto_hash_sha512_final(state: Sha512State): UByteArray

  fun crypto_generichash_init(key: UByteArray, outlen: Int): GenericHashState
}
