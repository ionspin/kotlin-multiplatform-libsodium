package com.ionspin.kotlin.crypto.aead

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-Aug-2020
 */
class AuthenticatedEncryptionWithAssociatedDataTest {
    @Test
    fun testXChaCha20Poly1305Ieft() = runTest {
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

            val encrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfEncrypt(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                encrypted,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testEmptyAssociatedData() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ("Ladies and Gentlemen of the class of '99: If I could offer you " +
                    "only one tip for the future, sunscreen would be it.").encodeToUByteArray()

            val associatedData = ubyteArrayOf()

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

            val encrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfEncrypt(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                encrypted,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testEmptyMessage() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ubyteArrayOf()

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

            val encrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfEncrypt(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                encrypted,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testEmptyCombination() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ubyteArrayOf()

            val associatedData = ubyteArrayOf()

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

            val encrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfEncrypt(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                encrypted,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecrypt(
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testXChaCha20Poly1305IeftDetached() = runTest {
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

            val encrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfEncryptDetached(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecryptDetached(
                encrypted.data,
                encrypted.tag,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.tag.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.xChaCha20Poly1305IetfDecryptDetached(
                    encrypted.data,
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testChaCha20Poly1305Ieft() = runTest {
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
                0x48U, 0x49U, 0x4aU, 0x4bU
            )

            val encrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305IetfEncrypt(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305IetfDecrypt(
                encrypted,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305IetfDecrypt(
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testChaCha20Poly1305IeftDetached() = runTest {
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
                0x48U, 0x49U, 0x4aU, 0x4bU
            )

            val encrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305IetfEncryptDetached(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305IetfDecryptDetached(
                encrypted.data,
                encrypted.tag,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.tag.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305IetfDecryptDetached(
                    encrypted.data,
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testChaCha20Poly1305() = runTest {
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
                0x40U, 0x41U, 0x42U, 0x43U, 0x44U, 0x45U, 0x46U, 0x47U
            )

            val encrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305Encrypt(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305Decrypt(
                encrypted,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305Decrypt(
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }

    @Test
    fun testChaCha20Poly1305Detached() = runTest {
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
                0x40U, 0x41U, 0x42U, 0x43U, 0x44U, 0x45U, 0x46U, 0x47U
            )

            val encrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305EncryptDetached(
                message,
                associatedData,
                nonce,
                key
            )
            val decrypted = AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305DecryptDetached(
                encrypted.data,
                encrypted.tag,
                associatedData,
                nonce,
                key
            )
            assertTrue {
                message.contentEquals(decrypted)
            }

            assertFailsWith(AeadCorrupedOrTamperedDataException::class) {
                val tamperedTag = encrypted.tag.copyOf()
                tamperedTag[3] = 0U
                tamperedTag[1] = 0U
                tamperedTag[0] = 0U
                AuthenticatedEncryptionWithAssociatedData.chaCha20Poly1305DecryptDetached(
                    encrypted.data,
                    tamperedTag,
                    associatedData,
                    nonce,
                    key
                )
            }
        }
    }
}
