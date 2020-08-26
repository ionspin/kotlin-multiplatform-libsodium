package com.ionspin.kotlin.crypto.secretstream

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 26-Aug-2020
 */
expect class SecretStreamState

data class SecretStreamStateAndHeader(val state: SecretStreamState, val header : UByteArray)

data class DecryptedDataAndTag(val decryptedData : UByteArray, val tag : UByte)

expect object SecretStream {

    fun xChaCha20Poly1305InitPush(key: UByteArray) : SecretStreamStateAndHeader
    fun xChaCha20Poly1305Push(state : SecretStreamState, message: UByteArray, additionalData : UByteArray = ubyteArrayOf(), tag: UByte) : UByteArray
    fun xChaCha20Poly1305InitPull(key: UByteArray, header: UByteArray) : SecretStreamStateAndHeader
    fun xChaCha20Poly1305Pull(state : SecretStreamState, ciphertext: UByteArray, additionalData : UByteArray = ubyteArrayOf()) : DecryptedDataAndTag

}
