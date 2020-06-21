package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.crypto.mac.Poly1305
import com.ionspin.kotlin.crypto.symmetric.ChaCha20Pure
import com.ionspin.kotlin.crypto.symmetric.XChaCha20Pure
import com.ionspin.kotlin.crypto.util.hexColumsPrint
import com.ionspin.kotlin.crypto.util.toLittleEndianUByteArray

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jun-2020
 */
internal class XChaCha20Poly1305Pure(val key: UByteArray, val nonce: UByteArray, val additionalData: UByteArray) {
    companion object {

        fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray) : UByteArray {
            val oneTimeKey = XChaCha20Pure.hChacha(key, nonce)
            val authKey =
                ChaCha20Pure.encrypt(
                    oneTimeKey.toLittleEndianUByteArray(),
                    ubyteArrayOf(0U, 0U, 0U, 0U) + nonce.sliceArray(16 until 24),
                    UByteArray(64) { 0U },
                    0U // If this is moved as a default parameter in encrypt, and not here (in 1.4-M2)
                                    // js compiler dies with: e: java.lang.NullPointerException
                    //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer$visitConst$1$3.invoke(ConstLowering.kt:28)
                    //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer.lowerConst(ConstLowering.kt:38)
                )
            println("Poly sub-key:")
            oneTimeKey.hexColumsPrint()
            println("Poly key:")
            authKey.hexColumsPrint()
            val cipherText = XChaCha20Pure.encrypt(key, nonce, message, 1U)
            val additionalDataPad = UByteArray(16 - additionalData.size % 16) { 0U }
            val cipherTextPad = UByteArray(16 - cipherText.size % 16) { 0U }
            val macData = additionalData + additionalDataPad +
                    cipherText + cipherTextPad +
                    additionalData.size.toULong().toLittleEndianUByteArray() +
                    cipherText.size.toULong().toLittleEndianUByteArray()
            oneTimeKey.toLittleEndianUByteArray().hexColumsPrint()
            val tag = Poly1305.poly1305Authenticate(authKey, macData)
            return cipherText + tag
        }
    }

//    val encryption = XChaCha20Pure(key, nonce, initialCounter = 0U) {
//
//    }
//
//
//    fun encryptPartialData(data: UByteArray) : UByteArray {
//
//    }
//
//    fun finish() : UByteArray {
//
//    }



}