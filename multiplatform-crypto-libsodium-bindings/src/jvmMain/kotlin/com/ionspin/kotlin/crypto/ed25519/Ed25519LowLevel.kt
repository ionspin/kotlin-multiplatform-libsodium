package com.ionspin.kotlin.crypto.ed25519

import com.ionspin.kotlin.crypto.GeneralLibsodiumException.Companion.ensureLibsodiumSuccess
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object Ed25519LowLevel {
    actual fun isValidPoint(encoded: UByteArray): Boolean =
        sodiumJna.crypto_core_ed25519_is_valid_point(encoded.asByteArray()) == 1

    actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        sodiumJna.crypto_core_ed25519_add(result.asByteArray(), p.asByteArray(), q.asByteArray())
            .ensureLibsodiumSuccess()

        return result
    }

    actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        sodiumJna.crypto_core_ed25519_sub(result.asByteArray(), p.asByteArray(), q.asByteArray())
            .ensureLibsodiumSuccess()

        return result
    }

    actual fun pointFromUniform(uniform: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        sodiumJna.crypto_core_ed25519_from_uniform(result.asByteArray(), uniform.asByteArray())
            .ensureLibsodiumSuccess()

        return result
    }

    actual fun randomPoint(): UByteArray = UByteArray(crypto_core_ed25519_BYTES).also {
        sodiumJna.crypto_core_ed25519_random(it.asByteArray())
    }

    actual fun randomScalar(): UByteArray = UByteArray(crypto_core_ed25519_SCALARBYTES).also {
        sodiumJna.crypto_core_ed25519_scalar_random(it.asByteArray())
    }

    actual fun invertScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        sodiumJna.crypto_core_ed25519_scalar_invert(result.asByteArray(), scalar.asByteArray()).ensureLibsodiumSuccess()

        return result
    }

    actual fun negateScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        sodiumJna.crypto_core_ed25519_scalar_negate(result.asByteArray(), scalar.asByteArray())

        return result
    }

    actual fun complementScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        sodiumJna.crypto_core_ed25519_scalar_complement(result.asByteArray(), scalar.asByteArray())

        return result
    }

    actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        sodiumJna.crypto_core_ed25519_scalar_add(result.asByteArray(), x.asByteArray(), y.asByteArray())

        return result
    }

    actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        sodiumJna.crypto_core_ed25519_scalar_sub(result.asByteArray(), x.asByteArray(), y.asByteArray())

        return result
    }

    actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        sodiumJna.crypto_core_ed25519_scalar_mul(result.asByteArray(), x.asByteArray(), y.asByteArray())

        return result
    }

    actual fun reduceScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        sodiumJna.crypto_core_ed25519_scalar_reduce(result.asByteArray(), scalar.asByteArray())

        return result
    }

    actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        sodiumJna.crypto_scalarmult_ed25519(result.asByteArray(), n.asByteArray(), p.asByteArray())
            .ensureLibsodiumSuccess()

        return result
    }

    actual fun scalarMultiplicationNoClamp(n: UByteArray, p: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        sodiumJna.crypto_scalarmult_ed25519_noclamp(result.asByteArray(), n.asByteArray(), p.asByteArray())
            .ensureLibsodiumSuccess()

        return result
    }

    actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        sodiumJna.crypto_scalarmult_ed25519_base(result.asByteArray(), n.asByteArray())
            .ensureLibsodiumSuccess()

        return result
    }

    actual fun scalarMultiplicationBaseNoClamp(n: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        sodiumJna.crypto_scalarmult_ed25519_base_noclamp(result.asByteArray(), n.asByteArray())
            .ensureLibsodiumSuccess()

        return result
    }
}