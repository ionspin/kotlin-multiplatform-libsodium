package com.ionspin.kotlin.crypto.secretbox

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 29-Aug-2020
 */
class SecretBoxTest {

    @Test
    fun secretBoxTestEasy() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ("Ladies and Gentlemen of the class of '99: If I could offer you " +
                    "only one tip for the future, sunscreen would be it.").encodeToUByteArray()


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

            val encrypted = SecretBox.easy(message, nonce, key)
            val decrypted = SecretBox.openEasy(encrypted, nonce, key)
            assertTrue { decrypted.contentEquals(message) }
            assertFailsWith(SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey::class) {
                val tamperedTag = encrypted.copyOf()
                tamperedTag[2] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                SecretBox.openEasy(tamperedTag, nonce, key)
            }
        }


    }

    @Test
    fun secretBoxTestDetached() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ("Ladies and Gentlemen of the class of '99: If I could offer you " +
                    "only one tip for the future, sunscreen would be it.").encodeToUByteArray()


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

            val encrypted = SecretBox.detached(message, nonce, key)
            val decrypted = SecretBox.openDetached(encrypted.data, encrypted.tag, nonce, key)
            assertTrue { decrypted.contentEquals(message) }
            assertFailsWith(SecretBoxCorruptedOrTamperedDataExceptionOrInvalidKey::class) {
                val tamperedTag = encrypted.tag.copyOf()
                tamperedTag[2] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                SecretBox.openDetached(encrypted.data, tamperedTag, nonce, key)
            }
        }


    }
}
