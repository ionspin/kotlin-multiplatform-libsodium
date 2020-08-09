package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.hash.MultipartHash
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Jun-2020
 */
inline class EncryptableString(val content: String) : Encryptable<EncryptableString> {
    override fun toEncryptableForm(): UByteArray {
        return content.encodeToUByteArray()
    }

    override fun fromEncryptableForm(): (UByteArray) -> EncryptableString {
        return { uByteArray ->
            EncryptableString(uByteArray.toByteArray().decodeToString())
        }
    }

    fun asString() : String = content


}

fun String.asEncryptableString() : EncryptableString {
    return EncryptableString(this)
}

interface Encryptable<T> {
    fun toEncryptableForm() : UByteArray
    fun fromEncryptableForm() : (UByteArray) -> T
}

data class HashedData(val hash: UByteArray) {
    fun toHexString() : String {
        return hash.toHexString()
    }
}

data class SymmetricKey(val value : UByteArray) {
    companion object {

    }
}

data class EncryptedData constructor(val ciphertext: UByteArray, val nonce: UByteArray)

interface HashApi {
    fun hash(data: UByteArray, key : UByteArray = ubyteArrayOf()) : HashedData
    fun multipartHash(key: UByteArray? = null) : MultipartHash
}

interface EncryptionApi {
    fun encrypt(key: SymmetricKey, data : Encryptable<*>, additionalData : UByteArray) : EncryptedData
    fun <T: Encryptable<T>> decrypt(key: SymmetricKey, encryptedData : EncryptedData, additionalData: UByteArray, byteArrayDeserializer : (UByteArray) -> T) : T
    fun createMultipartEncryptor(key: SymmetricKey) : MultipartAuthenticatedEncryption
    fun createMultipartDecryptor(key: SymmetricKey, header: MultipartEncryptionHeader) : MultipartAuthenticatedDecryption

}

interface AuthenticatedEncryption {
    fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray = ubyteArrayOf()) : UByteArray
    fun decrypt(key: UByteArray, nonce: UByteArray, cipherText: UByteArray, additionalData: UByteArray = ubyteArrayOf()) : UByteArray

}

data class EncryptedDataPart(val data : UByteArray)
data class DecryptedDataPart(val data : UByteArray)

data class MultipartEncryptionHeader(val nonce: UByteArray)

class InvalidTagException : RuntimeException("Tag mismatch! Encrypted data is corrupted or tampered with.")

interface MultipartAuthenticatedDecryption {
    fun decryptPartialData(data: EncryptedDataPart, additionalData: UByteArray = ubyteArrayOf()) : DecryptedDataPart
    fun cleanup()
}

interface MultipartAuthenticatedEncryption {
    fun encryptPartialData(data: UByteArray, additionalData: UByteArray = ubyteArrayOf()) : EncryptedDataPart
    fun startEncryption() : MultipartEncryptionHeader
    fun cleanup()

}