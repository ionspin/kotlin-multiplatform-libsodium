package debug.test

import Sha256State
import kotlin.Int
import kotlin.UByteArray

expect class Sha256State

expect class Hashing {
  fun init(state: Sha256State): Int
}

expect class GenericHash {
  fun init(
    state: UByteArray,
    key: UByteArray,
    outlen: Int
  ): Int
}
