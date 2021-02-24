package com.ionspin.kotlin.crypto.box

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object Box {
    /**
     * The crypto_box_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_box_PUBLICKEYBYTES bytes) and the secret key into
     * sk (crypto_box_SECRETKEYBYTES bytes).
     */
    actual fun keypair(): BoxKeyPair {
        val publicKey = UByteArray(crypto_box_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_box_SECRETKEYBYTES)
        sodiumJna.crypto_box_keypair(publicKey.asByteArray(), secretKey.asByteArray())
        return BoxKeyPair(publicKey, secretKey)
    }

    /**
     * Using crypto_box_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_box_SEEDBYTES bytes).
     */
    actual fun seedKeypair(seed: UByteArray): BoxKeyPair {
        val publicKey = UByteArray(crypto_box_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_box_SECRETKEYBYTES)
        sodiumJna.crypto_box_seed_keypair(publicKey.asByteArray(), secretKey.asByteArray(), seed.asByteArray())
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
        sodiumJna.crypto_box_easy(
            ciphertext.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            recipientsPublicKey.asByteArray(),
            sendersSecretKey.asByteArray()
        )
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
        val validationResult = sodiumJna.crypto_box_open_easy(
            message.asByteArray(),
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            nonce.asByteArray(),
            sendersPublicKey.asByteArray(),
            recipientsSecretKey.asByteArray()
        )
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
        sodiumJna.crypto_box_beforenm(sessionKey.asByteArray(), publicKey.asByteArray(), secretKey.asByteArray())
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

        sodiumJna.crypto_box_easy_afternm(
            ciphertext.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            precomputedKey.asByteArray()
        )

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
        val validationResult = sodiumJna.crypto_box_open_easy_afternm(
            message.asByteArray(),
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            nonce.asByteArray(),
            precomputedKey.asByteArray()
        )

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

        sodiumJna.crypto_box_detached(
            ciphertext.asByteArray(),
            tag.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            nonce.asByteArray(),
            recipientsPublicKey.asByteArray(),
            sendersSecretKey.asByteArray()
        )

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

        val validationResult = sodiumJna.crypto_box_open_detached(
            message.asByteArray(),
            ciphertext.asByteArray(),
            tag.asByteArray(),
            ciphertext.size.toLong(),
            nonce.asByteArray(),
            sendersPublicKey.asByteArray(),
            recipientsSecretKey.asByteArray()
        )

        if (validationResult != 0) {
            throw BoxCorruptedOrTamperedDataException()
        }

        return message
    }

    actual fun seal(message: UByteArray, recipientsPublicKey: UByteArray): UByteArray {
        val ciphertextWithPublicKey = UByteArray(message.size + crypto_box_SEALBYTES)
        sodiumJna.crypto_box_seal(
            ciphertextWithPublicKey.asByteArray(),
            message.asByteArray(),
            message.size.toLong(),
            recipientsPublicKey.asByteArray()
        )
        return ciphertextWithPublicKey
    }

    actual fun sealOpen(ciphertext: UByteArray, recipientsPublicKey: UByteArray, recipientsSecretKey: UByteArray): UByteArray {
        val message = UByteArray(ciphertext.size - crypto_box_SEALBYTES)
        val validationResult = sodiumJna.crypto_box_seal_open(
            message.asByteArray(),
            ciphertext.asByteArray(),
            ciphertext.size.toLong(),
            recipientsPublicKey.asByteArray(),
            recipientsSecretKey.asByteArray()
        )

        if (validationResult != 0) {
            throw BoxCorruptedOrTamperedDataException()
        }

        return message
    }


}
