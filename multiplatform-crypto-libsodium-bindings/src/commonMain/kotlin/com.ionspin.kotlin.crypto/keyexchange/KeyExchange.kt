package com.ionspin.kotlin.crypto.keyexchange

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Oct-2020
 */

const val crypto_kx_PUBLICKEYBYTES = 32
const val crypto_kx_SECRETKEYBYTES = 32
const val crypto_kx_SEEDBYTES = 32
const val crypto_kx_SESSIONKEYBYTES = 32
const val crypto_kx_PRIMITIVE = "x25519blake2b"

data class KeyExchangeKeyPair(val publicKey: UByteArray, val secretKey: UByteArray)
data class KeyExchangeSessionKeyPair(val receiveKey: UByteArray, val sendKey: UByteArray)

expect object KeyExchange {
    fun clientSessionKeys(clientPublicKey: UByteArray, clientSecretKey: UByteArray, serverPublicKey: UByteArray) : KeyExchangeSessionKeyPair
    fun keypair() : KeyExchangeKeyPair
    fun seedKeypair(seed: UByteArray) : KeyExchangeKeyPair
    fun serverSessionKeys(serverPublicKey: UByteArray, serverSecretKey: UByteArray, clientPublicKey: UByteArray) : KeyExchangeSessionKeyPair
}
