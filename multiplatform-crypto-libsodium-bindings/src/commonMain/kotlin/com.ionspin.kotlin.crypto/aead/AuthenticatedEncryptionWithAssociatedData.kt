package com.ionspin.kotlin.crypto.aead

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-Aug-2020
 */

//X - Ietf
val crypto_aead_xchacha20poly1305_ietf_KEYBYTES = 32
val crypto_aead_xchacha20poly1305_ietf_NPUBBYTES = 24
val crypto_aead_xchacha20poly1305_ietf_ABYTES = 16

// Ietf
val crypto_aead_chacha20poly1305_ietf_KEYBYTES = 32
val crypto_aead_chacha20poly1305_ietf_NPUBBYTES = 12
val crypto_aead_chacha20poly1305_ietf_ABYTES = 16

// original chacha20poly1305

val crypto_aead_chacha20poly1305_KEYBYTES = 32
val crypto_aead_chacha20poly1305_NPUBBYTES = 8
val crypto_aead_chacha20poly1305_ABYTES = 16

/**
 * A data class wrapping returned encrypted data and and tag from aead encrypt functions.
 */
data class AeadEncryptedDataAndTag(val data: UByteArray, val tag: UByteArray)

/**
 * An exception thrown when tag generated from received data and key doesn't match the received tag
 */
class AeadCorrupedOrTamperedDataException() : RuntimeException("Tag (authentication data) validation failed. Data is corrupted or tampered with.")

/**
 * This is a form of symmetric encryption, that assures both confidentiality and authenticity of the data.
 *
 * Offered here are three implementations of (x)ChaCha20-Poly1305 construction:
 * - ChaCha20Poly1305 - uses 64bit nonce
 * - ChaCha20Poly1305-IETF - uses 96bit nonce (standardised by [RFC8439](https://tools.ietf.org/html/rfc8439)
 * - XChaCha20Poly1305 - uses 192bit nonce - recommended choice
 *
 * The only difference is the size of the nonce, and how is the nonce used.
 *
 * (x)ChaCha20 is a streaming cipher develop by Daniel J. Bernstein. He is also the author of Poly1305 a fast Message
 * Authentication Code system
 *
 * Libsodium offers two additional variants for each of the aforementioned variants:
 * - Combined
 * - Detached
 *
 * Combined mode returns encrypted data and tag as one UByteArray, while detached mode returns them as separate UByteArrays.
 * To be kotlin idiomatic we are returning detached tag and encrypted data inside a wrapper data class [AeadEncryptedDataAndTag]
 */
expect object AuthenticatedEncryptionWithAssociatedData {
    // X - Ietf
    fun xChaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    fun xChaCha20Poly1305IetfDecrypt(
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    fun xChaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag

    fun xChaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    // Ietf

    fun chaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    fun chaCha20Poly1305IetfDecrypt(
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    fun chaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag

    fun chaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    // Original chacha20poly1305

    fun chaCha20Poly1305Encrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    fun chaCha20Poly1305Decrypt(
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray

    fun chaCha20Poly1305EncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag

    fun chaCha20Poly1305DecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray


    fun xChaCha20Poly1305IetfKeygen() : UByteArray
    fun chaCha20Poly1305IetfKeygen() : UByteArray
    fun chaCha20Poly1305Keygen() : UByteArray

}
