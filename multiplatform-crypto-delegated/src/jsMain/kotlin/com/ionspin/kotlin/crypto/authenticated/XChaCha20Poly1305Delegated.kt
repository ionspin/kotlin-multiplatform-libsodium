package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.crypto.InvalidTagException
import com.ionspin.kotlin.crypto.getSodium
import com.ionspin.kotlin.crypto.util.hexColumsPrint
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-Jun-2020
 */

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
            val encrypted = getSodium().crypto_aead_xchacha20poly1305_ietf_encrypt(
                message.toUInt8Array(),
                associatedData.toUInt8Array(),
                Uint8Array(0),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            )
            return encrypted.toUByteArray()
        }

        actual fun decrypt(
            key: UByteArray,
            nonce: UByteArray,
            ciphertext: UByteArray,
            associatedData: UByteArray
        ): UByteArray {
            val decrypted = getSodium().crypto_aead_xchacha20poly1305_ietf_decrypt(
                Uint8Array(0),
                ciphertext.toUInt8Array(),
                associatedData.toUInt8Array(),
                nonce.toUInt8Array(),
                key.toUInt8Array()
            )
            return decrypted.toUByteArray()
        }
    }

    var state : dynamic = null
    var isInitialized = false
    var isEncryptor = false

    actual fun initializeForEncryption(key: UByteArray) : UByteArray {
        println("Initializaing for encryption")
        val stateAndHeader = getSodium().crypto_secretstream_xchacha20poly1305_init_push(key.toUInt8Array())
        state = stateAndHeader.state
        val header = stateAndHeader.header as Uint8Array
        console.log(state)
        console.log(header)
        println("Done initializaing for encryption")
        isInitialized = true
        isEncryptor = true
        return header.toUByteArray()
    }

    actual fun initializeForDecryption(key: UByteArray, header: UByteArray) {
        println("Initializing for decryption")
        header.hexColumsPrint()
        state = getSodium().crypto_secretstream_xchacha20poly1305_init_pull(header.toUInt8Array(), key.toUInt8Array())
        console.log(state)
        isInitialized = true
        isEncryptor = false
    }

    internal actual constructor(
        key: UByteArray,
        testState: UByteArray,
        testHeader: UByteArray,
        isDecryptor: Boolean
    ) : this() {
        state = getSodium().crypto_secretstream_xchacha20poly1305_init_pull(testHeader.toUInt8Array(), key.toUInt8Array())
        console.log(state)
        println("Done initializaing test state")
        isInitialized = true
        isEncryptor = !isDecryptor
    }

    actual fun encrypt(data: UByteArray, associatedData: UByteArray): UByteArray {
        if (!isInitialized) {
            throw RuntimeException("Not initalized!")
        }
        if (!isEncryptor) {
            throw RuntimeException("Initialized as decryptor, attempted to use as encryptor")
        }
        val encrypted = getSodium().crypto_secretstream_xchacha20poly1305_push(state, data.toUInt8Array(), associatedData.toUInt8Array(), 0U)
        return encrypted.toUByteArray()
    }

    actual fun decrypt(data: UByteArray, associatedData: UByteArray): UByteArray {
        if (!isInitialized) {
            throw RuntimeException("Not initalized!")
        }
        if (isEncryptor) {
            throw RuntimeException("Initialized as encryptor, attempted to use as decryptor")
        }
        val decryptedWithTag = getSodium().crypto_secretstream_xchacha20poly1305_pull(state, data.toUInt8Array(), associatedData.toUInt8Array())
        val decrypted = decryptedWithTag.message as Uint8Array
        val validTag = decryptedWithTag.tag

        if (validTag != 0U) {
            println("Tag validation failed")
            throw InvalidTagException()
        }
        return decrypted.toUByteArray()
    }

    actual fun cleanup() {
        //TODO JS cleanup
    }



}
