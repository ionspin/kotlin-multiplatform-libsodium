package com.ionspin.kotlin.crypto.stream

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 11-Oct-2020
 */
const val crypto_stream_chacha20_KEYBYTES = 32
const val crypto_stream_chacha20_NONCEBYTES = 8
const val crypto_stream_chacha20_ietf_KEYBYTES = 32
const val crypto_stream_chacha20_ietf_NONCEBYTES = 12
const val crypto_stream_xchacha20_NONCEBYTES = 24
const val crypto_stream_xchacha20_KEYBYTES = 32


expect object Stream {
    fun chacha20Keygen() : UByteArray

    fun chacha20(clen: Int, nonce: UByteArray, key: UByteArray) : UByteArray
    fun chacha20Xor(message : UByteArray, nonce: UByteArray, key: UByteArray) : UByteArray
    fun chacha20XorIc(message : UByteArray, nonce: UByteArray, initialCounter: ULong, key: UByteArray) : UByteArray

    fun chacha20IetfXor(message : UByteArray, nonce: UByteArray, key: UByteArray) : UByteArray
    fun chacha20IetfXorIc(message : UByteArray, nonce: UByteArray, initialCounter: ULong, key: UByteArray) : UByteArray

//    fun xChacha20Keygen() : UByteArray
//
//    fun xChacha20Xor(message : UByteArray, nonce: UByteArray, key: UByteArray) : UByteArray
//    fun xChacha20XorIc(message : UByteArray, nonce: UByteArray, initialCounter: ULong, key: UByteArray) : UByteArray
}
