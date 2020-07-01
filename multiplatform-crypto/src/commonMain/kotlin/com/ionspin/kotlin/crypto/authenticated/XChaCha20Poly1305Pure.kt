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

    private val updateableEncryptionPrimitive = XChaCha20Pure(key, nonce, initialCounter = 1U)
    private val updateableMacPrimitive : Poly1305

    private val polyBuffer = UByteArray(16)
    private var polyBufferByteCounter = 0

    private var processedBytes = 0

    init {
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
        updateableMacPrimitive = Poly1305(authKey)
//        val additionalDataPad = UByteArray(16 - additionalData.size % 16) { 0U }
//        processPolyBytes(additionalData + additionalDataPad)

    }

    // Sketch libsodium stream cipher with chachapoly, because my two pass approach to multipart encryption is
    // inneficient, to put it mildly.

    // libsodium-like state
    // key, 32 Bytes
    // nonce, 12 Bytes
    // pad, 8 bytes

    //libsodium like header
    //random header bytes 24 and put that into out
    //then hchacha20 of key and random bytes (out) to generate state key
    //the reset counter to 1
    //then copy to state->NONCE, HCHACHAINPUTBYTES (16B) from out, length of INONCE_BYTES which is 8, which uses up all random from previous step
    //Pad state with 8B of zeroes

    //header is a 24byte nonce

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

    fun encryptPartial(data: UByteArray, additionalData: UByteArray = ubyteArrayOf(), tag : UByte = 0U) : UByteArray {
        val result = UByteArray(1 + data.size + 16) //Tag marker, ciphertext, mac
        //get encryption state
        val block = UByteArray(64) { 0U }
        println("--block")
        block.hexColumsPrint()
        println("--block")
        println("--key")
        calcKey.hexColumsPrint()
        println("--key")
        println("--nonce")
        calcNonce.hexColumsPrint()
        println("--nonce")
        ChaCha20Pure.xorWithKeystream(calcKey, calcNonce, block, 0U).copyInto(block) // This is equivalent to the first 64 bytes of keystream
        /*
        5C 9A C3 7B CA 8F 2B 12 9A D8 41 3B 0C E9 55 EF
        25 27 9A 4B 5B 7F 87 75 0C 47 E9 C9 DE 82 44 BA
        6C 51 48 F4 9C 0A 24 6B F2 7C 51 5E 62 1A 16 E1
        28 23 C6 B5 12 2E AD 58 AD 51 AA 34 78 33 08 C9
         */
        println("--block")
        block.hexColumsPrint()
        println("--block")
        val poly1305 = Poly1305(block)
        block.overwriteWithZeroes()
        if (additionalData.isNotEmpty()) {
            val additionalDataPadded = additionalData + UByteArray(16 - additionalData.size % 16) { 0U }
            processPolyBytes(poly1305, additionalDataPadded)
        }

        block[0] = tag
        ChaCha20Pure.xorWithKeystream(calcKey, calcNonce, block, 1U).copyInto(block) // This just xors block[0] with keystream
        processPolyBytes(poly1305, block) // but updates the mac with the full block!
//        println("--poly 1")
        // 13 10 8E D1 3C B9 77 C1 9B 95 66 C8 1B 8A 5D D3
//        poly1305.finalizeMac().hexColumsPrint()
//        println("--poly 1")
        // In libsodium c code, it now sets the first byte to be a tag, we'll just save it for now
        val encryptedTag = block[0]
        //And then encrypt the rest of the message
        val ciphertext = ChaCha20Pure.xorWithKeystream(calcKey, calcNonce, data, 2U) // With appropriate counter
        println("ciphertext-----")
        /*
        paddedCipherText--
            D3 2D 59 B8 C4 66 2E 47 29 C6 F9 93 4B 09 27 24
            DD F3 05 48 94 67 10 00 21 85 22 96 3C CE 8E B7
            53 9D 46 F5 3C 5E 48 9B 8C 13 B7 28 6B B3 6C 3A
            04 B7 25 B9 50 45 08 0B 89 A2 0F 70 CC 60 1B C3
            17 35 9F AE 82 51 43 1B 9D 53 9E E2 AF 20 1F FD
            03 59 11 51 9E AC 83 CD 78 D1 D0 E5 D7 0E 41 DE
            FB 5C 7F 1C 00 00 00 00 00 00 00 00 00 00 00 00
            paddedCipherText--
         */
        (ciphertext + UByteArray(((16 - 64 + data.size) % 16)) { 0U }).hexColumsPrint()
        println("pad: ${16 - ((data.size) % 16)}")
        println("pad: ${((16U + data.size.toUInt() - block.size.toUInt()) % 16U).toInt()}")
        println("ciphertext-----")
        processPolyBytes(poly1305, ciphertext + UByteArray(((16U + data.size.toUInt() - block.size.toUInt()) % 16U).toInt()) { 0U } ) //TODO this is inefficient as it creates a new array and copies data
//        println("--poly cipher")
        // 93 D9 13 DC AB 1D 07 D7 51 03 17 85 8A 5C F0 84
//        poly1305.finalizeMac().hexColumsPrint()
//        println("--poly cipher")
        val finalMac = additionalData.size.toULong().toLittleEndianUByteArray() + (ciphertext.size + 64).toULong().toLittleEndianUByteArray()
        processPolyBytes(poly1305, finalMac)
        val mac = poly1305.finalizeMac(polyBuffer.sliceArray(0 until polyBufferByteCounter))
        //19 F3 39 CC DE 82 35 08 C1 82 DB 3D F1 EF 89 45
        println("poly final --")
        mac.hexColumsPrint()
        println("poly final --")
        return ubyteArrayOf(encryptedTag) + ciphertext + mac
    }

    // Sketch end

    fun encryptPartialData(data: UByteArray) : UByteArray {
        processedBytes += data.size
        val encrypted = updateableEncryptionPrimitive.xorWithKeystream(data)
//        processPolyBytes(encrypted)

        val cipherTextPad = UByteArray(16 - processedBytes % 16) { 0U }
        val macData = cipherTextPad +
//                additionalData.size.toULong().toLittleEndianUByteArray() +
                processedBytes.toULong().toLittleEndianUByteArray()
//        processPolyBytes(macData)
        val tag = updateableMacPrimitive.finalizeMac()

        return encrypted + tag
    }

    fun verifyPartialData(data: UByteArray) {
//        processPolyBytes(data)
    }

    fun checkTag(expectedTag: UByteArray) {
        val cipherTextPad = UByteArray(16 - processedBytes % 16) { 0U }
        val macData = cipherTextPad +
//                additionalData.size.toULong().toLittleEndianUByteArray() +
                processedBytes.toULong().toLittleEndianUByteArray()
//        processPolyBytes(macData)
        val tag = updateableMacPrimitive.finalizeMac()
        if (!tag.contentEquals(expectedTag)) {
            throw InvalidTagException()
        }
    }

    fun decrypt(data: UByteArray) : UByteArray {
        processedBytes += data.size
        val decrypted = updateableEncryptionPrimitive.xorWithKeystream(data)
//        processPolyBytes(decrypted)
        return decrypted
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

    fun finishEncryption() : Pair<UByteArray, UByteArray> {

        val cipherTextPad = UByteArray(16 - processedBytes % 16) { 0U }
        val macData = cipherTextPad +
//                additionalData.size.toULong().toLittleEndianUByteArray() +
                processedBytes.toULong().toLittleEndianUByteArray()
//        processPolyBytes(macData)
        val tag = updateableMacPrimitive.finalizeMac()
        return Pair(tag, nonce)
    }


}
