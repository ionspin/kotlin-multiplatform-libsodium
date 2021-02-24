package com.ionspin.kotlin.crypto.keyexchange

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object KeyExchange {
    actual fun clientSessionKeys(clientPublicKey: UByteArray, clientSecretKey: UByteArray, serverPublicKey: UByteArray) : KeyExchangeSessionKeyPair {
        val receiveKey = UByteArray(crypto_kx_SESSIONKEYBYTES)
        val sendKey = UByteArray(crypto_kx_SESSIONKEYBYTES)

        sodiumJna.crypto_kx_client_session_keys(
            receiveKey.asByteArray(),
            sendKey.asByteArray(),
            clientPublicKey.asByteArray(),
            clientSecretKey.asByteArray(),
            serverPublicKey.asByteArray()
        )



        return KeyExchangeSessionKeyPair(receiveKey, sendKey)
    }

    actual fun keypair() : KeyExchangeKeyPair {
        val publicKey = UByteArray(crypto_kx_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_kx_SECRETKEYBYTES)


        sodiumJna.crypto_kx_keypair(publicKey.asByteArray(), secretKey.asByteArray())


        return KeyExchangeKeyPair(publicKey, secretKey)
    }

    actual fun seedKeypair(seed: UByteArray) : KeyExchangeKeyPair {
        val publicKey = UByteArray(crypto_kx_PUBLICKEYBYTES)
        val secretKey = UByteArray(crypto_kx_SECRETKEYBYTES)

        sodiumJna.crypto_kx_seed_keypair(publicKey.asByteArray(), secretKey.asByteArray(), seed.asByteArray())

        return KeyExchangeKeyPair(publicKey, secretKey)
    }

    actual fun serverSessionKeys(serverPublicKey: UByteArray, serverSecretKey: UByteArray, clientPublicKey: UByteArray) : KeyExchangeSessionKeyPair {
        val receiveKey = UByteArray(crypto_kx_SESSIONKEYBYTES)
        val sendKey = UByteArray(crypto_kx_SESSIONKEYBYTES)

        sodiumJna.crypto_kx_server_session_keys(
            receiveKey.asByteArray(),
            sendKey.asByteArray(),
            serverPublicKey.asByteArray(),
            serverSecretKey.asByteArray(),
            clientPublicKey.asByteArray()
        )

        return KeyExchangeSessionKeyPair(receiveKey, sendKey)
    }
}
