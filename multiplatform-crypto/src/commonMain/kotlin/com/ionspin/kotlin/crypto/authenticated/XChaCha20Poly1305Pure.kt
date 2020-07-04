package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.crypto.AuthenticatedEncryption
import com.ionspin.kotlin.crypto.InvalidTagException
import com.ionspin.kotlin.crypto.mac.Poly1305
import com.ionspin.kotlin.crypto.symmetric.ChaCha20Pure
import com.ionspin.kotlin.crypto.symmetric.XChaCha20Pure
import com.ionspin.kotlin.crypto.util.hexColumsPrint
import com.ionspin.kotlin.crypto.util.overwriteWithZeroes
import com.ionspin.kotlin.crypto.util.toLittleEndianUByteArray

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jun-2020
 */
class XChaCha20Poly1305Pure(val key: UByteArray, val nonce: UByteArray) {
    companion object : AuthenticatedEncryption {

        override fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray) : UByteArray {
            val subKey = XChaCha20Pure.hChacha(key, nonce)
            val authKey =
                ChaCha20Pure.xorWithKeystream(
                    subKey.toLittleEndianUByteArray(),
                    ubyteArrayOf(0U, 0U, 0U, 0U) + nonce.sliceArray(16 until 24),
                    UByteArray(64) { 0U },
                    0U // If this is moved as a default parameter in encrypt, and not here (in 1.4-M2)
                                    // js compiler dies with: e: java.lang.NullPointerException
                    //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer$visitConst$1$3.invoke(ConstLowering.kt:28)
                    //	at org.jetbrains.kotlin.ir.backend.js.lower.ConstTransformer.lowerConst(ConstLowering.kt:38)
                )
            val cipherText = XChaCha20Pure.xorWithKeystream(key, nonce, message, 1U)
            val additionalDataPad = UByteArray(16 - additionalData.size % 16) { 0U }
            val cipherTextPad = UByteArray(16 - cipherText.size % 16) { 0U }
            val macData = additionalData + additionalDataPad +
                    cipherText + cipherTextPad +
                    additionalData.size.toULong().toLittleEndianUByteArray() +
                    cipherText.size.toULong().toLittleEndianUByteArray()
            val tag = Poly1305.poly1305Authenticate(authKey, macData)
            return cipherText + tag
        }

        override fun decrypt(key: UByteArray, nonce: UByteArray, cipherText: UByteArray, additionalData: UByteArray) : UByteArray {
            val subKey = XChaCha20Pure.hChacha(key, nonce)
            val authKey =
                ChaCha20Pure.xorWithKeystream(
                    subKey.toLittleEndianUByteArray(),
                    ubyteArrayOf(0U, 0U, 0U, 0U) + nonce.sliceArray(16 until 24),
                    UByteArray(64) { 0U },
                    0U
                )
            //2. Get the tag
            val tag = cipherText.sliceArray(cipherText.size - 16 until cipherText.size)
            //3. Verify tag is valid
            val cipherTextWithoutTag = cipherText.sliceArray(0 until cipherText.size - 16)
            val additionalDataPad = UByteArray(16 - additionalData.size % 16) { 0U }
            val cipherTextPad = UByteArray(16 - cipherTextWithoutTag.size % 16) { 0U }
            val macData = additionalData + additionalDataPad +
                    cipherTextWithoutTag + cipherTextPad +
                    additionalData.size.toULong().toLittleEndianUByteArray() +
                    cipherTextWithoutTag.size.toULong().toLittleEndianUByteArray()
            val calculatedTag = Poly1305.poly1305Authenticate(authKey, macData)
            if (!calculatedTag.contentEquals(tag)) {
                throw InvalidTagException()
            }
            //4. Decrypt data
            return XChaCha20Pure.xorWithKeystream(key, nonce, cipherTextWithoutTag, 1U)
        }

    }


    private val polyBuffer = UByteArray(16)
    private var polyBufferByteCounter = 0

    private var processedBytes = 0

    internal val calcKey : UByteArray = UByteArray(32)
    internal val calcNonce : UByteArray = UByteArray(12)

    init {
        val calc = XChaCha20Pure.hChacha(key, nonce).toLittleEndianUByteArray()
        calc.sliceArray(0 until 32).copyInto(calcKey)
        nonce.sliceArray(16 until 24).copyInto(calcNonce, 4)
        calcNonce[0] = 1U
        calcNonce[1] = 0U
        calcNonce[2] = 0U
        calcNonce[3] = 0U
        println("Calckey-------=")
        calcKey.hexColumsPrint()
        println("Calckey-------=")
        println("Calcnonce---------")
        calcNonce.hexColumsPrint()
        println("Calcnonce---------")
    }

    fun streamEncrypt(data: UByteArray, additionalData: UByteArray, tag : UByte) : UByteArray {
        val result = UByteArray(1 + data.size + 16) //Tag marker, ciphertext, mac
        //get encryption state
        val block = UByteArray(64) { 0U }
        ChaCha20Pure.xorWithKeystream(calcKey, calcNonce, block, 0U).copyInto(block) // This is equivalent to the first 64 bytes of keystream
        val poly1305 = Poly1305(block)
        block.overwriteWithZeroes()
        if (additionalData.isNotEmpty()) {
            val additionalDataPadded = additionalData + UByteArray(16 - additionalData.size % 16) { 0U }
            processPolyBytes(poly1305, additionalDataPadded)
        }
        block[0] = tag
        ChaCha20Pure.xorWithKeystream(calcKey, calcNonce, block, 1U).copyInto(block) // This just xors block[0] with keystream
        processPolyBytes(poly1305, block) // but updates the mac with the full block!
        // In libsodium c code, it now sets the first byte to be a tag, we'll just save it for now
        val encryptedTag = block[0]
        //And then encrypt the rest of the message
        val ciphertext = ChaCha20Pure.xorWithKeystream(calcKey, calcNonce, data, 2U) // With appropriate counter
        // Next we update the poly1305 with ciphertext and padding, BUT the padding in libsodium is not correctly calculated, so it doesn't
        // pad correctly. https://github.com/jedisct1/libsodium/issues/976
        // We want to use libsodium in delegated flavour, so we will use the same incorrect padding here.
        // From security standpoint there are no obvious drawbacks, as padding was initially added to decrease implementation complexity.
        processPolyBytes(poly1305, ciphertext + UByteArray(((16U + data.size.toUInt() - block.size.toUInt()) % 16U).toInt()) { 0U } ) //TODO this is inefficient as it creates a new array and copies data
        // Last 16byte block containing actual additional data nad ciphertext sizes
        val finalMac = additionalData.size.toULong().toLittleEndianUByteArray() + (ciphertext.size + 64).toULong().toLittleEndianUByteArray()
        processPolyBytes(poly1305, finalMac)
        val mac = poly1305.finalizeMac(polyBuffer.sliceArray(0 until polyBufferByteCounter))
        return ubyteArrayOf(encryptedTag) + ciphertext + mac
    }

    fun streamDecrypt(data: UByteArray, additionalData: UByteArray, tag: UByte) : UByteArray {
        TODO()
    }



    private fun processPolyBytes(updateableMacPrimitive: Poly1305, data: UByteArray) {
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
                        polyBuffer[i] = data[borrowed + i]
                    }
                    polyBufferByteCounter = polyRemainder
                }
            }
        }
    }


}
