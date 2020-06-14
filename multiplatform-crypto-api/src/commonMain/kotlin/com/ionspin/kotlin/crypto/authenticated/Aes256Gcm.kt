package com.ionspin.kotlin.crypto.authenticated

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
interface Aes256GcmStateless {

    fun encrypt(message: UByteArray, additionalData: UByteArray, rawData : UByteArray, key: Aes256GcmKey) : Aes256GcmEncryptionResult

    fun decrypt(encryptedData: UByteArray, nonce: UByteArray, key : Aes256GcmKey) : UByteArray
}

data class Aes256GcmEncryptionResult(val cyphertext : UByteArray, val additionalData: UByteArray, val nonce: UByteArray, val tag: UByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Aes256GcmEncryptionResult

        if (cyphertext != other.cyphertext) return false
        if (additionalData != other.additionalData) return false
        if (nonce != other.nonce) return false
        if (tag != other.tag) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cyphertext.hashCode()
        result = 31 * result + additionalData.hashCode()
        result = 31 * result + nonce.hashCode()
        result = 31 * result + tag.hashCode()
        return result
    }
}


interface Aes256GcmKey {
    val key : UByteArray
}
