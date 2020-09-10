package com.ionspin.kotlin.crypto.box

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_box_beforenm
import libsodium.crypto_box_detached
import libsodium.crypto_box_easy
import libsodium.crypto_box_easy_afternm
import libsodium.crypto_box_keypair
import libsodium.crypto_box_open_detached
import libsodium.crypto_box_open_easy
import libsodium.crypto_box_open_easy_afternm
import libsodium.crypto_box_seal
import libsodium.crypto_box_seal_open
import libsodium.crypto_box_seed_keypair

actual object Box {
    /**
     * The crypto_box_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_box_PUBLICKEYBYTES bytes) and the secret key into
     * sk (crypto_box_SECRETKEYBYTES bytes).
     */
    actual fun keypair(): BoxKeyPair {
        val publicKey = UByteArray(crypto_box_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_box_SECRETKEYBYTES)
        val publicKeyPinned = publicKey.pin()
        val secretKeyPinned = secretKey.pin()
        crypto_box_keypair(publicKeyPinned.toPtr(), secretKeyPinned.toPtr())
        publicKeyPinned.unpin()
        secretKeyPinned.unpin()
        return BoxKeyPair(publicKey, secretKey)
    }

    /**
     * Using crypto_box_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_box_SEEDBYTES bytes).
     */
    actual fun seedKeypair(seed: UByteArray): BoxKeyPair {
        val publicKey = UByteArray(crypto_box_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_box_SECRETKEYBYTES)
        val publicKeyPinned = publicKey.pin()
        val secretKeyPinned = secretKey.pin()
        val seedPinned = seed.pin()
        crypto_box_seed_keypair(publicKeyPinned.toPtr(), secretKeyPinned.toPtr(), seedPinned.toPtr())
        publicKeyPinned.unpin()
        secretKeyPinned.unpin()
        seedPinned.unpin()
        return BoxKeyPair(publicKey, secretKey)
    }

    /**
     * The crypto_box_easy() function encrypts a message m whose length is mlen bytes, with a recipient's public key pk, a sender's secret key sk and a nonce n.
     * n should be crypto_box_NONCEBYTES bytes.
     * c should be at least crypto_box_MACBYTES + mlen bytes long.
     * This function writes the authentication tag, whose length is crypto_box_MACBYTES bytes, in c,
     * immediately followed by the encrypted message, whose length is the same as the plaintext: mlen.
     */
    actual fun easy(
        message: UByteArray,
        nonce: UByteArray,
        recipientsPublicKey: UByteArray,
        sendersSecretKey: UByteArray
    ): UByteArray {
        val ciphertext = UByteArray(message.size + crypto_box_MACBYTES)
        val ciphertextPinned = ciphertext.pin()

        val messagePinned = message.pin()
        val noncePinned = nonce.pin()
        val recipientsPublicKeyPinned = recipientsPublicKey.pin()
        val sendersSecretKeyPinned = sendersSecretKey.pin()

        crypto_box_easy(
            ciphertextPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            recipientsPublicKeyPinned.toPtr(),
            sendersSecretKeyPinned.toPtr()
        )

        ciphertextPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        recipientsPublicKeyPinned.unpin()
        sendersSecretKeyPinned.unpin()

        return ciphertext
    }

    /**
     * The crypto_box_open_easy() function verifies and decrypts a ciphertext produced by crypto_box_easy().
     * c is a pointer to an authentication tag + encrypted message combination, as produced by crypto_box_easy(). clen is the length of this authentication tag + encrypted message combination. Put differently, clen is the number of bytes written by crypto_box_easy(), which is crypto_box_MACBYTES + the length of the message.
     * The nonce n has to match the nonce used to encrypt and authenticate the message.
     * pk is the public key of the sender that encrypted the message. sk is the secret key of the recipient that is willing to verify and decrypt it.
     * The function throws [BoxCorruptedOrTamperedDataException] if the verification fails.
     */
    actual fun openEasy(
        ciphertext: UByteArray,
        nonce: UByteArray,
        sendersPublicKey: UByteArray,
        recipientsSecretKey: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size - crypto_box_MACBYTES)
        val messagePinned = message.pin()

        val ciphertextPinned = ciphertext.pin()
        val noncePinned = nonce.pin()
        val sendersPublicKeyPinned = sendersPublicKey.pin()
        val recipientsSecretKeyPinned = recipientsSecretKey.pin()

        val validationResult = crypto_box_open_easy(
            messagePinned.toPtr(),
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            noncePinned.toPtr(),
            sendersPublicKeyPinned.toPtr(),
            recipientsSecretKeyPinned.toPtr()
        )

        messagePinned.unpin()
        ciphertextPinned.unpin()
        noncePinned.unpin()
        sendersPublicKeyPinned.unpin()
        recipientsSecretKeyPinned.unpin()

        if (validationResult != 0) {
            throw BoxCorruptedOrTamperedDataException()
        }

        return message
    }

    /**
     * The crypto_box_beforenm() function computes a shared secret key given a public key pk and a secret key sk,
     * and puts it into k (crypto_box_BEFORENMBYTES bytes).
     */
    actual fun beforeNM(publicKey: UByteArray, secretKey: UByteArray): UByteArray {
        val sessionKey = UByteArray(crypto_box_BEFORENMBYTES)

        val sessionKeyPinned = sessionKey.pin()
        val publicKeyPinned = publicKey.pin()
        val secretKeyPinned = secretKey.pin()

        crypto_box_beforenm(sessionKeyPinned.toPtr(), publicKeyPinned.toPtr(), secretKeyPinned.toPtr())

        sessionKeyPinned.unpin()
        publicKeyPinned.unpin()
        secretKeyPinned.unpin()

        return sessionKey
    }

    /**
     * The _afternm variants of the previously described functions accept a precalculated shared secret key k instead of a key pair.
     */
    actual fun easyAfterNM(
        message: UByteArray,
        nonce: UByteArray,
        precomputedKey: UByteArray
    ): UByteArray {
        val ciphertext = UByteArray(message.size + crypto_box_MACBYTES)

        val ciphertextPinned = ciphertext.pin()
        val messagePinned = message.pin()
        val noncePinned = nonce.pin()
        val precomputedKeyPinned = precomputedKey.pin()

        crypto_box_easy_afternm(
            ciphertextPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            precomputedKeyPinned.toPtr()
        )

        ciphertextPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        precomputedKeyPinned.unpin()



        return ciphertext
    }

    /**
     * The _afternm variants of the previously described functions accept a precalculated shared secret key k instead of a key pair.
     */
    actual fun openEasyAfterNM(
        ciphertext: UByteArray,
        nonce: UByteArray,
        precomputedKey: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size - crypto_box_MACBYTES)

        val messagePinned = message.pin()
        val ciphertextPinned = ciphertext.pin()
        val noncePinned = nonce.pin()
        val precomputedKeyPinned = precomputedKey.pin()

        val validationResult = crypto_box_open_easy_afternm(
            messagePinned.toPtr(),
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            noncePinned.toPtr(),
            precomputedKeyPinned.toPtr()
        )

        ciphertextPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        precomputedKeyPinned.unpin()

        if (validationResult != 0) {
            throw BoxCorruptedOrTamperedDataException()
        }

        return message
    }

    /**
     * This function encrypts a message m of length mlen with a nonce n and a secret key sk for a recipient whose
     * public key is pk, and puts the encrypted message into c.
     * Exactly mlen bytes will be put into c, since this function does not prepend the authentication tag.
     * The tag, whose size is crypto_box_MACBYTES bytes, will be put into mac.
     */
    actual fun detached(
        message: UByteArray,
        nonce: UByteArray,
        recipientsPublicKey: UByteArray,
        sendersSecretKey: UByteArray
    ): BoxEncryptedDataAndTag {
        val ciphertext = UByteArray(message.size)
        val tag = UByteArray(crypto_box_MACBYTES)

        val ciphertextPinned = ciphertext.pin()
        val tagPinned = tag.pin()

        val messagePinned = message.pin()
        val noncePinned = nonce.pin()
        val recipientsPublicKeyPinned = recipientsPublicKey.pin()
        val sendersSecretKeyPinned = sendersSecretKey.pin()

        crypto_box_detached(
            ciphertextPinned.toPtr(),
            tagPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            recipientsPublicKeyPinned.toPtr(),
            sendersSecretKeyPinned.toPtr()
        )

        ciphertextPinned.unpin()
        tagPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        recipientsPublicKeyPinned.unpin()
        sendersSecretKeyPinned.unpin()
        return BoxEncryptedDataAndTag(ciphertext, tag)
    }

    /**
     * The crypto_box_open_detached() function verifies and decrypts an encrypted message c whose length is clen using the recipient's secret key sk and the sender's public key pk.
     * clen doesn't include the tag, so this length is the same as the plaintext.
     * The plaintext is put into m after verifying that mac is a valid authentication tag for this ciphertext, with the given nonce n and key k.
     * The function throws [BoxCorruptedOrTamperedDataException] if the verification fails.
     */
    actual fun openDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        nonce: UByteArray,
        sendersPublicKey: UByteArray,
        recipientsSecretKey: UByteArray
    ): UByteArray {
        val message = UByteArray(ciphertext.size)

        val messagePinned = message.pin()
        val ciphertextPinned = ciphertext.pin()
        val tagPinned = tag.pin()
        val noncePinned = nonce.pin()
        val recipientsSecretKeyPinned = recipientsSecretKey.pin()
        val sendersPublicKeyPinned = sendersPublicKey.pin()


        val validationResult = crypto_box_open_detached(
            messagePinned.toPtr(),
            ciphertextPinned.toPtr(),
            tagPinned.toPtr(),
            ciphertext.size.convert(),
            noncePinned.toPtr(),
            sendersPublicKeyPinned.toPtr(),
            recipientsSecretKeyPinned.toPtr()
        )

        ciphertextPinned.unpin()
        tagPinned.unpin()
        messagePinned.unpin()
        noncePinned.unpin()
        recipientsSecretKeyPinned.unpin()
        sendersPublicKeyPinned.unpin()

        if (validationResult != 0) {
            throw BoxCorruptedOrTamperedDataException()
        }

        return message
    }

    actual fun seal(message: UByteArray, recipientsPublicKey: UByteArray): UByteArray {
        val ciphertextWithPublicKey = UByteArray(message.size + crypto_box_SEALBYTES)

        val ciphertextWithPublicKeyPinned = ciphertextWithPublicKey.pin()
        val messagePinned = message.pin()
        val recipientsPublicKeyPinned = recipientsPublicKey.pin()

        crypto_box_seal(
            ciphertextWithPublicKeyPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            recipientsPublicKeyPinned.toPtr()
        )

        ciphertextWithPublicKeyPinned.unpin()
        messagePinned.unpin()
        recipientsPublicKeyPinned.unpin()

        return ciphertextWithPublicKey

    }

    actual fun sealOpen(ciphertext: UByteArray, recipientsPublicKey: UByteArray, recipientsSecretKey: UByteArray): UByteArray {
        val message = UByteArray(ciphertext.size - crypto_box_SEALBYTES)

        val recipientsPublicKeyPinned = recipientsPublicKey.pin()
        val messagePinned = message.pin()
        val ciphertextPinned = ciphertext.pin()
        val recipientsSecretKeyPinned = recipientsSecretKey.pin()

        val validationResult = crypto_box_seal_open(
            messagePinned.toPtr(),
            ciphertextPinned.toPtr(),
            ciphertext.size.convert(),
            recipientsPublicKeyPinned.toPtr(),
            recipientsSecretKeyPinned.toPtr()
        )

        messagePinned.unpin()
        ciphertextPinned.unpin()
        recipientsPublicKeyPinned.unpin()
        recipientsSecretKeyPinned.unpin()

        if (validationResult != 0) {
            throw BoxCorruptedOrTamperedDataException()
        }

        return message
    }


}