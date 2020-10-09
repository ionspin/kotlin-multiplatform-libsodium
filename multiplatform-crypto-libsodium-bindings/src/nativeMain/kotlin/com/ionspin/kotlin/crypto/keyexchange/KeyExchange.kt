package com.ionspin.kotlin.crypto.keyexchange

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.pin
import libsodium.crypto_kx_client_session_keys
import libsodium.crypto_kx_keypair
import libsodium.crypto_kx_seed_keypair
import libsodium.crypto_kx_server_session_keys

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Oct-2020
 */
actual object KeyExchange {
    actual fun clientSessionKeys(clientPublicKey: UByteArray, clientSecretKey: UByteArray, serverPublicKey: UByteArray) : KeyExchangeSessionKeyPair {
        val receiveKey = UByteArray(crypto_kx_SESSIONKEYBYTES)
        val sendKey = UByteArray(crypto_kx_SESSIONKEYBYTES)

        val clientPublicKeyPinned = clientPublicKey.pin()
        val clientSecretKeyPinned = clientSecretKey.pin()
        val serverPublicKeyPinned = serverPublicKey.pin()
        val receiveKeyPinned = receiveKey.pin()
        val sendKeyPinned = sendKey.pin()

        crypto_kx_client_session_keys(
            receiveKeyPinned.toPtr(),
            sendKeyPinned.toPtr(),
            clientPublicKeyPinned.toPtr(),
            clientSecretKeyPinned.toPtr(),
            serverPublicKeyPinned.toPtr()
        )

        clientPublicKeyPinned.unpin()
        clientSecretKeyPinned.unpin()
        serverPublicKeyPinned.unpin()
        receiveKeyPinned.unpin()
        sendKeyPinned.unpin()

        return KeyExchangeSessionKeyPair(receiveKey, sendKey)
    }

    actual fun keypair() : KeyExchangeKeyPair {
        val publicKey = UByteArray(crypto_kx_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_kx_SECRETKEYBYTES)

        val publicKeyPinned = publicKey.pin()
        val secretKeyPinned = secretKey.pin()
        crypto_kx_keypair(publicKeyPinned.toPtr(), secretKeyPinned.toPtr())
        publicKeyPinned.unpin()
        secretKeyPinned.unpin()

        return KeyExchangeKeyPair(publicKey, secretKey)
    }

    actual fun seedKeypair(seed: UByteArray) : KeyExchangeKeyPair {
        val publicKey = UByteArray(crypto_kx_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_kx_SECRETKEYBYTES)

        val seedPinned = seed.pin()
        val publicKeyPinned = publicKey.pin()
        val secretKeyPinned = secretKey.pin()

        crypto_kx_seed_keypair(publicKeyPinned.toPtr(), secretKeyPinned.toPtr(), seedPinned.toPtr())

        publicKeyPinned.unpin()
        secretKeyPinned.unpin()
        seedPinned.unpin()

        return KeyExchangeKeyPair(publicKey, secretKey)
    }

    actual fun serverSessionKeys(serverPublicKey: UByteArray, serverSecretKey: UByteArray, clientPublicKey: UByteArray) : KeyExchangeSessionKeyPair {
        val receiveKey = UByteArray(crypto_kx_SESSIONKEYBYTES)
        val sendKey = UByteArray(crypto_kx_SESSIONKEYBYTES)

        val serverPublicKeyPinned = serverPublicKey.pin()
        val serverSecretKeyPinned = serverSecretKey.pin()
        val clientPublicKeyPinned = clientPublicKey.pin()
        val receiveKeyPinned = receiveKey.pin()
        val sendKeyPinned = sendKey.pin()

        crypto_kx_server_session_keys(
            receiveKeyPinned.toPtr(),
            sendKeyPinned.toPtr(),
            serverPublicKeyPinned.toPtr(),
            serverSecretKeyPinned.toPtr(),
            clientPublicKeyPinned.toPtr()
        )

        serverPublicKeyPinned.unpin()
        serverSecretKeyPinned.unpin()
        clientPublicKeyPinned.unpin()
        receiveKeyPinned.unpin()
        sendKeyPinned.unpin()

        return KeyExchangeSessionKeyPair(receiveKey, sendKey)
    }
}
