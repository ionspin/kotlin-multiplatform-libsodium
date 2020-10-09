package com.ionspin.kotlin.crypto.keyexchange

import com.ionspin.kotlin.crypto.getSodium
import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
import org.khronos.webgl.Uint8Array

actual object KeyExchange {
    actual fun clientSessionKeys(clientPublicKey: UByteArray, clientSecretKey: UByteArray, serverPublicKey: UByteArray) : KeyExchangeSessionKeyPair {


        val result = getSodium().crypto_kx_client_session_keys(
            clientPublicKey.toUInt8Array(),
            clientSecretKey.toUInt8Array(),
            serverPublicKey.toUInt8Array()
        )

        val receiveKey = (result.sharedRx as Uint8Array).toUByteArray()
        val sendKey = (result.sharedTx as Uint8Array).toUByteArray()



        return KeyExchangeSessionKeyPair(receiveKey, sendKey)
    }

    actual fun keypair() : KeyExchangeKeyPair {
        val result = getSodium().crypto_kx_keypair()

        val publicKey = (result.publicKey as Uint8Array).toUByteArray()
        val secretKey = (result.privateKey as Uint8Array).toUByteArray()

        return KeyExchangeKeyPair(publicKey, secretKey)
    }

    actual fun seedKeypair(seed: UByteArray) : KeyExchangeKeyPair {
        val result = getSodium().crypto_kx_seed_keypair(seed.toUInt8Array())

        val publicKey = (result.publicKey as Uint8Array).toUByteArray()
        val secretKey = (result.privateKey as Uint8Array).toUByteArray()

        return KeyExchangeKeyPair(publicKey, secretKey)
    }

    actual fun serverSessionKeys(serverPublicKey: UByteArray, serverSecretKey: UByteArray, clientPublicKey: UByteArray) : KeyExchangeSessionKeyPair {

        val result = getSodium().crypto_kx_server_session_keys(
            serverPublicKey.toUInt8Array(),
            serverSecretKey.toUInt8Array(),
            clientPublicKey.toUInt8Array()
        )

        val receiveKey = (result.sharedRx as Uint8Array).toUByteArray()
        val sendKey = (result.sharedTx as Uint8Array).toUByteArray()

        return KeyExchangeSessionKeyPair(receiveKey, sendKey)
    }
}
