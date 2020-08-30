package com.ionspin.kotlin.crypto.authenticated

import com.goterl.lazycode.lazysodium.SodiumJava
import com.goterl.lazycode.lazysodium.interfaces.SecretStream
import com.ionspin.kotlin.crypto.InvalidTagException
import com.ionspin.kotlin.crypto.util.hexColumsPrint

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
actual class XChaCha20Poly1305Delegated internal actual constructor() {
    actual companion object {
        actual fun encrypt(
            key: UByteArray,
            nonce: UByteArray,
            message: UByteArray,
            associatedData: UByteArray
        ): UByteArray {
            val ciphertext = ByteArray(message.size + 16)
            SodiumJava().crypto_aead_xchacha20poly1305_ietf_encrypt(
                ciphertext,
                longArrayOf(ciphertext.size.toLong()),
                message.toByteArray(),
                message.size.toLong(),
                associatedData.toByteArray(),
                associatedData.size.toLong(),
                null,
                nonce.toByteArray(),
                key.toByteArray()

            )
            return ciphertext.asUByteArray()
        }

        actual fun decrypt(
            key: UByteArray,
            nonce: UByteArray,
            ciphertext: UByteArray,
            associatedData: UByteArray
        ): UByteArray {
            val message = ByteArray(ciphertext.size - 16)
            SodiumJava().crypto_aead_xchacha20poly1305_ietf_decrypt(
                message,
                longArrayOf(ciphertext.size.toLong()),
                null,
                ciphertext.toByteArray(),
                ciphertext.size.toLong(),
                associatedData.toByteArray(),
                associatedData.size.toLong(),
                nonce.toByteArray(),
                key.toByteArray()

            )
            return message.asUByteArray()
        }
    }

    val state : SecretStream.State = SecretStream.State()
    val sodium = SodiumJava()

    var isInitialized = false
    var isEncryptor = false

    internal actual constructor(
        key: UByteArray,
        testState: UByteArray,
        testHeader: UByteArray,
        isDecryptor: Boolean
    ) : this() {
        state.k = testState.sliceArray(0 until 32).toByteArray()
        state.nonce = testState.sliceArray(32 until 44).toByteArray()
        isInitialized = true
        isEncryptor = !isDecryptor
    }

    actual fun initializeForEncryption(key: UByteArray) : UByteArray {
        val header = UByteArray(24)
        sodium.crypto_secretstream_xchacha20poly1305_init_push(state, header.asByteArray(), key.asByteArray())
        isInitialized = true
        isEncryptor = true
        return header
    }

    actual fun initializeForDecryption(key: UByteArray, header: UByteArray) {
        sodium.crypto_secretstream_xchacha20poly1305_init_pull(state, header.asByteArray(), key.asByteArray())
        isInitialized = true
        isEncryptor = false
    }

    actual fun encrypt(data: UByteArray, associatedData: UByteArray): UByteArray {
        if (!isInitialized) {
            throw RuntimeException("Not initalized!")
        }
        if (!isEncryptor) {
            throw RuntimeException("Initialized as decryptor, attempted to use as encryptor")
        }
        val ciphertext = ByteArray(1 + data.size + 16)
        sodium.crypto_secretstream_xchacha20poly1305_push(
            state, ciphertext, null,
            data.asByteArray(), data.size.toLong(),
            associatedData.asByteArray(), associatedData.size.toLong(),
            0
        )
        return ciphertext.asUByteArray()
    }

    actual fun decrypt(data: UByteArray, associatedData: UByteArray): UByteArray {
        if (!isInitialized) {
            throw RuntimeException("Not initalized!")
        }
        if (isEncryptor) {
            throw RuntimeException("Initialized as encryptor, attempted to use as decryptor")
        }
        val plaintext = ByteArray(data.size - 17)

        val validTag = sodium.crypto_secretstream_xchacha20poly1305_pull(
            state, plaintext, null,
            null,
            data.asByteArray(),
            (data.size).toLong(),
            associatedData.asByteArray(),
            associatedData.size.toLong()
        )
        if (validTag != 0) {
            println("Tag validation failed")
            throw InvalidTagException()
        }
        return plaintext.asUByteArray()

    }

    actual fun cleanup() {
        sodium.sodium_memzero(state.k, 32)
        sodium.sodium_memzero(state.nonce, 12)
    }




}
