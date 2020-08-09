package com.ionspin.kotlin.crypto.symmetric

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
interface XChaCha20 {
    interface Nonce {
        val content: UByteArray
    }

    interface Key {
        val content : UByteArray
    }
    fun encrypt(key: Key, inputMessage: UByteArray) : XChaCha20EncryptionResult

    fun decrypt(key: Key, nonce: Nonce) : UByteArray
}

data class XChaCha20EncryptionResult(val nonce: UByteArray, val encryptionData: UByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as XChaCha20EncryptionResult

        if (nonce != other.nonce) return false
        if (encryptionData != other.encryptionData) return false

        return true
    }

    override fun hashCode(): Int {
        var result = nonce.hashCode()
        result = 31 * result + encryptionData.hashCode()
        return result
    }
}



interface XChaCha20KeyProvider {
    fun generateNewKey() : XChaCha20.Key

    fun createFromUByteArray(uByteArray: UByteArray) : XChaCha20.Key
}