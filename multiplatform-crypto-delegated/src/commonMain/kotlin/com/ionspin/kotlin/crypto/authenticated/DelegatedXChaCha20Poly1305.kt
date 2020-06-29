package com.ionspin.kotlin.crypto.authenticated


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
expect class XChaCha20Poly1305Delegated constructor(key: UByteArray, additionalData: UByteArray) {
    internal constructor(key: UByteArray, additionalData: UByteArray, testState : UByteArray, testHeader: UByteArray)
    companion object {
        fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray) : UByteArray
        fun decrypt(key: UByteArray, nonce: UByteArray, ciphertext: UByteArray, additionalData: UByteArray) : UByteArray
    }

    fun encryptPartialData(data: UByteArray) : UByteArray
    fun verifyPartialData(data: UByteArray)
    fun checkTag(expectedTag: UByteArray)
    fun decrypt(data: UByteArray) : UByteArray
    fun finishEncryption() : Pair<UByteArray, UByteArray>

}


