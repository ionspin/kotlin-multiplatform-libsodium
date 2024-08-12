package com.ionspin.kotlin.crypto.ristretto255

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

actual abstract class Ristretto255LowLevel actual constructor() {
  actual fun isValidPoint(encoded: UByteArray): Boolean =
    getSodium().crypto_core_ristretto255_is_valid_point(encoded.toUint8Array())

  actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_add(p.toUint8Array(), q.toUint8Array())

    return result.toUByteArray()
  }

  actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_sub(p.toUint8Array(), q.toUint8Array())

    return result
  }

  actual fun encodedPointFromHash(hash: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_from_hash(hash.toUint8Array())

    return result.toUByteArray()
  }

  actual fun randomEncodedPoint(): UByteArray {
    val result = getSodium().crypto_core_ristretto255_random()

    return result.toUByteArray()
  }

  actual fun randomEncodedScalar(): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_random()

    return result.toUByteArray()
  }

  actual fun invert(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_invert(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun negate(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_negate(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun complement(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_complement(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_add(x.toUint8Array(), y.toUint8Array())

    return result.toUByteArray()
  }

  actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_sub(x.toUint8Array(), y.toUint8Array())

    return result.toUByteArray()
  }

  actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_mul(x.toUint8Array(), y.toUint8Array())

    return result.toUByteArray()
  }

  actual fun reduce(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ristretto255_scalar_reduce(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
    val result = getSodium().crypto_scalarmult_ristretto255(n.toUint8Array(), p.toUint8Array())

    return result.toUByteArray()
  }

  actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
    val result = getSodium().crypto_scalarmult_ristretto255_base(n.toUint8Array())

    return result.toUByteArray()
  }
}