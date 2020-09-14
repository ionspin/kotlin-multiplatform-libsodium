package com.ionspin.kotlin.crypto.signature

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 13/Sep/2020
 */
expect class SignatureState

data class SignatureKeyPair(val publicKey: UByteArray, val secretKey: UByteArray)

const val crypto_sign_BYTES = 64
const val crypto_sign_SEEDBYTES = 32
const val crypto_sign_PUBLICKEYBYTES = 32
const val crypto_sign_SECRETKEYBYTES = 64
const val crypto_scalarmult_curve25519_BYTES = 32

class InvalidSignatureException() : RuntimeException("Signature validation failed")

expect object Signature {
    fun init(): SignatureState
    fun update(state: SignatureState, data: UByteArray)
    fun finalCreate(state: SignatureState, secretKey: UByteArray): UByteArray
    fun finalVerify(state: SignatureState, signature: UByteArray, publicKey: UByteArray)

    /**
     * The crypto_sign_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_sign_PUBLICKEYBYTES bytes) and the secret key into sk (crypto_sign_SECRETKEYBYTES bytes).
     */
    fun keypair(): SignatureKeyPair

    /**
     * The crypto_sign_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_sign_PUBLICKEYBYTES bytes) and the secret key into sk (crypto_sign_SECRETKEYBYTES bytes).
     * Using crypto_sign_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_sign_SEEDBYTES bytes).
     */
    fun seedKeypair(seed: UByteArray): SignatureKeyPair

    /**
     * The crypto_sign() function prepends a signature to a message m whose length is mlen bytes, using the secret key sk.
     * The signed message, which includes the signature + a plain copy of the message, is put into sm, and is crypto_sign_BYTES + mlen bytes long.
     */
    fun sign(message : UByteArray, secretKey : UByteArray) : UByteArray

    /**
     * The crypto_sign_open() function checks that the signed message sm whose length is smlen bytes has a valid signature for the public key pk.
     * If the signature is doesn't appear to be valid, the function throws an exception
     */
    fun open(signedMessage: UByteArray, publicKey: UByteArray) : UByteArray

    /**
     * In detached mode, the signature is stored without attaching a copy of the original message to it.
     * The crypto_sign_detached() function signs the message m whose length is mlen bytes, using the secret key sk,
     * and puts the signature into sig, which can be up to crypto_sign_BYTES bytes long.
     */
    fun detached(message: UByteArray, secretKey: UByteArray): UByteArray

    /**
     * The crypto_sign_verify_detached() function verifies that sig is a valid signature for the message m whose length
     * is mlen bytes, using the signer's public key pk.
     */
    fun verifyDetached(signature: UByteArray, message: UByteArray, publicKey: UByteArray)
    /**
     * The crypto_sign_ed25519_pk_to_curve25519() function converts an Ed25519 public key ed25519_pk to an X25519 public key and stores it into x25519_pk.
     */
    fun ed25519PkToCurve25519(ed25519PublicKey: UByteArray) : UByteArray

    /**
     * The crypto_sign_ed25519_sk_to_curve25519() function converts an Ed25519 secret key ed25519_sk to an X25519 secret key and stores it into x25519_sk.
     */
    fun ed25519SkToCurve25519(ed25519SecretKey: UByteArray) : UByteArray

    /**
     * The secret key actually includes the seed (either a random seed or the one given to crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    fun ed25519SkToSeed(secretKey : UByteArray) : UByteArray
    /**
     * The secret key actually includes the seed (either a random seed or the one given to crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    fun ed25519SkToPk(secretKey: UByteArray) : UByteArray
}