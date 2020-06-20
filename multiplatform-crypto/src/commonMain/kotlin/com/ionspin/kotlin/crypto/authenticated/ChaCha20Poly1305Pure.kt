package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.crypto.mac.Poly1305
import com.ionspin.kotlin.crypto.symmetric.ChaCha20Pure
import com.ionspin.kotlin.crypto.symmetric.XChaCha20Pure
import com.ionspin.kotlin.crypto.util.fromLittleEndianArrayToUIntWithPosition
import com.ionspin.kotlin.crypto.util.hexColumsPrint
import com.ionspin.kotlin.crypto.util.toLittleEndianUByteArray

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jun-2020
 */
class ChaCha20Poly1305Pure {
    companion object {

        fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray) : UByteArray {
            val state = UIntArray(16) {
                when (it) {
                    0 -> ChaCha20Pure.sigma0_32
                    1 -> ChaCha20Pure.sigma1_32
                    2 -> ChaCha20Pure.sigma2_32
                    3 -> ChaCha20Pure.sigma3_32
                    4 -> key.fromLittleEndianArrayToUIntWithPosition(0)
                    5 -> key.fromLittleEndianArrayToUIntWithPosition(4)
                    6 -> key.fromLittleEndianArrayToUIntWithPosition(8)
                    7 -> key.fromLittleEndianArrayToUIntWithPosition(12)
                    8 -> key.fromLittleEndianArrayToUIntWithPosition(16)
                    9 -> key.fromLittleEndianArrayToUIntWithPosition(20)
                    10 -> key.fromLittleEndianArrayToUIntWithPosition(24)
                    11 -> key.fromLittleEndianArrayToUIntWithPosition(28)
                    12 -> 0U
                    13 -> nonce.fromLittleEndianArrayToUIntWithPosition(0)
                    14 -> nonce.fromLittleEndianArrayToUIntWithPosition(4)
                    15 -> nonce.fromLittleEndianArrayToUIntWithPosition(8)
                    else -> 0U
                }
            }
            val oneTimeKey = ChaCha20Pure.hash(state).sliceArray(0 until 32)
            println("Poly key:")
            oneTimeKey.hexColumsPrint()
            val cipherText = ChaCha20Pure.encrypt(key, nonce, message, 1U)
            val additionalDataPad = UByteArray(16 - additionalData.size % 16) { 0U }
            val cipherTextPad = UByteArray(16 - cipherText.size % 16) { 0U }
            val macData = additionalData + additionalDataPad +
                    cipherText + cipherTextPad +
                    additionalData.size.toULong().toLittleEndianUByteArray() +
                    cipherText.size.toULong().toLittleEndianUByteArray()
            println("Mac data")
            macData.hexColumsPrint()
            val tag = Poly1305.poly1305Authenticate(oneTimeKey, macData)
            println("Tag:")
            tag.hexColumsPrint()
            return  cipherText + tag
        }
    }
}