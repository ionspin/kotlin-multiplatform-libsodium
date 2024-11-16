package com.ionspin.kotlin.crypto.ed25519

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array

actual object Ed25519LowLevel {
    actual fun isValidPoint(encoded: UByteArray): Boolean =
        getSodium().crypto_core_ed25519_is_valid_point(encoded.toUInt8Array())

    actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_add(p.toUInt8Array(), q.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_sub(p.toUInt8Array(), q.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun pointFromUniform(uniform: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_from_uniform(uniform.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun randomPoint(): UByteArray {
        val result = getSodium().crypto_core_ed25519_random()

        return result.toUByteArray()
    }

    actual fun randomScalar(): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_random()

        return result.toUByteArray()
    }

    actual fun invertScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_invert(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun negateScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_negate(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun complementScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_complement(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_add(x.toUInt8Array(), y.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_sub(x.toUInt8Array(), y.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_mul(x.toUInt8Array(), y.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun reduceScalar(scalar: UByteArray): UByteArray {
        val result = getSodium().crypto_core_ed25519_scalar_reduce(scalar.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
        val result = getSodium().crypto_scalarmult_ed25519(n.toUInt8Array(), p.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun scalarMultiplicationNoClamp(n: UByteArray, p: UByteArray): UByteArray {
        val result = getSodium().crypto_scalarmult_ed25519_noclamp(n.toUInt8Array(), p.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
        val result = getSodium().crypto_scalarmult_ed25519_base(n.toUInt8Array())

        return result.toUByteArray()
    }

    actual fun scalarMultiplicationBaseNoClamp(n: UByteArray): UByteArray {
        val result = getSodium().crypto_scalarmult_ed25519_base_noclamp(n.toUInt8Array())

        return result.toUByteArray()
    }
}