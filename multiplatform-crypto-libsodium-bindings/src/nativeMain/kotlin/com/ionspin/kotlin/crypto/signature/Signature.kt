package com.ionspin.kotlin.crypto.signature

import com.ionspin.kotlin.crypto.GeneralLibsodiumException.Companion.ensureLibsodiumSuccess
import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import libsodium.crypto_sign
import libsodium.crypto_sign_detached
import libsodium.crypto_sign_ed25519_pk_to_curve25519
import libsodium.crypto_sign_ed25519_sk_to_curve25519
import libsodium.crypto_sign_ed25519_sk_to_pk
import libsodium.crypto_sign_ed25519_sk_to_seed
import libsodium.crypto_sign_ed25519ph_state
import libsodium.crypto_sign_final_create
import libsodium.crypto_sign_final_verify
import libsodium.crypto_sign_init
import libsodium.crypto_sign_keypair
import libsodium.crypto_sign_open
import libsodium.crypto_sign_seed_keypair
import libsodium.crypto_sign_update
import libsodium.crypto_sign_verify_detached
import platform.posix.malloc

actual typealias SignatureState = crypto_sign_ed25519ph_state

actual object Signature {
    actual fun init(): SignatureState {
        val stateAllocated = malloc(SignatureState.size.convert())
        val statePointed = stateAllocated!!.reinterpret<SignatureState>().pointed
        crypto_sign_init(statePointed.ptr).ensureLibsodiumSuccess()
        return statePointed
    }

    actual fun update(state: SignatureState, data: UByteArray) {
        val dataPinned = data.pin()
        crypto_sign_update(state.ptr, dataPinned.toPtr(), data.size.convert()).ensureLibsodiumSuccess()
        dataPinned.unpin()
    }

    actual fun finalCreate(
        state: SignatureState,
        secretKey: UByteArray
    ): UByteArray {
        val signature = UByteArray(crypto_sign_BYTES)
        val secretKeyPinned = secretKey.pin()
        val signaturePinned = signature.pin()
        crypto_sign_final_create(
            state.ptr,
            signaturePinned.toPtr(),
            null,
            secretKeyPinned.toPtr()
        ).ensureLibsodiumSuccess()
        secretKeyPinned.unpin()
        signaturePinned.unpin()
        return signature
    }

    actual fun finalVerify(
        state: SignatureState,
        signature: UByteArray,
        publicKey: UByteArray
    ) {
        val signaturePinned = signature.pin()
        val publicKeyPinned = publicKey.pin()
        val verificationResult = crypto_sign_final_verify(
            state.ptr,
            signaturePinned.toPtr(),
            publicKeyPinned.toPtr()
        )

        signaturePinned.unpin()
        publicKeyPinned.unpin()

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
        val publicKeyPinned = publicKey.pin()
        val secretKeyPinned = secretKey.pin()
        crypto_sign_keypair(
            publicKeyPinned.toPtr(),
            secretKeyPinned.toPtr(),
        ).ensureLibsodiumSuccess()
        publicKeyPinned.unpin()
        secretKeyPinned.unpin()
        return SignatureKeyPair(publicKey, secretKey)
    }

    /**
     * The crypto_sign_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_sign_PUBLICKEYBYTES bytes) and the secret key into sk (crypto_sign_SECRETKEYBYTES bytes).
     * Using crypto_sign_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_sign_SEEDBYTES bytes).
     */
    actual fun seedKeypair(seed: UByteArray): SignatureKeyPair {
        val seedPinned = seed.pin()
        val publicKey = UByteArray(crypto_sign_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_sign_SECRETKEYBYTES)
        val publicKeyPinned = publicKey.pin()
        val secretKeyPinned = secretKey.pin()
        crypto_sign_seed_keypair(
            publicKeyPinned.toPtr(),
            secretKeyPinned.toPtr(),
            seedPinned.toPtr()
        ).ensureLibsodiumSuccess()
        seedPinned.unpin()
        publicKeyPinned.unpin()
        secretKeyPinned.unpin()
        return SignatureKeyPair(publicKey, secretKey)
    }

    /**
     * The crypto_sign() function prepends a signature to a message m whose length is mlen bytes, using the secret key sk.
     * The signed message, which includes the signature + a plain copy of the message, is put into sm, and is crypto_sign_BYTES + mlen bytes long.
     */
    actual fun sign(message: UByteArray, secretKey: UByteArray): UByteArray {
        val signedMessage = UByteArray(message.size + crypto_sign_BYTES)
        val signedMessagePinned = signedMessage.pin()
        val messagePinned = message.pin()
        val secretKeyPinned = secretKey.pin()
        crypto_sign(
            signedMessagePinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            secretKeyPinned.toPtr()
        ).ensureLibsodiumSuccess()
        signedMessagePinned.unpin()
        messagePinned.unpin()
        secretKeyPinned.unpin()

        return signedMessage
    }

    /**
     * The crypto_sign_open() function checks that the signed message sm whose length is smlen bytes has a valid signature for the public key pk.
     * If the signature is doesn't appear to be valid, the function throws an exception
     */
    actual fun open(signedMessage: UByteArray, publicKey: UByteArray): UByteArray {
        val message = UByteArray(signedMessage.size - crypto_sign_BYTES)
        val messagePinned = message.pin()
        val signedMessagePinned = signedMessage.pin()
        val publicKeyPinned = publicKey.pin()

        val verificationResult = crypto_sign_open(
            messagePinned.toPtr(),
            null,
            signedMessagePinned.toPtr(),
            signedMessage.size.convert(),
            publicKeyPinned.toPtr()
        )
        messagePinned.unpin()
        signedMessagePinned.unpin()
        publicKeyPinned.unpin()
        if (verificationResult == -1) {
            throw InvalidSignatureException()
        }
        return message
    }

    /**
     * In detached mode, the signature is stored without attaching a copy of the original message to it.
     * The crypto_sign_detached() function signs the message m whose length is mlen bytes, using the secret key sk,
     * and puts the signature into sig, which can be up to crypto_sign_BYTES bytes long.
     */
    actual fun detached(message: UByteArray, secretKey: UByteArray): UByteArray {
        val signature = UByteArray(crypto_sign_BYTES)
        val signaturePinned = signature.pin()
        val messagePinned = message.pin()
        val secretKeyPinned = secretKey.pin()
        crypto_sign_detached(
            signaturePinned.toPtr(),
            null,
            messagePinned.toPtr(),
            message.size.convert(),
            secretKeyPinned.toPtr()
        ).ensureLibsodiumSuccess()
        signaturePinned.unpin()
        messagePinned.unpin()
        secretKeyPinned.unpin()

        return signature
    }

    /**
     * The crypto_sign_verify_detached() function verifies that sig is a valid signature for the message m whose length
     * is mlen bytes, using the signer's public key pk.
     */
    actual fun verifyDetached(
        signature: UByteArray,
        message: UByteArray,
        publicKey: UByteArray
    ) {
        val signaturePinned = signature.pin()
        val messagePinned = message.pin()
        val publicKeyPinned = publicKey.pin()
        val verificationResult = crypto_sign_verify_detached(
            signaturePinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            publicKeyPinned.toPtr()
        )
        signaturePinned.unpin()
        messagePinned.unpin()
        publicKeyPinned.unpin()

        if (verificationResult == -1) {
            throw InvalidSignatureException()
        }
    }

    /**
     * The crypto_sign_ed25519_pk_to_curve25519() function converts an Ed25519 public key ed25519_pk to an X25519 public key and stores it into x25519_pk.
     */
    actual fun ed25519PkToCurve25519(ed25519PublicKey: UByteArray) : UByteArray {
        val x25519PublicKey = UByteArray(crypto_scalarmult_curve25519_BYTES)
        val x25519PublicKeyPinned = x25519PublicKey.pin()
        val ed25519PublicKeyPinned = ed25519PublicKey.pin()
        crypto_sign_ed25519_pk_to_curve25519(
            x25519PublicKeyPinned.toPtr(),
            ed25519PublicKeyPinned.toPtr()
        ).ensureLibsodiumSuccess()
        x25519PublicKeyPinned.unpin()
        ed25519PublicKeyPinned.unpin()
        return x25519PublicKey
    }

    actual fun ed25519SkToCurve25519(ed25519SecretKey: UByteArray) : UByteArray {
        val x25519SecretKey = UByteArray(crypto_scalarmult_curve25519_BYTES)
        val x25519SecretKeyPinned = x25519SecretKey.pin()
        val ed25519SecretKeyPinned = ed25519SecretKey.pin()
        crypto_sign_ed25519_sk_to_curve25519(
            x25519SecretKeyPinned.toPtr(),
            ed25519SecretKeyPinned.toPtr()
        ).ensureLibsodiumSuccess()
        x25519SecretKeyPinned.unpin()
        ed25519SecretKeyPinned.unpin()
        return x25519SecretKey
    }

    /**
     * The secret key actually includes the seed (either a random seed or the one given to crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    actual fun ed25519SkToSeed(secretKey: UByteArray): UByteArray {
        val seed = UByteArray(crypto_sign_SEEDBYTES)

        val secretKeyPinned = secretKey.pin()
        val seedPinned = seed.pin()

        crypto_sign_ed25519_sk_to_seed(
            seedPinned.toPtr(),
            secretKeyPinned.toPtr()
        ).ensureLibsodiumSuccess()

        secretKeyPinned.unpin()
        seedPinned.unpin()

        return seed

    }

    /**
     * The secret key actually includes the seed (either a random seed or the one given to crypto_sign_seed_keypair()) as well as the public key.
     * While the public key can always be derived from the seed, the precomputation saves a significant amount of CPU cycles when signing.
     */
    actual fun ed25519SkToPk(secretKey: UByteArray): UByteArray {
        val publicKey = UByteArray(crypto_sign_PUBLICKEYBYTES)

        val secretKeyPinned = secretKey.pin()
        val publicKeyPinned = publicKey.pin()

        crypto_sign_ed25519_sk_to_pk(
            publicKeyPinned.toPtr(),
            secretKeyPinned.toPtr()
        ).ensureLibsodiumSuccess()

        secretKeyPinned.unpin()
        publicKeyPinned.unpin()

        return publicKey
    }

}
