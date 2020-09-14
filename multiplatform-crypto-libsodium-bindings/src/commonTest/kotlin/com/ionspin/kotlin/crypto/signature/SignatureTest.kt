package com.ionspin.kotlin.crypto.signature

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 14/Sep/2020
 */
class SignatureTest {

    @Test
    fun testSignAndVerify() {
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
                Signature.open(tamperedMessage, keys.publicKey)
            }
        }

    }
}