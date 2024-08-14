package com.ionspin.kotlin.crypto.ristretto255

import com.ionspin.kotlin.crypto.GeneralLibsodiumException.Companion.ensureLibsodiumSuccess
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna
import com.ionspin.kotlin.crypto.ed25519.HashToCurveAlgorithm
import com.ionspin.kotlin.crypto.util.toCString

actual abstract class Ristretto255LowLevel actual constructor() {
  actual fun isValidPoint(encoded: UByteArray): Boolean =
    sodiumJna.crypto_core_ristretto255_is_valid_point(encoded.asByteArray()) == 1

  actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    sodiumJna.crypto_core_ristretto255_add(result.asByteArray(), p.asByteArray(), q.asByteArray())
      .ensureLibsodiumSuccess()

    return result
  }

  actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    sodiumJna.crypto_core_ristretto255_sub(result.asByteArray(), p.asByteArray(), q.asByteArray())
      .ensureLibsodiumSuccess()

    return result
  }

  actual fun encodedPointFromHash(hash: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    sodiumJna.crypto_core_ristretto255_from_hash(result.asByteArray(), hash.asByteArray())

    return result
  }

  actual fun encodedPointFromString(ctx: String?, msg: UByteArray, hashAlg: HashToCurveAlgorithm): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)
    val ctxEncoded = ctx?.toCString()

    sodiumJna.crypto_core_ristretto255_from_string(
      result.asByteArray(),
      ctxEncoded?.asByteArray(),
      msg.asByteArray(),
      msg.size,
      hashAlg.id
    ).ensureLibsodiumSuccess()

    return result
  }

  actual fun encodedPointFromStringRo(ctx: String?, msg: UByteArray, hashAlg: HashToCurveAlgorithm): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)
    val ctxEncoded = ctx?.toCString()

    sodiumJna.crypto_core_ristretto255_from_string_ro(
      result.asByteArray(),
      ctxEncoded?.asByteArray(),
      msg.asByteArray(),
      msg.size,
      hashAlg.id
    ).ensureLibsodiumSuccess()

    return result
  }

  actual fun randomEncodedPoint(): UByteArray = UByteArray(crypto_core_ristretto255_BYTES).also {
    sodiumJna.crypto_core_ristretto255_random(it.asByteArray())
  }

  actual fun randomEncodedScalar(): UByteArray = UByteArray(crypto_core_ristretto255_SCALARBYTES).also {
    sodiumJna.crypto_core_ristretto255_scalar_random(it.asByteArray())
  }

  actual fun invertScalar(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    sodiumJna.crypto_core_ristretto255_scalar_invert(result.asByteArray(), scalar.asByteArray()).ensureLibsodiumSuccess()

    return result
  }

  actual fun negateScalar(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    sodiumJna.crypto_core_ristretto255_scalar_negate(result.asByteArray(), scalar.asByteArray())

    return result
  }

  actual fun complementScalar(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    sodiumJna.crypto_core_ristretto255_scalar_complement(result.asByteArray(), scalar.asByteArray())

    return result
  }

  actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    sodiumJna.crypto_core_ristretto255_scalar_add(result.asByteArray(), x.asByteArray(), y.asByteArray())

    return result
  }

  actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    sodiumJna.crypto_core_ristretto255_scalar_sub(result.asByteArray(), x.asByteArray(), y.asByteArray())

    return result
  }

  actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    sodiumJna.crypto_core_ristretto255_scalar_mul(result.asByteArray(), x.asByteArray(), y.asByteArray())

    return result
  }

  actual fun reduceScalar(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    sodiumJna.crypto_core_ristretto255_scalar_reduce(result.asByteArray(), scalar.asByteArray())

    return result
  }

  actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    sodiumJna.crypto_scalarmult_ristretto255(result.asByteArray(), n.asByteArray(), p.asByteArray())
      .ensureLibsodiumSuccess()

    return result
  }

  actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    sodiumJna.crypto_scalarmult_ristretto255_base(result.asByteArray(), n.asByteArray())
      .ensureLibsodiumSuccess()

    return result
  }
}