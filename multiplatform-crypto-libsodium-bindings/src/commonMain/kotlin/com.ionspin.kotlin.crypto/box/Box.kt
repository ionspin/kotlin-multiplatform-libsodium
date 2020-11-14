package com.ionspin.kotlin.crypto.box

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Aug-2020
 */

val crypto_box_PUBLICKEYBYTES = 32
val crypto_box_SECRETKEYBYTES = 32
val crypto_box_MACBYTES = 16
val crypto_box_SEEDBYTES = 32
val crypto_box_NONCEBYTES = 24
val crypto_box_SEALBYTES = 48
val crypto_box_BEFORENMBYTES = 32

data class BoxKeyPair(val publicKey : UByteArray, val secretKey: UByteArray)
data class BoxEncryptedDataAndTag(val ciphertext: UByteArray, val tag: UByteArray)

class BoxCorruptedOrTamperedDataException() : RuntimeException("MAC validation failed. Data is corrupted or tampered with.")

/**
 * Authenticated encryption (crypto_box_* API)
 *
 * Using public-key authenticated encryption, Bob can encrypt a confidential message specifically for Alice, using Alice's public key.
 * Using Bob's public key, Alice can compute a shared secret key. Using Alice's public key and his secret key,
 * Bob can compute the exact same shared secret key. That shared secret key can be used to verify that the encrypted
 * message was not tampered with, before eventually decrypting it.
 * Alice only needs Bob's public key, the nonce and the ciphertext. Bob should never ever share his secret key,
 * even with Alice.
 * And in order to send messages to Alice, Bob only needs Alice's public key. Alice should never ever share her secret
 * key either, even with Bob.
 * Alice can reply to Bob using the same system, without having to generate a distinct key pair.
 * The nonce doesn't have to be confidential, but it should be used with just one invocation of crypto_box_easy() for a
 * particular pair of public and secret keys.
 * One easy way to generate a nonce is to use randombytes_buf(), considering the size of the nonces the risk of any
 * random collisions is negligible. For some applications, if you wish to use nonces to detect missing messages or to
 * ignore replayed messages, it is also acceptable to use a simple incrementing counter as a nonce. A better alternative
 * is to use the crypto_secretstream() API.
 * When doing so you must ensure that the same value can never be re-used (for example you may have multiple threads
 * or even hosts generating messages using the same key pairs).
 * As stated above, senders can decrypt their own messages, and compute a valid authentication tag for any messages
 * encrypted with a given shared secret key. This is generally not an issue for online protocols. If this is not
 * acceptable, check out the Sealed Boxes section, as well as the Key Exchange section in this documentation.
 *
 *
 * Sealed boxes (crypto_box_seal_* API)
 *
 * Sealed boxes are designed to anonymously send messages to a recipient given its public key.
 * Only the recipient can decrypt these messages, using its private key. While the recipient can verify the integrity
 * of the message, it cannot verify the identity of the sender.
 * A message is encrypted using an ephemeral key pair, whose secret part is destroyed right after the encryption process.
 * Without knowing the secret key used for a given message, the sender cannot decrypt its own message later.
 * And without additional data, a message cannot be correlated with the identity of its sender.
 */
expect object Box {
    /**
     * The crypto_box_keypair() function randomly generates a secret key and a corresponding public key.
     * The public key is put into pk (crypto_box_PUBLICKEYBYTES bytes) and the secret key into
     * sk (crypto_box_SECRETKEYBYTES bytes).
     */
    fun keypair() : BoxKeyPair

    /**
     * Using crypto_box_seed_keypair(), the key pair can also be deterministically derived from a single key seed (crypto_box_SEEDBYTES bytes).
     */
    fun seedKeypair(seed: UByteArray) : BoxKeyPair

    /**
     * The crypto_box_easy() function encrypts a message m whose length is mlen bytes, with a recipient's public key pk, a sender's secret key sk and a nonce n.
     * n should be crypto_box_NONCEBYTES bytes.
     * c should be at least crypto_box_MACBYTES + mlen bytes long.
     * This function writes the authentication tag, whose length is crypto_box_MACBYTES bytes, in c,
     * immediately followed by the encrypted message, whose length is the same as the plaintext: mlen.
     */
    fun easy(message : UByteArray, nonce : UByteArray, recipientsPublicKey: UByteArray, sendersSecretKey: UByteArray) : UByteArray

    /**
     * The crypto_box_open_easy() function verifies and decrypts a ciphertext produced by crypto_box_easy().
     * c is a pointer to an authentication tag + encrypted message combination, as produced by crypto_box_easy(). clen is the length of this authentication tag + encrypted message combination. Put differently, clen is the number of bytes written by crypto_box_easy(), which is crypto_box_MACBYTES + the length of the message.
     * The nonce n has to match the nonce used to encrypt and authenticate the message.
     * pk is the public key of the sender that encrypted the message. sk is the secret key of the recipient that is willing to verify and decrypt it.
     * The function throws [BoxCorruptedOrTamperedDataException] if the verification fails.
     */
    fun openEasy(ciphertext : UByteArray, nonce: UByteArray, sendersPublicKey: UByteArray, recipientsSecretKey: UByteArray) : UByteArray
    /**
     * The crypto_box_beforenm() function computes a shared secret key given a public key pk and a secret key sk,
     * and puts it into k (crypto_box_BEFORENMBYTES bytes).
     */
    fun beforeNM(publicKey: UByteArray, secretKey: UByteArray) : UByteArray

    /**
     * The _afternm variants of the previously described functions accept a precalculated shared secret key k instead of a key pair.
     */
    fun easyAfterNM(message : UByteArray, nonce: UByteArray, precomputedKey: UByteArray) : UByteArray

    /**
     * The _afternm variants of the previously described functions accept a precalculated shared secret key k instead of a key pair.
     */
    fun openEasyAfterNM(ciphertext: UByteArray, nonce: UByteArray, precomputedKey: UByteArray) : UByteArray


    /**
     * This function encrypts a message m of length mlen with a nonce n and a secret key sk for a recipient whose
     * public key is pk, and puts the encrypted message into c.
     * Exactly mlen bytes will be put into c, since this function does not prepend the authentication tag.
     * The tag, whose size is crypto_box_MACBYTES bytes, will be put into mac.
     */
    fun detached(message: UByteArray, nonce: UByteArray, recipientsPublicKey: UByteArray, sendersSecretKey: UByteArray) : BoxEncryptedDataAndTag

    /**
     * The crypto_box_open_detached() function verifies and decrypts an encrypted message c whose length is clen using the recipient's secret key sk and the sender's public key pk.
     * clen doesn't include the tag, so this length is the same as the plaintext.
     * The plaintext is put into m after verifying that mac is a valid authentication tag for this ciphertext, with the given nonce n and key k.
     * The function throws [BoxCorruptedOrTamperedDataException] if the verification fails.
     */
    fun openDetached(ciphertext: UByteArray, tag: UByteArray, nonce: UByteArray, sendersPublicKey: UByteArray, recipientsSecretKey: UByteArray) : UByteArray


    fun seal(message: UByteArray, recipientsPublicKey: UByteArray) : UByteArray

    fun sealOpen(ciphertext: UByteArray, recipientsPublicKey: UByteArray, recipientsSecretKey: UByteArray) : UByteArray

}
