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
class XChaCha20Poly1305Pure(val key: UByteArray, val nonce: UByteArray, val additionalData: UByteArray) {
    companion object {

        fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray) : UByteArray {
            val subKey = XChaCha20Pure.hChacha(key, nonce)
            val authKey =
                ChaCha20Pure.encrypt(
                    subKey.toLittleEndianUByteArray(),
                    ubyteArrayOf(0U, 0U, 0U, 0U) + nonce.sliceArray(16 until 24),
                    UByteArray(64) { 0U },
                    0U // If this is moved as a default parameter in encrypt, and not here (in 1.4-M2)
                                    // js compiler dies with: e: java.lang.NullPointerException
                    //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer$visitConst$1$3.invoke(ConstLowering.kt:28)
                    //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer.lowerConst(ConstLowering.kt:38)
                )
            val cipherText = XChaCha20Pure.encrypt(key, nonce, message, 1U)
            val additionalDataPad = UByteArray(16 - additionalData.size % 16) { 0U }
            val cipherTextPad = UByteArray(16 - cipherText.size % 16) { 0U }
            val macData = additionalData + additionalDataPad +
                    cipherText + cipherTextPad +
                    additionalData.size.toULong().toLittleEndianUByteArray() +
                    cipherText.size.toULong().toLittleEndianUByteArray()
            val tag = Poly1305.poly1305Authenticate(authKey, macData)
            return cipherText + tag
        }
    }

    private val updateableEncryptionPrimitive = XChaCha20Pure(key, nonce, initialCounter = 1U)
    private val updateableMacPrimitive : Poly1305

    private val polyBuffer = UByteArray(16)
    private var polyBufferByteCounter = 0

    private var processedBytes = 0

    init {
        val subKey = XChaCha20Pure.hChacha(key, nonce)
        val authKey =
            ChaCha20Pure.encrypt(
                subKey.toLittleEndianUByteArray(),
                ubyteArrayOf(0U, 0U, 0U, 0U) + nonce.sliceArray(16 until 24),
                UByteArray(64) { 0U },
                0U // If this is moved as a default parameter in encrypt, and not here (in 1.4-M2)
                // js compiler dies with: e: java.lang.NullPointerException
                //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer$visitConst$1$3.invoke(ConstLowering.kt:28)
                //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer.lowerConst(ConstLowering.kt:38)
            )
        updateableMacPrimitive = Poly1305(authKey)
        val additionalDataPad = UByteArray(16 - additionalData.size % 16) { 0U }
        processPolyBytes(additionalData + additionalDataPad)

    }


    fun encryptPartialData(data: UByteArray) : UByteArray {
        processedBytes += data.size
        val encrypted = updateableEncryptionPrimitive.encryptPartialData(data)
        processPolyBytes(encrypted)
        return encrypted
    }

    private fun processPolyBytes(data: UByteArray) {
        if (polyBufferByteCounter == 0) {
            val polyBlocks = data.size / 16
            val polyRemainder = data.size % 16
            for (i in 0 until polyBlocks) {
                updateableMacPrimitive.updateMac(data.sliceArray(i * 16 until i * 16 + 16))
            }
            if (polyRemainder != 0) {
                for (i in 0 until polyRemainder) {
                    polyBuffer[i] = data[data.size - polyRemainder + i]
                }
                polyBufferByteCounter = polyRemainder
            }
        } else {
            if (polyBufferByteCounter + data.size < 16) {
                for (i in 0 until data.size) {
                    polyBuffer[polyBufferByteCounter + i] = data[i]
                }
                polyBufferByteCounter += data.size
            } else {
                val borrowed = 16 - polyBufferByteCounter
                for (i in polyBufferByteCounter until 16) {
                    polyBuffer[i] = data[i - polyBufferByteCounter]
                }
                updateableMacPrimitive.updateMac(polyBuffer)
                polyBufferByteCounter = 0
                val polyBlocks = (data.size - borrowed) / 16
                val polyRemainder = (data.size - borrowed) % 16
                for (i in 0 until polyBlocks) {
                    updateableMacPrimitive.updateMac(data.sliceArray(borrowed + i * 16 until borrowed + i * 16 + 16))
                }
                if (polyRemainder != 0) {
                    for (i in 0 until polyRemainder) {
                        polyBuffer[i] = data[data.size + borrowed - polyRemainder + i]
                    }
                    polyBufferByteCounter = polyRemainder
                }
            }
        }
    }






    fun finish() : UByteArray {

        val cipherTextPad = UByteArray(16 - processedBytes % 16) { 0U }
        val macData = cipherTextPad +
                additionalData.size.toULong().toLittleEndianUByteArray() +
                processedBytes.toULong().toLittleEndianUByteArray()
        processPolyBytes(macData)
        val tag = updateableMacPrimitive.finalizeMac()
        return tag
    }



}