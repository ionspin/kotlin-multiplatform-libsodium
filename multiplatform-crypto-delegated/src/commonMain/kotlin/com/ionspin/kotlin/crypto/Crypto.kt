package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.authenticated.XChaCha20Poly1305Delegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bProperties
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegatedStateless
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bMultipart
import com.ionspin.kotlin.crypto.hash.sha.*
import com.ionspin.kotlin.crypto.keyderivation.ArgonResult

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */

object CryptoInitializerDelegated : CryptoInitializer {
    override suspend fun initialize() {
        Initializer.initialize()
    }

    fun initializeWithCallback(done: () -> Unit) {
        Initializer.initializeWithCallback(done)
    }

    override fun isInitialized(): Boolean {
        return Initializer.isInitialized()
    }
}

object CryptoPrimitives : PrimitivesApi {

    object Blake2b {
        fun updateable(key: UByteArray? = null, hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): com.ionspin.kotlin.crypto.hash.blake2b.Blake2bMultipart {
            checkInitialization()
            return Blake2bDelegated(key, hashLength)
        }

        fun stateless(message: UByteArray, key: UByteArray = ubyteArrayOf(), hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): UByteArray {
            checkInitialization()
            return Blake2bDelegatedStateless.digest(message, key, hashLength)
        }
    }

    object Sha256 {
        fun updateable(): com.ionspin.kotlin.crypto.hash.sha.Sha256 {
            checkInitialization()
            return Sha256Delegated()
        }

        fun stateless(message: UByteArray) : UByteArray{
            checkInitialization()
            return Sha256StatelessDelegated.digest(inputMessage =  message)
        }
    }

    object Sha512 {
        fun updateable(): Sha512Multipart {
            checkInitialization()
            return Sha512Delegated()
        }

        fun stateless(message: UByteArray) : UByteArray {
            checkInitialization()
            return Sha512StatelessDelegated.digest(inputMessage =  message)
        }
    }

    private fun checkInitialization() {
        if (!Initializer.isInitialized()) {
            throw RuntimeException("Platform library not initialized, check if you called Initializer.initialize()")
        }
    }

    override fun hashBlake2bMultipart(key: UByteArray?, hashLength: Int): Blake2bMultipart {
        checkInitialization()
        return Blake2bDelegated(key, hashLength)
    }

    override fun hashBlake2b(message: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        checkInitialization()
        return Blake2bDelegatedStateless.digest(message, key, hashLength)
    }

    override fun hashSha256Multipart(): com.ionspin.kotlin.crypto.hash.sha.Sha256 {
        checkInitialization()
        return Sha256Delegated()
    }

    override fun hashSha256(message: UByteArray): UByteArray {
        checkInitialization()
        return Sha256StatelessDelegated.digest(inputMessage =  message)
    }

    override fun hashSha512Multipart(): com.ionspin.kotlin.crypto.hash.sha.Sha512Multipart {
        checkInitialization()
        return Sha512Delegated()
    }

    override fun hashSha512(message: UByteArray): UByteArray {
        checkInitialization()
        return Sha512StatelessDelegated.digest(inputMessage =  message)
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
//        return Argon2Delegated.derive(
//            password,
//            salt,
//            key,
//            associatedData,
//            parallelism
//            tagLength,
//            memory,
//            numberOfIterations
//        )
        TODO()
    }

}


fun SymmetricKey.Companion.randomKey() : SymmetricKey {
    return SymmetricKey(SRNG.getRandomBytes(32))
}

object Crypto {

    object Hash : HashApi {
        override fun hash(data: UByteArray, key : UByteArray) : HashedData {
            return HashedData(Blake2bDelegatedStateless.digest(data, key))
        }

        override fun multipartHash(key: UByteArray?) : com.ionspin.kotlin.crypto.hash.MultipartHash {
            return Blake2bDelegated(key)
        }
    }

    object Encryption : EncryptionApi {
        override fun encrypt(key: SymmetricKey, data : Encryptable<*>, associatedData : UByteArray) : EncryptedData {
            if (key.value.size != 32) {
                throw RuntimeException("Invalid key size! Required 32, supplied ${key.value.size}")
            }
            val nonce = SRNG.getRandomBytes(24)
            return EncryptedData(XChaCha20Poly1305Delegated.encrypt(key.value, nonce, data.toEncryptableForm(), associatedData), nonce)

        }

        override fun <T: Encryptable<T>> decrypt(key: SymmetricKey, encryptedData : EncryptedData, associatedData: UByteArray, byteArrayDeserializer : (UByteArray) -> T) : T {
            return byteArrayDeserializer(XChaCha20Poly1305Delegated.decrypt(key.value, encryptedData.nonce, encryptedData.ciphertext, associatedData))

        }

        override fun createMultipartEncryptor(key: SymmetricKey): MultipartAuthenticatedEncryption {
            return MultipartAuthenticatedEncryptor(key)
        }

        override fun createMultipartDecryptor(key: SymmetricKey, header: MultipartEncryptionHeader) : MultipartAuthenticatedDecryption {
            val decryptor = XChaCha20Poly1305Delegated()
            decryptor.initializeForDecryption(key.value, header.nonce)
            return MultipartAuthenticatedDecryptor(decryptor)
        }


    }
}

class MultipartAuthenticatedEncryptor internal constructor(val key : SymmetricKey) : MultipartAuthenticatedEncryption {

    val header : MultipartEncryptionHeader
    val primitive = XChaCha20Poly1305Delegated()
    init {
        header = MultipartEncryptionHeader(primitive.initializeForEncryption(key.value))
    }

    override fun startEncryption(): MultipartEncryptionHeader {
        return header
    }

    override fun encryptPartialData(data: UByteArray, associatedData: UByteArray): EncryptedDataPart {
        return EncryptedDataPart(primitive.encrypt(data, associatedData))
    }

    override fun cleanup() {
        primitive.cleanup()
    }
}


class MultipartAuthenticatedDecryptor internal constructor(val decryptor: XChaCha20Poly1305Delegated) : MultipartAuthenticatedDecryption {
    override fun decryptPartialData(data: EncryptedDataPart, associatedData: UByteArray): DecryptedDataPart {
        return DecryptedDataPart(decryptor.decrypt(data.data, associatedData))
    }

    override fun cleanup() {
        decryptor.cleanup()
    }

}

expect object Initializer {
    fun isInitialized() : Boolean

    suspend fun initialize()

    fun initializeWithCallback(done: () -> (Unit))
}

