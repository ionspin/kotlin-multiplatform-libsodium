package com.ionspin.kotlin.crypto

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

object Crypto : CryptoProvider {
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

inline class EncryptableString(val content: String) : Encryptable {
    override fun encryptableData(): UByteArray {
        return content.encodeToUByteArray()
    }

    fun asString() : String = content

}

fun String.asEncryptableString() : EncryptableString {
    return EncryptableString(this)
}

interface Encryptable {
    fun encryptableData() : UByteArray
}

object PublicApi {
    data class HashedData(val hash: UByteArray) {
        fun toHexString() : String {
            return hash.toHexString()
        }
    }

    data class EncryptedData(val encrypted: UByteArray)



    object Hash {
        fun hash() : HashedData {
            TODO()
        }
    }
    object Symmetric {
        fun encrypt(data : Encryptable) : EncryptedData {
            TODO()
        }

        fun <T: Encryptable> decrypt(encryptedData : EncryptedData) : T {
            TODO()
        }
    }
}