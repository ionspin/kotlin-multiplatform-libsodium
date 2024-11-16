package com.ionspin.kotlin.crypto.ristretto255

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

actual object Ristretto255LowLevel {
    actual fun isValidPoint(encoded: UByteArray): Boolean =
        getSodium().crypto_core_ristretto255_is_valid_point(encoded.toUInt8Array())

    actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_add(p.toUInt8Array(), q.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_sub(p.toUInt8Array(), q.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun pointFromHash(hash: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_from_hash(hash.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun randomPoint(): UByteArray {
        val result = getSodium().crypto_core_ristretto255_random()

        return result.toUByteArray()
    }

    actual fun randomScalar(): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_random()

        return result.toUByteArray()
    }

    actual fun invertScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_invert(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun negateScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_negate(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun complementScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_complement(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_add(x.toUInt8Array(), y.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_sub(x.toUInt8Array(), y.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_mul(x.toUInt8Array(), y.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun reduceScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ristretto255_scalar_reduce(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
        val result = getSodium().crypto_scalarmult_ristretto255(n.toUInt8Array(), p.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
        val result = getSodium().crypto_scalarmult_ristretto255_base(n.toUInt8Array())

        return result.toUByteArray()
    }
}