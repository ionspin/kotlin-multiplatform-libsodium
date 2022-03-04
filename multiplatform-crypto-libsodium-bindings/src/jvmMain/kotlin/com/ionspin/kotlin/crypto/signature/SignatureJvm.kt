package com.ionspin.kotlin.crypto.signature

import com.ionspin.kotlin.crypto.Ed25519SignatureState
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual typealias SignatureState = Ed25519SignatureState

actual object Signature {
    actual fun init(): SignatureState {
        return SignatureState()
    }

    actual fun update(state: SignatureState, data: UByteArray) {
        sodiumJna.crypto_sign_update(state, data.asByteArray(), data.size.toLong())
    }

    actual fun finalCreate(
        state: SignatureState,
        secretKey: UByteArray
    ): UByteArray {
        val signature = UByteArray(crypto_sign_BYTES)
        sodiumJna.crypto_sign_final_create(
            state,
            signature.asByteArray(),
            null,
            secretKey.asByteArray()
        )
        return signature
    }

    actual fun finalVerify(
        state: SignatureState,
        signature: UByteArray,
        publicKey: UByteArray
    ) {
        val verificationResult = sodiumJna.crypto_sign_final_verify(
            state,
            signature.asByteArray(),
            publicKey.asByteArray()
        )
        if (verificationResult == -1) {
            throw InvalidSignatureException()
        }
    }

    /**
     * The crypto_sign_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_sign_PUBLICKEYBYTES bytes) and the secret key into sk (crypto_sign_SECRETKEYBYTES bytes).
     */
    actual fun keypair(): SignatureKeyPair {
        val publicKey = UByteArray(crypto_sign_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_sign_SECRETKEYBYTES)
        sodiumJna.crypto_sign_keypair(
            publicKey.asByteArray(),
            secretKey.asByteArray(),
        )
        return SignatureKeyPair(publicKey, secretKey)
    }

    /**
     * The crypto_sign_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_sign_PUBLICKEYBYTES bytes) and the secret key into sk (crypto_sign_SECRETKEYBYTES bytes).
     * Using crypto_sign_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_sign_SEEDBYTES bytes).
     */
    actual fun seedKeypair(seed: UByteArray): SignatureKeyPair {
        val publicKey = UByteArray(crypto_sign_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_sign_SECRETKEYBYTES)


        sodiumJna.crypto_sign_seed_keypair(
            publicKey.asByteArray(),
            secretKey.asByteArray(),
            seed.asByteArray()
        )
        return SignatureKeyPair(publicKey, secretKey)
    }

    /**
     * The sodiumJna.crypto_sign() function prepends a signature to a message m whose length is mlen bytes, using the secret key sk.
     * The signed message, which includes the signature + a plain copy of the message, is put into sm, and is sodiumJna.crypto_sign_BYTES + mlen bytes long.
     */
    actual fun sign(message: UByteArray, secretKey: UByteArray): UByteArray {
        val signedMessage = UByteArray(message.size + crypto_sign_BYTES)

        sodiumJna.crypto_sign(
            signedMessage.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            secretKey.asByteArray()
        )

        return signedMessage
    }

    /**
     * The sodiumJna.crypto_sign_open() function checks that the signed message sm whose length is smlen bytes has a valid signature for the public key pk.
     * If the signature is doesn't appear to be valid, the function throws an exception
     */
    actual fun open(signedMessage: UByteArray, publicKey: UByteArray): UByteArray {
        val message = UByteArray(signedMessage.size - crypto_sign_BYTES)

        val verificationResult = sodiumJna.crypto_sign_open(
            message.asByteArray(),
            null,
            signedMessage.asByteArray(),
            signedMessage.size.toLong(),
            publicKey.asByteArray()
        )
        if (verificationResult == -1) {
            throw InvalidSignatureException()
        }
        return message
    }

    /**
     * In detached mode, the signature is stored without attaching a copy of the original message to it.
     * The sodiumJna.crypto_sign_detached() function signs the message m whose length is mlen bytes, using the secret key sk,
     * and puts the signature into sig, which can be up to sodiumJna.crypto_sign_BYTES bytes long.
     */
    actual fun detached(message: UByteArray, secretKey: UByteArray): UByteArray {
        val signature = UByteArray(crypto_sign_BYTES)

        sodiumJna.crypto_sign_detached(
            signature.asByteArray(),
            null,
            message.asByteArray(),
            message.size.toLong(),
            secretKey.asByteArray()
        )

        return signature
    }

    /**
     * The sodiumJna.crypto_sign_verify_detached() function verifies that sig is a valid signature for the message m whose length
     * is mlen bytes, using the signer's public key pk.
     */
    actual fun verifyDetached(
        signature: UByteArray,
        message: UByteArray,
        publicKey: UByteArray
    ) {

        val verificationResult = sodiumJna.crypto_sign_verify_detached(
            signature.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            publicKey.asByteArray()
        )

        if (verificationResult == -1) {
            throw InvalidSignatureException()
        }
    }

    /**
     * The sodiumJna.crypto_sign_ed25519_pk_to_curve25519() function converts an Ed25519 public key ed25519_pk to an X25519 public key and stores it into x25519_pk.
     */
    actual fun ed25519PkToCurve25519(ed25519PublicKey: UByteArray) : UByteArray {
        val x25519PublicKey = UByteArray(crypto_scalarmult_curve25519_BYTES)
        sodiumJna.crypto_sign_ed25519_pk_to_curve25519(
            x25519PublicKey.asByteArray(),
            ed25519PublicKey.asByteArray()
        )
        return x25519PublicKey
    }

    actual fun ed25519SkToCurve25519(ed25519SecretKey: UByteArray) : UByteArray {
        val x25519SecretKey = UByteArray(crypto_scalarmult_curve25519_BYTES)
        sodiumJna.crypto_sign_ed25519_sk_to_curve25519(
            x25519SecretKey.asByteArray(),
            ed25519SecretKey.asByteArray()
        )
        return x25519SecretKey
    }

    /**
     * The secret key actually includes the seed (either a random seed or the one given to sodiumJna.crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    actual fun ed25519SkToSeed(secretKey: UByteArray): UByteArray {
        val seed = UByteArray(crypto_sign_SEEDBYTES)

        sodiumJna.crypto_sign_ed25519_sk_to_seed(
            seed.asByteArray(),
            secretKey.asByteArray()
        )

        return seed

    }

    /**
     * The secret key actually includes the seed (either a random seed or the one given to sodiumJna.crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    actual fun ed25519SkToPk(secretKey: UByteArray): UByteArray {
        val publicKey = UByteArray(crypto_sign_PUBLICKEYBYTES)

        sodiumJna.crypto_sign_ed25519_sk_to_pk(
            publicKey.asByteArray(),
            secretKey.asByteArray()
        )

        return publicKey
    }

}
