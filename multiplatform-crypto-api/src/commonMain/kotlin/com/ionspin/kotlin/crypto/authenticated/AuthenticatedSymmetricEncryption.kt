package com.ionspin.kotlin.crypto.authenticated

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Jun-2020
 */
interface AuthenticatedEncryption {
    fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray) : UByteArray
    fun decrypt(key: UByteArray, nonce: UByteArray, cipherText: UByteArray, additionalData: UByteArray) : UByteArray

}

data class EncryptedDataPart(val data : UByteArray)
data class DecryptedDataPart(val data : UByteArray)

data class MultipartEncryptedDataDescriptor(val data: UByteArray, val nonce: UByteArray)




interface MultipartAuthenticatedVerification {
    fun verifyPartialData(data: EncryptedDataPart)
    fun finalizeVerificationAndPrepareDecryptor() : MultipartAuthenticatedDecryption
}

interface MultipartAuthenticatedDecryption {
    fun decryptPartialData(data: EncryptedDataPart) : DecryptedDataPart
}

interface MultipartAuthenticatedEncryption {
    fun encryptPartialData(data: UByteArray) : EncryptedDataPart
    fun finish() : MultipartEncryptedDataDescriptor

}

