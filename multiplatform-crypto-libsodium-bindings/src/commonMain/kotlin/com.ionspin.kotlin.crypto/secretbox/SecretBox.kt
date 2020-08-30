package com.ionspin.kotlin.crypto.secretbox

import kotlin.js.JsName

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 29-Aug-2020
 */

val crypto_secretbox_KEYBYTES = 32
val crypto_secretbox_MACBYTES = 16
val crypto_secretbox_NONCEBYTES = 24

class SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey() : RuntimeException("MAC validation failed. Data is corrupted or tampered with.")

data class SecretBoxEncryptedDataAndTag(val data: UByteArray, val tag: UByteArray)

expect object SecretBox {

    fun easy(message : UByteArray, nonce: UByteArray, key: UByteArray) : UByteArray
    fun openEasy(ciphertext: UByteArray, nonce: UByteArray, key: UByteArray) : UByteArray

    fun detached(message: UByteArray, nonce: UByteArray, key: UByteArray) : SecretBoxEncryptedDataAndTag
    fun openDetached(ciphertext: UByteArray, tag: UByteArray, nonce: UByteArray, key: UByteArray) : UByteArray

    fun keygen() : UByteArray
}
