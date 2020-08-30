package com.ionspin.kotlin.crypto.aead

actual object AuthenticatedEncryptionWithAssociatedData {

    // Ietf

    // Original chacha20poly1305
    actual fun xChaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun xChaCha20Poly1305IetfDecrypt(
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun xChaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        TODO("not implemented yet")
    }

    actual fun xChaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305IetfEncrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305IetfDecrypt(
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305IetfEncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305IetfDecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305Encrypt(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305Decrypt(
        ciphertext: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305EncryptDetached(
        message: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): AeadEncryptedDataAndTag {
        TODO("not implemented yet")
    }

    actual fun chaCha20Poly1305DecryptDetached(
        ciphertext: UByteArray,
        tag: UByteArray,
        associatedData: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        TODO("not implemented yet")
    }

}
