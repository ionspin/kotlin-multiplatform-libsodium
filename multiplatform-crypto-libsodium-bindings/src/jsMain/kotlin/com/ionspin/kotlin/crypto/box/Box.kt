package com.ionspin.kotlin.crypto.box

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

actual object Box {
    /**
     * The crypto_box_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_box_PUBLICKEYBYTES bytes) and the secret key into
     * sk (crypto_box_SECRETKEYBYTES bytes).
     */
    actual fun keypair(): BoxKeyPair {
        val keypair = getSodium().crypto_box_keypair()
        return BoxKeyPair(
            (keypair.publicKey as Uint8Array).toUByteArray(),
            (keypair.privateKey as Uint8Array).toUByteArray()
        )
    }

    /**
     * Using crypto_box_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_box_SEEDBYTES bytes).
     */
    actual fun seedKeypair(seed: UByteArray): BoxKeyPair {
        val keypair = getSodium().crypto_box_seed_keypair(seed.toUInt8Array())
        return BoxKeyPair(
            (keypair.publicKey as Uint8Array).toUByteArray(),
            (keypair.privateKey as Uint8Array).toUByteArray()
        )
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
        return getSodium().crypto_box_easy(
            message.toUInt8Array(),
            nonce.toUInt8Array(),
            recipientsPublicKey.toUInt8Array(),
            sendersSecretKey.toUInt8Array(),
        ).toUByteArray()

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
        try {
            return getSodium().crypto_box_open_easy(
                ciphertext.toUInt8Array(),
                nonce.toUInt8Array(),
                sendersPublicKey.toUInt8Array(),
                recipientsSecretKey.toUInt8Array(),
            ).toUByteArray()
        } catch (error: Throwable) {
            throw BoxCorruptedOrTamperedDataException()
        }

    }

    /**
     * The crypto_box_beforenm() function computes a shared secret key given a public key pk and a secret key sk,
     * and puts it into k (crypto_box_BEFORENMBYTES bytes).
     */
    actual fun beforeNM(publicKey: UByteArray, secretKey: UByteArray): UByteArray {
        return getSodium().crypto_box_beforenm(
            publicKey.toUInt8Array(),
            secretKey.toUInt8Array()
        ).toUByteArray()
    }

    /**
     * The _afternm variants of the previously described functions accept a precalculated shared secret key k instead of a key pair.
     */
    actual fun easyAfterNM(
        message: UByteArray,
        nonce: UByteArray,
        precomputedKey: UByteArray
    ): UByteArray {
        return getSodium().crypto_box_easy_afternm(
            message.toUInt8Array(),
            nonce.toUInt8Array(),
            precomputedKey.toUInt8Array()
        ).toUByteArray()
    }

    /**
     * The _afternm variants of the previously described functions accept a precalculated shared secret key k instead of a key pair.
     */
    actual fun openEasyAfterNM(
        ciphertext: UByteArray,
        nonce: UByteArray,
        precomputedKey: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_box_open_easy_afternm(
                ciphertext.toUInt8Array(),
                nonce.toUInt8Array(),
                precomputedKey.toUInt8Array(),
            ).toUByteArray()
        } catch (error: Throwable) {
            throw BoxCorruptedOrTamperedDataException()
        }
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
        val detached = getSodium().crypto_box_detached(
            message.toUInt8Array(),
            nonce.toUInt8Array(),
            recipientsPublicKey.toUInt8Array(),
            sendersSecretKey.toUInt8Array(),
        )
        return BoxEncryptedDataAndTag(
            (detached.ciphertext as Uint8Array).toUByteArray(),
            (detached.mac as Uint8Array).toUByteArray()
        )
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
        try {
           return getSodium().crypto_box_open_detached(
               ciphertext.toUInt8Array(),
               tag.toUInt8Array(),
               nonce.toUInt8Array(),
               sendersPublicKey.toUInt8Array(),
               recipientsSecretKey.toUInt8Array(),
           ).toUByteArray()
        } catch (error: Throwable) {
            throw BoxCorruptedOrTamperedDataException()
        }
    }

    actual fun seal(message: UByteArray, recipientsPublicKey: UByteArray): UByteArray {
        return getSodium().crypto_box_seal(
            message.toUInt8Array(),
            recipientsPublicKey.toUInt8Array()
        ).toUByteArray()
    }

    actual fun sealOpen(
        ciphertext: UByteArray,
        recipientsPublicKey: UByteArray,
        recipientsSecretKey: UByteArray
    ): UByteArray {
        try {
            return getSodium().crypto_box_seal_open(
                ciphertext.toUInt8Array(),
                recipientsPublicKey.toUInt8Array(),
                recipientsSecretKey.toUInt8Array(),
            ).toUByteArray()
        }  catch (error: Throwable) {
            throw BoxCorruptedOrTamperedDataException()
        }
    }


}