package com.ionspin.kotlin.crypto.signature

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 14/Sep/2020
 */
class SignatureTest {

    @Test
    fun testSignAndVerify() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val keys = Signature.keypair()
            val message = "Some text that will be signed".encodeToUByteArray()
            val signedMessage = Signature.sign(message, keys.secretKey)
            val verifiedMessage = Signature.open(signedMessage, keys.publicKey)
            assertTrue {
                verifiedMessage.contentEquals(message)
            }
            assertFailsWith(InvalidSignatureException::class) {
                val tamperedMessage = signedMessage.copyOf()
                tamperedMessage[crypto_sign_BYTES + 1] = 0U
                tamperedMessage[crypto_sign_BYTES + 2] = 0U
                tamperedMessage[crypto_sign_BYTES + 3] = 0U
                Signature.open(tamperedMessage, keys.publicKey)
            }
        }

    }

    @Test
    fun testDetachedSignAndVerify() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val keys = Signature.keypair()
            val message = "Some text that will be signed".encodeToUByteArray()
            val signature = Signature.detached(message, keys.secretKey)
            val verifiedMessage = Signature.verifyDetached(signature, message, keys.publicKey)
            assertFailsWith(InvalidSignatureException::class) {
                val tamperedSignature = signature.copyOf()
                tamperedSignature[crypto_sign_BYTES - 1] = 0U
                tamperedSignature[1] = 0U
                tamperedSignature[15] = 0U
                tamperedSignature[33] = 0U
                Signature.verifyDetached(tamperedSignature, message, keys.publicKey)
            }
        }
    }

    @Test
    fun testMultipart() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val keys = Signature.keypair()
            val message1 = "Some text that ".encodeToUByteArray()
            val message2 = "will be signed".encodeToUByteArray()
            val state = Signature.init()
            Signature.update(state, message1)
            Signature.update(state, message2)
            val signature = Signature.finalCreate(state, keys.secretKey)
            val verificationState = Signature.init()
            Signature.update(verificationState, message1)
            Signature.update(verificationState, message2)
            Signature.finalVerify(verificationState, signature, keys.publicKey)
            assertFailsWith(InvalidSignatureException::class) {
                val tamperedSignature = signature.copyOf()
                tamperedSignature[crypto_sign_BYTES - 1] = 0U
                tamperedSignature[crypto_sign_BYTES - 2] = 0U
                tamperedSignature[crypto_sign_BYTES - 3] = 0U
                Signature.finalVerify(verificationState, tamperedSignature, keys.publicKey)
            }
        }
    }
}
