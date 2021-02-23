package com.ionspin.kotlin.crypto.scalarmult

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object ScalarMultiplication {
    /**
     * This function can be used to compute a shared secret q given a user's secret key and another user's public key.
     * n is crypto_scalarmult_SCALARBYTES bytes long, p and the output are crypto_scalarmult_BYTES bytes long.
     * q represents the X coordinate of a point on the curve. As a result, the number of possible keys is limited to
     * the group size (≈2^252), which is smaller than the key space.
     * For this reason, and to mitigate subtle attacks due to the fact many (p, n) pairs produce the same result,
     * using the output of the multiplication q directly as a shared key is not recommended.
     * A better way to compute a shared key is h(q ‖ pk1 ‖ pk2), with pk1 and pk2 being the public keys.
     * By doing so, each party can prove what exact public key they intended to perform a key exchange with
     * (for a given public key, 11 other public keys producing the same shared secret can be trivially computed).
     * This can be achieved with the following code snippet:
     */
    actual fun scalarMultiplication(secretKeyN: UByteArray, publicKeyP: UByteArray): UByteArray {
        val result = UByteArray(crypto_scalarmult_BYTES)

        sodiumJna.crypto_scalarmult(result.asByteArray(), secretKeyN.asByteArray(), publicKeyP.asByteArray())


        return result
    }

    /**
     * Given a user's secret key n (crypto_scalarmult_SCALARBYTES bytes), the crypto_scalarmult_base() function
     * computes the user's public key and puts it into q (crypto_scalarmult_BYTES bytes).
     * crypto_scalarmult_BYTES and crypto_scalarmult_SCALARBYTES are provided for consistency,
     * but it is safe to assume that crypto_scalarmult_BYTES == crypto_scalarmult_SCALARBYTES.
     */
    actual fun scalarMultiplicationBase(
        secretKeyN: UByteArray
    ): UByteArray {
        val result = UByteArray(crypto_scalarmult_BYTES)

        sodiumJna.crypto_scalarmult_base(result.asByteArray(), secretKeyN.asByteArray())

        return result
    }

}
