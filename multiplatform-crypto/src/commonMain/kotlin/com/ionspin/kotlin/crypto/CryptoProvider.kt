package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.authenticated.XChaCha20Poly1305Pure
import com.ionspin.kotlin.crypto.hash.UpdatableHash
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bProperties
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bPure
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.hash.sha.Sha256Pure
import com.ionspin.kotlin.crypto.hash.sha.Sha512Pure
import com.ionspin.kotlin.crypto.util.toHexString

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */
typealias Blake2bPureStateless = Blake2bPure.Companion
typealias Sha256PureStateless = Sha256Pure.Companion
typealias Sha512PureStateless = Sha512Pure.Companion

object Primitives : CryptoProvider {
    override suspend fun initialize() {
        //Nothing to do atm.
    }

    fun initializeWithCallback(done: () -> Unit) {
        done()
    }

    object Blake2b {
        fun updateable(key: UByteArray? = null, hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): com.ionspin.kotlin.crypto.hash.blake2b.Blake2b {
            checkInitialization()
            return Blake2bPure(key, hashLength)
        }

        fun stateless(message: UByteArray, key: UByteArray = ubyteArrayOf(), hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): UByteArray {
            checkInitialization()
            return Blake2bPureStateless.digest(message, key, hashLength)
        }
    }

    object Sha256 {
        fun updateable(): com.ionspin.kotlin.crypto.hash.sha.Sha256 {
            checkInitialization()
            return Sha256Pure()
        }

        fun stateless(message: UByteArray) : UByteArray{
            checkInitialization()
            return Sha256PureStateless.digest(inputMessage =  message)
        }
    }

    object Sha512 {
        fun updateable(): com.ionspin.kotlin.crypto.hash.sha.Sha512 {
            checkInitialization()
            return Sha512Pure()
        }

        fun stateless(message: UByteArray) : UByteArray {
            checkInitialization()
            return Sha512PureStateless.digest(inputMessage =  message)
        }
    }


    private fun checkInitialization() {
        // Nothing to do atm
    }

}

inline class EncryptableString(val content: String) : Encryptable<EncryptableString> {
    override fun toEncryptableForm(): UByteArray {
        return content.encodeToUByteArray()
    }

    override fun fromEncryptableForm(): (UByteArray) -> EncryptableString {
        return { uByteArray ->
            EncryptableString(uByteArray.toByteArray().decodeToString())
        }
    }

    fun asString() : String = content


}

fun String.asEncryptableString() : EncryptableString {
    return EncryptableString(this)
}

interface Encryptable<T> {
    fun toEncryptableForm() : UByteArray
    fun fromEncryptableForm() : (UByteArray) -> T
}

data class HashedData(val hash: UByteArray) {
    fun toHexString() : String {
        return hash.toHexString()
    }
}

data class SymmetricKey(val value : UByteArray) {
    companion object {
        fun randomKey() : SymmetricKey {
            return SymmetricKey(SRNG.getRandomBytes(32))
        }
    }
}

data class EncryptedData internal constructor(val ciphertext: UByteArray, val nonce: UByteArray) {

}

object PublicApi {

    object Hash {
        fun hash(data: UByteArray, key : UByteArray = ubyteArrayOf()) : HashedData {
            return HashedData(Blake2bPureStateless.digest(data, key))
        }

        fun updateableHash(key: UByteArray? = null) : UpdatableHash {
            return Blake2bPure(key)
        }
    }
    object Symmetric {
        fun encrypt(key: SymmetricKey, data : Encryptable<*>, additionalData : UByteArray = ubyteArrayOf()) : EncryptedData {
            if (key.value.size != 32) {
                throw RuntimeException("Invalid key size! Required 32, supplied ${key.value.size}")
            }
            val nonce = SRNG.getRandomBytes(24)
            return EncryptedData(XChaCha20Poly1305Pure.encrypt(key.value, nonce, data.toEncryptableForm(), additionalData), nonce)

        }

        fun <T: Encryptable<T>> decrypt(key: SymmetricKey, encryptedData : EncryptedData, additionalData: UByteArray, byteArrayDeserializer : (UByteArray) -> T) : T {
            return byteArrayDeserializer(XChaCha20Poly1305Pure.decrypt(key.value, encryptedData.nonce, encryptedData.ciphertext, additionalData))

        }
    }
}