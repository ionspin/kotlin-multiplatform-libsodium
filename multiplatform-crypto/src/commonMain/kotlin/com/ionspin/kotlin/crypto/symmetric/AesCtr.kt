/*
 *    Copyright 2019 Ugljesa Jovanovic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.bignum.Endianness
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.modular.ModularBigInteger
import com.ionspin.kotlin.crypto.SRNG
import com.ionspin.kotlin.crypto.chunked
import com.ionspin.kotlin.crypto.symmetric.AesCtr.Companion.encrypt
import com.ionspin.kotlin.crypto.xor

/**
 *
 *  Advanced encryption standard with counter mode
 *
 * For bulk encryption/decryption use [AesCtr.encrypt] and [AesCtr.decrypt]
 *
 * To get an instance of AesCtr and then feed it data sequentially with [addData] use [createEncryptor] and [createDecryptor]
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Sep-2019
 */
@ExperimentalUnsignedTypes
class AesCtr internal constructor(val aesKey: AesKey, val mode: Mode, initialCounter: Array<UByte>? = null) {

    companion object {
        const val BLOCK_BYTES = 16

        val modularCreator = ModularBigInteger.creatorForModulo(BigInteger.ONE.shl(128) - 1)
        /**
         * Creates and returns AesCtr instance that can be fed data using [addData]. Once you have submitted all
         * data call [encrypt]
         */
        fun createEncryptor(aesKey: AesKey) : AesCtr {
            return AesCtr(aesKey, Mode.ENCRYPT)
        }
        /**
         * Creates and returns AesCtr instance that can be fed data using [addData]. Once you have submitted all
         * data call [decrypt]
         */
        fun createDecryptor(aesKey : AesKey) : AesCtr {
            return AesCtr(aesKey, Mode.DECRYPT)
        }
        /**
         * Bulk encryption, returns encrypted data and a random initial counter 
         */
        fun encrypt(aesKey: AesKey, data: Array<UByte>): EncryptedDataAndInitialCounter {
            val aesCtr = AesCtr(aesKey, Mode.ENCRYPT)
            aesCtr.addData(data)
            return aesCtr.encrypt()
        }
        /**
         * Bulk decryption, returns decrypted data
         */
        fun decrypt(aesKey: AesKey, data: Array<UByte>, initialCounter: Array<UByte>? = null): Array<UByte> {
            val aesCtr = AesCtr(aesKey, Mode.DECRYPT, initialCounter)
            aesCtr.addData(data)
            return aesCtr.decrypt()
        }

    }

    var currentOutput: Array<UByte> = arrayOf()
    var previousEncrypted: Array<UByte> = arrayOf()
    val counterStart = initialCounter ?: SRNG.getRandomBytes(16)
    var blockCounter = modularCreator.fromBigInteger(BigInteger.fromUByteArray(counterStart, Endianness.BIG))

    val output = MutableList<Array<UByte>>(0) { arrayOf() }

    var buffer: Array<UByte> = UByteArray(16) { 0U }.toTypedArray()
    var bufferCounter = 0

    fun addData(data: Array<UByte>) {
        //Padding
        when {
            bufferCounter + data.size < BLOCK_BYTES -> appendToBuffer(data, bufferCounter)
            bufferCounter + data.size >= BLOCK_BYTES -> {
                val chunked = data.chunked(BLOCK_BYTES)
                chunked.forEach { chunk ->
                    if (bufferCounter + chunk.size < BLOCK_BYTES) {
                        appendToBuffer(chunk, bufferCounter)
                    } else {
                        chunk.copyInto(
                            destination = buffer,
                            destinationOffset = bufferCounter,
                            startIndex = 0,
                            endIndex = BLOCK_BYTES - bufferCounter
                        )
                        output += consumeBlock(buffer, blockCounter)
                        blockCounter += 1
                        buffer = Array<UByte>(BLOCK_BYTES) {
                            when (it) {
                                in (0 until (chunk.size - (BLOCK_BYTES - bufferCounter))) -> {
                                    chunk[it + (BLOCK_BYTES - bufferCounter)]
                                }
                                else -> {
                                    0U
                                }
                            }

                        }
                        bufferCounter = chunk.size - (BLOCK_BYTES - bufferCounter)
                    }
                }

            }
        }

    }
    /**
     * Encrypt fed data and return it alongside the randomly chosen initial counter state
     * @return Encrypted data and initial counter state
     */
    fun encrypt(): EncryptedDataAndInitialCounter {
        if (bufferCounter > 0) {
            output += consumeBlock(buffer, blockCounter)
        }
        return EncryptedDataAndInitialCounter(
            output.reversed().foldRight(Array<UByte>(0) { 0U }) { arrayOfUBytes, acc -> acc + arrayOfUBytes },
            counterStart
        )
    }
    /**
     * Decrypt data
     * @return Decrypted data
     */
    fun decrypt(): Array<UByte> {
        if (bufferCounter > 0) {
            output += consumeBlock(buffer, blockCounter)
        }
        //JS compiler freaks out here if we don't supply exact type
        val reversed: List<Array<UByte>> = output.reversed() as List<Array<UByte>>
        val folded: Array<UByte> = reversed.foldRight(Array<UByte>(0) { 0U }) { arrayOfUBytes, acc ->
            acc + arrayOfUBytes
        }
        return folded
    }

    private fun appendToBuffer(array: Array<UByte>, start: Int) {
        array.copyInto(destination = buffer, destinationOffset = start, startIndex = 0, endIndex = array.size)
        bufferCounter += array.size
    }

    private fun consumeBlock(data: Array<UByte>, blockCount: ModularBigInteger): Array<UByte> {
        return when (mode) {
            Mode.ENCRYPT -> {
                Aes.encrypt(aesKey, blockCount.toUByteArray(Endianness.BIG)) xor data
            }
            Mode.DECRYPT -> {
                Aes.encrypt(aesKey, blockCount.toUByteArray(Endianness.BIG)) xor data
            }
        }

    }

}

@ExperimentalUnsignedTypes
data class EncryptedDataAndInitialCounter(val encryptedData : Array<UByte>, val initialCounter : Array<UByte>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EncryptedDataAndInitializationVector

        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (!initialCounter.contentEquals(other.initilizationVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedData.contentHashCode()
        result = 31 * result + initialCounter.contentHashCode()
        return result
    }
}