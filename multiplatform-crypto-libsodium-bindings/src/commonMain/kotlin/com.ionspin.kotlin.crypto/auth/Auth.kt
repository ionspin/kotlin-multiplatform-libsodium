package com.ionspin.kotlin.crypto.auth

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-Aug-2020
 */

val crypto_auth_BYTES = 32
val crypto_auth_KEYBYTES = 32

val crypto_auth_hmacsha256_KEYBYTES = 32
val crypto_auth_hmacsha256_BYTES =32

val crypto_auth_hmacsha512_KEYBYTES = 32
val crypto_auth_hmacsha512_BYTES = 64

expect object Auth {

    fun authKeygen() : UByteArray
    fun auth(message: UByteArray, key: UByteArray) : UByteArray
    fun authVerify(mac: UByteArray, message: UByteArray, key: UByteArray) : Boolean

    fun authHmacSha256Keygen() : UByteArray
    fun authHmacSha256(message: UByteArray, key: UByteArray) : UByteArray
    fun authHmacSha256Verify(mac: UByteArray, message: UByteArray, key: UByteArray) : Boolean

    fun authHmacSha512Keygen() : UByteArray
    fun authHmacSha512(message: UByteArray, key: UByteArray) : UByteArray
    fun authHmacSha512Verify(mac: UByteArray, message: UByteArray, key: UByteArray) : Boolean

}
