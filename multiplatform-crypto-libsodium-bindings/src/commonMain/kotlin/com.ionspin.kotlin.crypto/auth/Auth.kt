package com.ionspin.kotlin.crypto.auth

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-Aug-2020
 */

val crypto_auth_BYTES = 32
val crypto_auth_KEYBYTES = 32

// do the 512 hmac and the deliver just 32 bytes of result
val crypto_auth_hmacsha512256_BYTES = 32
val crypto_auth_hmacsha512256_KEYBYTES = 32

val crypto_auth_hmacsha256_KEYBYTES = 32
val crypto_auth_hmacsha256_BYTES = 32

val crypto_auth_hmacsha512_KEYBYTES = 32
val crypto_auth_hmacsha512_BYTES = 64

/**
 * Authentication is a process of generating authentication data (tag) for a certain message. Its purpose is to assure
 * that the data hasn't been corrupted or tampered with during the transport.
 *
 * We support 3 variants:
 * - without suffix - HMAC-SHA512-256 (HMAC SHA512 with just the first 256 bits used)
 * - *HmacSha256 - HMAC-SHA256
 * - *HmacSha512 - HMAC-SHA512
 *
 * Each variant supports three operations:
 * - keygen - generate appropriate key for MAC function
 * - auth - generate the authentication data (tag/mac)
 * - verify - verify that the authenticatoin data (tag/mac) is correct
 */
expect object Auth {
    /**
     * Generate a secret key, meant to be used with auth function.
     */
    fun authKeygen() : UByteArray

    /**
     * Generate a HMAC-SHA512-256 authentication data - Message Authentication Code - tag
     */
    fun auth(message: UByteArray, key: UByteArray) : UByteArray

    /**
     * Verify that given message, secret key and tag, a newly calculated tag matches the received HMAC-SHA512-256 tag.
     */
    fun authVerify(tag: UByteArray, message: UByteArray, key: UByteArray) : Boolean

    /**
     * Generate a secret key, meant to be used with auth function.
     */
    fun authHmacSha256Keygen() : UByteArray
    /**
     * Generate a HMAC-SHA256 authentication data - Message Authentication Code - tag
     */
    fun authHmacSha256(message: UByteArray, key: UByteArray) : UByteArray
    /**
     * Verify that given message, secret key and tag, a newly calculated tag matches the received HMAC-SHA256 tag.
     */
    fun authHmacSha256Verify(tag: UByteArray, message: UByteArray, key: UByteArray) : Boolean

    /**
     * Generate a secret key, meant to be used with auth function.
     */
    fun authHmacSha512Keygen() : UByteArray
    /**
     * Generate a HMAC-SHA512 authentication data - Message Authentication Code - tag
     */
    fun authHmacSha512(message: UByteArray, key: UByteArray) : UByteArray
    /**
     * Verify that given message, secret key and tag, a newly calculated tag matches the received HMAC-SHA512 tag.
     */
    fun authHmacSha512Verify(tag: UByteArray, message: UByteArray, key: UByteArray) : Boolean

}
