package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.authenticated.*
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bMultipart
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bPure
import com.ionspin.kotlin.crypto.hash.sha.Sha256Pure
import com.ionspin.kotlin.crypto.hash.sha.Sha512Pure
import com.ionspin.kotlin.crypto.keyderivation.ArgonResult
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Pure

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */
object CryptoInitializerPure : CryptoInitializer {
    override suspend fun initialize() {
        //Nothing to do atm.
    }

    fun initializeWithCallback(done: () -> Unit) {
        done()
    }

    override fun isInitialized(): Boolean {
        return true
    }
}

object CryptoPrimitives : PrimitivesApi {
    private fun checkInitialization() {
        CryptoInitializerPure.isInitialized()
    }

    override fun hashBlake2bMultipart(key: UByteArray?, hashLength: Int): Blake2bMultipart {
        checkInitialization()
        return Blake2bPure(key, hashLength)
    }

    override fun hashBlake2b(message: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        checkInitialization()
        return Blake2bPure.digest(message, key, hashLength)
    }

    override fun hashSha256Multipart(): com.ionspin.kotlin.crypto.hash.sha.Sha256 {
        checkInitialization()
        return Sha256Pure()
    }

    override fun hashSha256(message: UByteArray): UByteArray {
        checkInitialization()
        return Sha256Pure.digest(inputMessage =  message)
    }

    override fun hashSha512Multipart(): com.ionspin.kotlin.crypto.hash.sha.Sha512Multipart {
        checkInitialization()
        return Sha512Pure()
    }

    override fun hashSha512(message: UByteArray): UByteArray {
        checkInitialization()
        return Sha512Pure.digest(inputMessage =  message)
    }

    override fun deriveKey(
        password: String,
        salt: String?,
        key: String,
        associatedData: String,
        parallelism: Int,
        tagLength: Int,
        memory: Int,
        numberOfIterations: Int
    ): ArgonResult {
        return Argon2Pure.derive(
            password,
            salt,
            key,
            associatedData,
            parallelism,
            tagLength,
            memory,
            numberOfIterations
        )
    }
}

fun SymmetricKey.Companion.randomKey() : SymmetricKey {
    return SymmetricKey(SRNG.getRandomBytes(32))
}

object Crypto {

    object Hash : HashApi {
        override fun hash(data: UByteArray, key : UByteArray) : HashedData {
            return HashedData(Blake2bPure.digest(data, key))
        }

        override fun multipartHash(key: UByteArray?) : com.ionspin.kotlin.crypto.hash.MultipartHash {
            return Blake2bPure(key)
        }
    }

    object Encryption : EncryptionApi {
        override fun encrypt(key: SymmetricKey, data : Encryptable<*>, additionalData : UByteArray) : EncryptedData {
            if (key.value.size != 32) {
                throw RuntimeException("Invalid key size! Required 32, supplied ${key.value.size}")
            }
            val nonce = SRNG.getRandomBytes(24)
            return EncryptedData(XChaCha20Poly1305Pure.encrypt(key.value, nonce, data.toEncryptableForm(), additionalData), nonce)

        }

        override fun <T: Encryptable<T>> decrypt(key: SymmetricKey, encryptedData : EncryptedData, additionalData: UByteArray, byteArrayDeserializer : (UByteArray) -> T) : T {
            return byteArrayDeserializer(XChaCha20Poly1305Pure.decrypt(key.value, encryptedData.nonce, encryptedData.ciphertext, additionalData))

        }

        override fun createMultipartEncryptor(key: SymmetricKey) : MultipartAuthenticatedEncryption {
            return MultipartAuthenticatedEncryptor(key)
        }

        override fun createMultipartDecryptor(key: SymmetricKey, header: MultipartEncryptionHeader) : MultipartAuthenticatedDecryption {
            val decryptor = XChaCha20Poly1305Pure(key.value, header.nonce)
            return MultipartAuthenticatedDecryptor(decryptor)
        }
    }
}

class MultipartAuthenticatedEncryptor internal constructor(val key : SymmetricKey) : MultipartAuthenticatedEncryption {
    val header = MultipartEncryptionHeader(SRNG.getRandomBytes(24))
    val primitive = XChaCha20Poly1305Pure(key.value, header.nonce)
    override fun encryptPartialData(data: UByteArray, additionalData: UByteArray): EncryptedDataPart {
        return EncryptedDataPart(primitive.streamEncrypt(data, additionalData, 0U))
    }

    override fun startEncryption(): MultipartEncryptionHeader {
        return header
    }

    override fun cleanup() {
        primitive.cleanup()
    }
}

class MultipartAuthenticatedDecryptor internal constructor(val decryptor: XChaCha20Poly1305Pure) : MultipartAuthenticatedDecryption {
    override fun decryptPartialData(data: EncryptedDataPart, additionalData: UByteArray): DecryptedDataPart {
        return DecryptedDataPart(decryptor.streamDecrypt(data.data, additionalData, 0U))
    }

    override fun cleanup() {
        decryptor.cleanup()
    }

}
