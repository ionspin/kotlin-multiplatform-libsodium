package com.ionspin.kotlin.crypto.secretstream

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 26-Aug-2020
 *
 * This file is named with Jvm suffix because of https://youtrack.jetbrains.com/issue/KT-21186
 */
expect class SecretStreamState

data class SecretStreamStateAndHeader(val state: SecretStreamState, val header : UByteArray)

data class DecryptedDataAndTag(val decryptedData : UByteArray, val tag : UByte)

val crypto_secretstream_xchacha20poly1305_TAG_MESSAGE = 0
val crypto_secretstream_xchacha20poly1305_TAG_PUSH = 1
val crypto_secretstream_xchacha20poly1305_TAG_REKEY = 2
val crypto_secretstream_xchacha20poly1305_TAG_FINAL = 3

val crypto_secretstream_xchacha20poly1305_HEADERBYTES = 24
val crypto_secretstream_xchacha20poly1305_KEYBYTES = 32
val crypto_secretstream_xchacha20poly1305_ABYTES = 17

class SecretStreamCorruptedOrTamperedDataException() : RuntimeException("MAC validation failed. Data is corrupted or tampered with.")

expect object SecretStream {

    fun xChaCha20Poly1305InitPush(key: UByteArray) : SecretStreamStateAndHeader
    fun xChaCha20Poly1305Push(state : SecretStreamState, message: UByteArray, associatedData : UByteArray = ubyteArrayOf(), tag: UByte) : UByteArray
    fun xChaCha20Poly1305InitPull(key: UByteArray, header: UByteArray) : SecretStreamStateAndHeader
    fun xChaCha20Poly1305Pull(state : SecretStreamState, ciphertext: UByteArray, associatedData : UByteArray = ubyteArrayOf()) : DecryptedDataAndTag
    fun xChaCha20Poly1305Keygen() : UByteArray
    fun xChaCha20Poly1305Rekey(state: SecretStreamState)

}
