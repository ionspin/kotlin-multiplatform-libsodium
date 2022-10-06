package com.ionspin.kotlin.crypto.sample

import com.ionspin.kotlin.crypto.aead.AuthenticatedEncryptionWithAssociatedData
import com.ionspin.kotlin.crypto.aead.crypto_aead_xchacha20poly1305_ietf_NPUBBYTES
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.LibsodiumRandom
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString


object Sample {
    fun runSample() {
        val random = LibsodiumRandom.buf(32)
        println("Random: ${random.toHexString()}")
        println("Hashed 123 ${hashSomething()}")
        encryptThenDecrypt()
    }

    fun hashSomething() : String {
        val hash = Hash.sha512("123".encodeToUByteArray())
        return "Hash (SHA512) of 123: ${hash.toHexString()}"
    }

    fun encryptThenDecrypt() {
        val data = LibsodiumRandom.buf(2048)
        val associatedData = LibsodiumRandom.buf(256)
        val key = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfKeygen()
        val nonce = LibsodiumRandom.buf(crypto_aead_xchacha20poly1305_ietf_NPUBBYTES)
        val encrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfEncrypt(data, associatedData, nonce, key)
        println("Original data ${data.toHexString()}")
        println("Encrypted ${encrypted.toHexString()}")
        val decrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(encrypted, associatedData, nonce, key)
        println("Decrypted ${decrypted.toHexString()}")
    }
}
