package com.ionspin.kotlin.crypto.ed25519

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

actual abstract class Ed25519LowLevel actual constructor() {
  actual fun isValidPoint(encoded: UByteArray): Boolean =
    getSodium().crypto_core_ed25519_is_valid_point(encoded.toUint8Array())

  actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_add(p.toUint8Array(), q.toUint8Array())

    return result.toUByteArray()
  }

  actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_sub(p.toUint8Array(), q.toUint8Array())

    return result
  }

  actual fun encodedPointFromHash(hash: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_from_hash(hash.toUint8Array())

    return result.toUByteArray()
  }

  actual fun encodedPointFromUniform(uniform: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_from_uniform(hash.toUint8Array())

    return result.toUByteArray()
  }

  actual fun randomEncodedPoint(): UByteArray {
    val result = getSodium().crypto_core_ed25519_random()

    return result.toUByteArray()
  }

  actual fun randomEncodedScalar(): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_random()

    return result.toUByteArray()
  }

  actual fun invertScalar(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_invert(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun negateScalar(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_negate(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun complementScalar(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_complement(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_add(x.toUint8Array(), y.toUint8Array())

    return result.toUByteArray()
  }

  actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_sub(x.toUint8Array(), y.toUint8Array())

    return result.toUByteArray()
  }

  actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_mul(x.toUint8Array(), y.toUint8Array())

    return result.toUByteArray()
  }

  actual fun reduceScalar(scalar: UByteArray): UByteArray {
    val result = getSodium().crypto_core_ed25519_scalar_reduce(scalar.toUint8Array())

    return result.toUByteArray()
  }

  actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
    val result = getSodium().crypto_scalarmult_ed25519(n.toUint8Array(), p.toUint8Array())

    return result.toUByteArray()
  }

  actual fun scalarMultiplicationNoClamp(n: UByteArray, p: UByteArray): UByteArray {
    val result = getSodium().crypto_scalarmult_ed25519_noclamp(n.toUint8Array(), p.toUint8Array())

    return result.toUByteArray()
  }

  actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
    val result = getSodium().crypto_scalarmult_ed25519_base(n.toUint8Array())

    return result.toUByteArray()
  }

  actual fun scalarMultiplicationBaseNoClamp(n: UByteArray): UByteArray {
    val result = getSodium().crypto_scalarmult_ed25519_base_noclamp(n.toUint8Array())

    return result.toUByteArray()
  }
}