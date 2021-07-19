package com.ionspin.kotlin.crypto.box

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import kotlin.random.Random
import kotlin.random.nextUBytes
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 01-Sep-2020
 */
class BoxTest {
    @Test
    fun keypairTest() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val keypair = Box.keypair()
            assertTrue {
                keypair.publicKey.size == crypto_box_PUBLICKEYBYTES
            }
            assertTrue {
                keypair.secretKey.size == crypto_box_SECRETKEYBYTES
            }
        }
    }

    @Test
    fun testBoxEasy() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = "Message message message".encodeToUByteArray()
            val senderKeypair = Box.keypair()
            val recipientKeypair = Box.keypair()
            val messageNonce = Random(0).nextUBytes(crypto_box_NONCEBYTES)
            val encrypted = Box.easy(message, messageNonce, recipientKeypair.publicKey, senderKeypair.secretKey)
            val decrypted = Box.openEasy(encrypted, messageNonce, senderKeypair.publicKey, recipientKeypair.secretKey)
            assertTrue {
                decrypted.contentEquals(message)
            }

            assertFailsWith<BoxCorruptedOrTamperedDataException>() {
                val tampered = encrypted.copyOf()
                tampered[1] = 0U
                tampered[2] = 0U
                tampered[3] = 0U
                Box.openEasy(tampered, messageNonce, senderKeypair.publicKey, recipientKeypair.secretKey)
            }
        }
    }

    @Test
    fun testBoxEasyDetached() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = "Message message message".encodeToUByteArray()
            val senderKeypair = Box.keypair()
            val recipientKeypair = Box.keypair()
            val messageNonce = Random(0).nextUBytes(crypto_box_NONCEBYTES)
            val encrypted = Box.detached(message, messageNonce, recipientKeypair.publicKey, senderKeypair.secretKey)
            val decrypted = Box.openDetached(
                encrypted.ciphertext,
                encrypted.tag,
                messageNonce,
                senderKeypair.publicKey,
                recipientKeypair.secretKey
            )
            assertTrue {
                decrypted.contentEquals(message)
            }

            assertFailsWith<BoxCorruptedOrTamperedDataException>() {
                val tampered = encrypted.ciphertext.copyOf()
                tampered[1] = 0U
                tampered[2] = 0U
                tampered[3] = 0U
                Box.openDetached(
                    tampered,
                    encrypted.tag,
                    messageNonce,
                    senderKeypair.publicKey,
                    recipientKeypair.secretKey
                )
            }
        }
    }

    @Test
    fun testBeforeNonceAndMessage() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = "Message message message".encodeToUByteArray()
            val senderKeypair = Box.keypair()
            val recipientKeypair = Box.keypair()
            val messageNonce = Random(0).nextUBytes(crypto_box_NONCEBYTES)
            val senderComputedSessionKey = Box.beforeNM(recipientKeypair.publicKey, senderKeypair.secretKey)
            val recipientComputedSessionKey = Box.beforeNM(senderKeypair.publicKey, recipientKeypair.secretKey)

            assertTrue {
                senderComputedSessionKey.contentEquals(recipientComputedSessionKey)
            }
            val encrypted = Box.easyAfterNM(message, messageNonce, senderComputedSessionKey)
            val decrypted = Box.openEasyAfterNM(encrypted, messageNonce, recipientComputedSessionKey)
            assertTrue {
                decrypted.contentEquals(message)
            }

            assertFailsWith<BoxCorruptedOrTamperedDataException>() {
                val tampered = encrypted.copyOf()
                tampered[1] = 0U
                tampered[2] = 0U
                tampered[3] = 0U
                Box.openEasyAfterNM(tampered, messageNonce, recipientComputedSessionKey)
            }
        }
    }

    @Test
    fun testSeal() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = "Message message message".encodeToUByteArray()
            val recipientKeypair = Box.keypair()
            val sealed = Box.seal(message, recipientKeypair.publicKey)
            val unsealed = Box.sealOpen(sealed, recipientKeypair.publicKey, recipientKeypair.secretKey)

            assertTrue {
                unsealed.contentEquals(message)
            }

            assertFailsWith<BoxCorruptedOrTamperedDataException>() {
                val tampered = sealed.copyOf()
                tampered[1] = 0U
                tampered[2] = 0U
                tampered[3] = 0U
                Box.sealOpen(tampered, recipientKeypair.publicKey, recipientKeypair.secretKey)
            }
        }
    }


}
