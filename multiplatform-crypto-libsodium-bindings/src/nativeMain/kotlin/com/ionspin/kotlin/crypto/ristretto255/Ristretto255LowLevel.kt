package com.ionspin.kotlin.crypto.ristretto255

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.pin


actual abstract class Ristretto255LowLevel actual constructor() {
  actual fun isValidPoint(encoded: UByteArray): Boolean {
    return crypto_core_ristretto255_is_valid_point(encoded.pin().toPtr()) == 1
  }

  actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    crypto_core_ristretto255_add(result.pin().toPtr(), p.pin().toPtr(), q.pin().toPtr())
      .ensureLibsodiumSuccess()

    return result
  }

  actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    crypto_core_ristretto255_sub(result.pin().toPtr(), p.pin().toPtr(), q.pin().toPtr())
      .ensureLibsodiumSuccess()

    return result
  }

  actual fun encodedPointFromHash(hash: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    crypto_core_ristretto255_from_hash(result.pin().toPtr(), hash.pin().toPtr())

    return result
  }

  actual fun randomEncodedPoint(): UByteArray = UByteArray(crypto_core_ristretto255_BYTES).also {
    crypto_core_ristretto255_random(it.pin().toPtr())
  }

  actual fun randomEncodedScalar(): UByteArray = UByteArray(crypto_core_ristretto255_SCALARBYTES).also {
    crypto_core_ristretto255_scalar_random(it.pin().toPtr())
  }

  actual fun invert(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    crypto_core_ristretto255_scalar_invert(result.pin().toPtr(), scalar.pin().toPtr()).ensureLibsodiumSuccess()

    return result
  }

  actual fun negate(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    crypto_core_ristretto255_scalar_negate(result.pin().toPtr(), scalar.pin().toPtr())

    return result
  }

  actual fun complement(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    crypto_core_ristretto255_scalar_complement(result.pin().toPtr(), scalar.pin().toPtr())

    return result
  }

  actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    crypto_core_ristretto255_scalar_add(result.pin().toPtr(), x.pin().toPtr(), y.pin().toPtr())

    return result
  }

  actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    crypto_core_ristretto255_scalar_sub(result.pin().toPtr(), x.pin().toPtr(), y.pin().toPtr())

    return result
  }

  actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    crypto_core_ristretto255_scalar_mul(result.pin().toPtr(), x.pin().toPtr(), y.pin().toPtr())

    return result
  }

  actual fun reduce(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    crypto_core_ristretto255_scalar_reduce(result.pin().toPtr(), scalar.pin().toPtr())

    return result
  }

  actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    crypto_scalarmult_ristretto255(result.pin().toPtr(), n.pin().toPtr(), p.pin().toPtr())

    return result
  }

  actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    crypto_scalarmult_ristretto255_base(result.pin().toPtr(), n.pin().toPtr())

    return result
  }
}