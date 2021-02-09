package com.ionspin.kotlin.crypto.keyexchange

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.toHexString
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Oct-2020
 */
class KeyExchangeTest {
    @Test
    fun testKeyExchange() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val keypairClient = KeyExchange.keypair()
            val keypairServer = KeyExchange.keypair()
            val clientSessionKeyPair = KeyExchange.clientSessionKeys(
                keypairClient.publicKey,
                keypairClient.secretKey,
                keypairServer.publicKey
            )
            val serverSessionKeyPair = KeyExchange.serverSessionKeys(
                keypairServer.publicKey,
                keypairServer.secretKey,
                keypairClient.publicKey
            )
            println(clientSessionKeyPair.receiveKey.toHexString())
            println(clientSessionKeyPair.sendKey.toHexString())
            println(serverSessionKeyPair.receiveKey.toHexString())
            println(serverSessionKeyPair.sendKey.toHexString())
            assertTrue {
                clientSessionKeyPair.sendKey.contentEquals(serverSessionKeyPair.receiveKey)
            }
            assertTrue {
                clientSessionKeyPair.receiveKey.contentEquals(serverSessionKeyPair.sendKey)
            }
        }
    }

    @Test
    fun testKeyExchangeSeeded() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val seed = UByteArray(crypto_kx_SEEDBYTES) { 7U }
            val keypairClient = KeyExchange.seedKeypair(seed)
            val keypairServer = KeyExchange.seedKeypair(seed)

            println(keypairClient.publicKey.toHexString())
            println(keypairClient.secretKey.toHexString())
            println(keypairServer.publicKey.toHexString())
            println(keypairServer.secretKey.toHexString())

            assertTrue { keypairClient.secretKey.contentEquals(keypairServer.secretKey) }
            assertTrue { keypairClient.secretKey.contentEquals(keypairServer.secretKey) }
            val clientSessionKeyPair = KeyExchange.clientSessionKeys(
                keypairClient.publicKey,
                keypairClient.secretKey,
                keypairServer.publicKey
            )
            val serverSessionKeyPair = KeyExchange.serverSessionKeys(
                keypairServer.publicKey,
                keypairServer.secretKey,
                keypairClient.publicKey
            )
            println(clientSessionKeyPair.receiveKey.toHexString())
            println(clientSessionKeyPair.sendKey.toHexString())
            println(serverSessionKeyPair.receiveKey.toHexString())
            println(serverSessionKeyPair.sendKey.toHexString())
            assertTrue {
                clientSessionKeyPair.sendKey.contentEquals(serverSessionKeyPair.receiveKey)
            }
            assertTrue {
                clientSessionKeyPair.receiveKey.contentEquals(serverSessionKeyPair.sendKey)
            }
        }
    }
}
