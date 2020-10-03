package com.ionspin.kotlin.crypto.signature

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

actual typealias SignatureState = Any

actual object Signature {
    actual fun init(): SignatureState {
        return getSodium().crypto_sign_init()
    }

    actual fun update(state: SignatureState, data: UByteArray) {
        getSodium().crypto_sign_update(state, data.toUInt8Array())
    }

    actual fun finalCreate(
        state: SignatureState,
        secretKey: UByteArray
    ): UByteArray {
        return getSodium().crypto_sign_final_create(
            state,
            secretKey.toUInt8Array()
        ).toUByteArray()
    }

    actual fun finalVerify(
        state: SignatureState,
        signature: UByteArray,
        publicKey: UByteArray
    ) {
        val verificationResult = getSodium().crypto_sign_final_verify(
            state,
            signature.toUInt8Array(),
            publicKey.toUInt8Array()
        )
        if (verificationResult == false) {
            throw InvalidSignatureException()
        }
    }

    /**
     * The crypto_sign_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_sign_PUBLICKEYBYTES bytes) and the secret key into sk (crypto_sign_SECRETKEYBYTES bytes).
     */
    actual fun keypair(): SignatureKeyPair {
        val keypair = getSodium().crypto_sign_keypair()
        return SignatureKeyPair(
            (keypair.publicKey as Uint8Array).toUByteArray(),
            (keypair.privateKey as Uint8Array).toUByteArray()
        )
    }

    /**
     * The crypto_sign_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_sign_PUBLICKEYBYTES bytes) and the secret key into sk (crypto_sign_SECRETKEYBYTES bytes).
     * Using crypto_sign_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_sign_SEEDBYTES bytes).
     */
    actual fun seedKeypair(seed: UByteArray): SignatureKeyPair {
        val keypair = getSodium().crypto_sign_seed_keypair(seed.toUInt8Array())
        return SignatureKeyPair(
            (keypair.publicKey as Uint8Array).toUByteArray(),
            (keypair.privateKey as Uint8Array).toUByteArray()
        )
    }

    /**
     * The crypto_sign() function prepends a signature to a message m whose length is mlen bytes, using the secret key sk.
     * The signed message, which includes the signature + a plain copy of the message, is put into sm, and is crypto_sign_BYTES + mlen bytes long.
     */
    actual fun sign(message: UByteArray, secretKey: UByteArray): UByteArray {
        return getSodium().crypto_sign(
            message.toUInt8Array(),
            secretKey.toUInt8Array()
        ).toUByteArray()
    }

    /**
     * The crypto_sign_open() function checks that the signed message sm whose length is smlen bytes has a valid signature for the public key pk.
     * If the signature is doesn't appear to be valid, the function throws an exception
     */
    actual fun open(signedMessage: UByteArray, publicKey: UByteArray): UByteArray {
        try {
            return getSodium().crypto_sign_open(
                signedMessage.toUInt8Array(),
                publicKey.toUInt8Array()
            ).toUByteArray()
        } catch (error : Throwable) {
            throw InvalidSignatureException()
        }
    }

    /**
     * In detached mode, the signature is stored without attaching a copy of the original message to it.
     * The crypto_sign_detached() function signs the message m whose length is mlen bytes, using the secret key sk,
     * and puts the signature into sig, which can be up to crypto_sign_BYTES bytes long.
     */
    actual fun detached(message: UByteArray, secretKey: UByteArray): UByteArray {
        return getSodium().crypto_sign_detached(
            message.toUInt8Array(),
            secretKey.toUInt8Array()
        ).toUByteArray()
    }

    /**
     * The crypto_sign_verify_detached() function verifies that sig is a valid signature for the message m whose length
     * is mlen bytes, using the signer's public key pk.
     */
    actual fun verifyDetached(signature: UByteArray, message: UByteArray, publicKey: UByteArray) {
        val verificationResult = getSodium().crypto_sign_verify_detached(
            signature.toUInt8Array(),
            message.toUInt8Array(),
            publicKey.toUInt8Array()
        )

        if (verificationResult == false) {
            throw InvalidSignatureException()
        }
    }

    /**
     * The crypto_sign_ed25519_pk_to_curve25519() function converts an Ed25519 public key ed25519_pk to an X25519 public key and stores it into x25519_pk.
     */
    actual fun ed25519PkToCurve25519(ed25519PublicKey: UByteArray): UByteArray {
        return getSodium().crypto_sign_ed25519_pk_to_curve25519(
            ed25519PublicKey.toUInt8Array()
        ).toUByteArray()
    }

    /**
     * The crypto_sign_ed25519_sk_to_curve25519() function converts an Ed25519 secret key ed25519_sk to an X25519 secret key and stores it into x25519_sk.
     */
    actual fun ed25519SkToCurve25519(ed25519SecretKey: UByteArray): UByteArray {
        return getSodium().crypto_sign_ed25519_sk_to_curve25519(
            ed25519SecretKey.toUInt8Array()
        ).toUByteArray()
    }

    /**
     * The secret key actually includes the seed (either a random seed or the one given to crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    actual fun ed25519SkToSeed(secretKey: UByteArray): UByteArray {
        return getSodium().crypto_sign_ed25519_sk_to_seed(
            secretKey.toUInt8Array()
        ).toUByteArray()
    }

    /**
     * The secret key actually includes the seed (either a random seed or the one given to crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    actual fun ed25519SkToPk(secretKey: UByteArray): UByteArray {
        return getSodium().crypto_sign_ed25519_sk_to_pk(
            secretKey.toUInt8Array()
        ).toUByteArray()
    }

}