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


data class AeadEncryptedDataAndTag(val data: UByteArray, val tag: UByteArray)

class AeadCorrupedOrTamperedDataException() : RuntimeException("MAC validation failed. Data is corrupted or tampered with.")

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
