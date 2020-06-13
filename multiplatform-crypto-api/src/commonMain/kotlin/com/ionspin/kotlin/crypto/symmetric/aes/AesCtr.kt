package com.ionspin.kotlin.crypto.symmetric.aes

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 13-Jun-2020
 */
interface SimpleUpdateableAesCtr {
    fun update(data: UByteArray)

    fun process() : UByteArray

}

interface AdvancedUpdateableAesCtr : SimpleUpdateableAesCtr {
    fun update(data: UByteArray, counter : UByteArray) : UByteArray
}

interface SimpleStatelessAesCtr {

    fun encrypt(aesKey: AesKey, data: UByteArray) : EncryptedDataAndInitialCounter

    fun decrypt(aesKey: AesKey, encryptedDataAndInitialCounter: EncryptedDataAndInitialCounter) : UByteArray

}

interface AdvancedStatelessAesCtr : SimpleStatelessAesCtr {
    fun encrypt(aesKey: AesKey, data: UByteArray, initialCounter: UByteArray) : EncryptedDataAndInitialCounter
}

data class EncryptedDataAndInitialCounter(val encryptedData : UByteArray, val initialCounter : UByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EncryptedDataAndInitialCounter

        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (!initialCounter.contentEquals(other.initialCounter)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedData.contentHashCode()
        result = 31 * result + initialCounter.contentHashCode()
        return result
    }
}

data class EncryptedDataAndInitializationVector(val encryptedData : UByteArray, val initializationVector : UByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EncryptedDataAndInitializationVector

        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (!initializationVector.contentEquals(other.initializationVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedData.contentHashCode()
        result = 31 * result + initializationVector.contentHashCode()
        return result
    }
}
