package com.ionspin.kotlin.crypto.secretstream

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexColumnsPrint
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 15-Aug-2020
 */
class SecretStreamTest {


    @Test
    fun testSecretStream() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ("Ladies and Gentlemen of the class of '99: If I could offer you " +
                    "only one tip for the future, sunscreen would be it.").encodeToUByteArray()

            val associatedData = ubyteArrayOf(
                0x50U, 0x51U, 0x52U, 0x53U, 0xc0U, 0xc1U, 0xc2U, 0xc3U, 0xc4U, 0xc5U, 0xc6U, 0xc7U
            )
            val key = ubyteArrayOf(
                0x80U, 0x81U, 0x82U, 0x83U, 0x84U, 0x85U, 0x86U, 0x87U,
                0x88U, 0x89U, 0x8aU, 0x8bU, 0x8cU, 0x8dU, 0x8eU, 0x8fU,
                0x90U, 0x91U, 0x92U, 0x93U, 0x94U, 0x95U, 0x96U, 0x97U,
                0x98U, 0x99U, 0x9aU, 0x9bU, 0x9cU, 0x9dU, 0x9eU, 0x9fU,
            )

            val nonce = ubyteArrayOf(
                0x40U, 0x41U, 0x42U, 0x43U, 0x44U, 0x45U, 0x46U, 0x47U,
                0x48U, 0x49U, 0x4aU, 0x4bU, 0x4cU, 0x4dU, 0x4eU, 0x4fU,
                0x50U, 0x51U, 0x52U, 0x53U, 0x54U, 0x55U, 0x56U, 0x57U,
            )

            val expected = ubyteArrayOf(
                0xbdU, 0x6dU, 0x17U, 0x9dU, 0x3eU, 0x83U, 0xd4U, 0x3bU,
                0x95U, 0x76U, 0x57U, 0x94U, 0x93U, 0xc0U, 0xe9U, 0x39U,
                0x57U, 0x2aU, 0x17U, 0x00U, 0x25U, 0x2bU, 0xfaU, 0xccU,
                0xbeU, 0xd2U, 0x90U, 0x2cU, 0x21U, 0x39U, 0x6cU, 0xbbU,
                0x73U, 0x1cU, 0x7fU, 0x1bU, 0x0bU, 0x4aU, 0xa6U, 0x44U,
                0x0bU, 0xf3U, 0xa8U, 0x2fU, 0x4eU, 0xdaU, 0x7eU, 0x39U,
                0xaeU, 0x64U, 0xc6U, 0x70U, 0x8cU, 0x54U, 0xc2U, 0x16U,
                0xcbU, 0x96U, 0xb7U, 0x2eU, 0x12U, 0x13U, 0xb4U, 0x52U,
                0x2fU, 0x8cU, 0x9bU, 0xa4U, 0x0dU, 0xb5U, 0xd9U, 0x45U,
                0xb1U, 0x1bU, 0x69U, 0xb9U, 0x82U, 0xc1U, 0xbbU, 0x9eU,
                0x3fU, 0x3fU, 0xacU, 0x2bU, 0xc3U, 0x69U, 0x48U, 0x8fU,
                0x76U, 0xb2U, 0x38U, 0x35U, 0x65U, 0xd3U, 0xffU, 0xf9U,
                0x21U, 0xf9U, 0x66U, 0x4cU, 0x97U, 0x63U, 0x7dU, 0xa9U,
                0x76U, 0x88U, 0x12U, 0xf6U, 0x15U, 0xc6U, 0x8bU, 0x13U,
                0xb5U, 0x2eU, 0xc0U, 0x87U, 0x59U, 0x24U, 0xc1U, 0xc7U,
                0x98U, 0x79U, 0x47U, 0xdeU, 0xafU, 0xd8U, 0x78U, 0x0aU,
                0xcfU, 0x49U
            )
            message.hexColumnsPrint()
            println("---- init enc ----")
            val stateAndHeader = SecretStream.xChaCha20Poly1305InitPush(key)
            println("---- encrypt ----")
            val encrypted =
                SecretStream.xChaCha20Poly1305Push(stateAndHeader.state, message, ubyteArrayOf(), 0U)
            encrypted.hexColumnsPrint()
            println("---- init dec ----")
            val decryptState = SecretStream.xChaCha20Poly1305InitPull(key, stateAndHeader.header)
            println("---- decrypt ----")
            val decrypted =
                SecretStream.xChaCha20Poly1305Pull(decryptState.state, encrypted, ubyteArrayOf())
            decrypted.decryptedData.hexColumnsPrint()
            assertTrue {
                decrypted.decryptedData.contentEquals(message)
            }
            assertFailsWith(SecretStreamCorruptedOrTamperedDataException::class) {
                encrypted[encrypted.size - 5] = 0U
                encrypted[encrypted.size - 4] = 0U
                encrypted[encrypted.size - 3] = 0U
                val decryptState = SecretStream.xChaCha20Poly1305InitPull(key, stateAndHeader.header)
                val decrypted =
                    SecretStream.xChaCha20Poly1305Pull(decryptState.state, encrypted, ubyteArrayOf())
            }


        }
    }


}
// TODO modify nonce in state so we can have reproducible tests, theres already a similar way of doing this
// in crypto delegated project XChaCha20Poly1305 test
expect fun modifyState(state: SecretStreamState, forceNonce: UByteArray)


