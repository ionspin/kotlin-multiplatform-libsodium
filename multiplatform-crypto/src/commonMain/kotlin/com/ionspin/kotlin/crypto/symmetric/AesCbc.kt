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

import com.ionspin.kotlin.crypto.SRNG
import com.ionspin.kotlin.crypto.chunked
import com.ionspin.kotlin.crypto.xor

/**
 * Advanced encryption standard with cipher block chaining and PKCS #5
 *
 * For bulk encryption/decryption use [AesCbc.encrypt] and [AesCbc.decrypt]
 *
 * To get an instance of AesCbc and then feed it data sequentially with [addData] use [createEncryptor] and [createDecryptor]
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Sep-2019
 */
@ExperimentalUnsignedTypes
class AesCbc internal constructor(val aesKey: AesKey, val mode: Mode, initializationVector: Array<UByte>? = null) {

    companion object {
        const val BLOCK_BYTES = 16
        /**
         * Creates and returns AesCbc instance that can be fed data using [addData]. Once you have submitted all
         * data call [encrypt]
         */
        fun createEncryptor(aesKey: AesKey) : AesCbc {
            return AesCbc(aesKey, Mode.ENCRYPT)
        }
        /**
         * Creates and returns AesCbc instance that can be fed data using [addData]. Once you have submitted all
         * data call [decrypt]
         */
        fun createDecryptor(aesKey : AesKey) : AesCbc {
            return AesCbc(aesKey, Mode.DECRYPT)
        }

        /**
         * Bulk encryption, returns encrypted data and a random initialization vector
         */
        fun encrypt(aesKey: AesKey, data: Array<UByte>): EncryptedDataAndInitializationVector {
            val aesCbc = AesCbc(aesKey, Mode.ENCRYPT)
            aesCbc.addData(data)
            return aesCbc.encrypt()
        }

        /**
         * Bulk decryption, returns decrypted data
         */
        fun decrypt(aesKey: AesKey, data: Array<UByte>, initialCounter: Array<UByte>? = null): Array<UByte> {
            val aesCbc = AesCbc(aesKey, Mode.DECRYPT, initialCounter)
            aesCbc.addData(data)
            return aesCbc.decrypt()
        }

        private fun padToBlock(unpadded: Array<UByte>): Array<UByte> {
            val paddingSize = 16 - unpadded.size
            if (unpadded.size == BLOCK_BYTES) {
                return unpadded
            }

            if (unpadded.size == BLOCK_BYTES) {
                return Array(BLOCK_BYTES) {
                    BLOCK_BYTES.toUByte()
                }
            }

            if (unpadded.size > BLOCK_BYTES) {
                throw IllegalStateException("Block larger than 128 bytes")
            }

            return Array(BLOCK_BYTES) {
                when (it) {
                    in unpadded.indices -> unpadded[it]
                    else -> paddingSize.toUByte()
                }
            }

        }
    }

    var currentOutput: Array<UByte> = arrayOf()
    var previousEncrypted: Array<UByte> = arrayOf()
    val initVector = if (initializationVector.isNullOrEmpty()) {
        SRNG.getRandomBytes(16)
    } else {
        initializationVector
    }

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
                        output += consumeBlock(buffer)
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
     * Encrypt fed data and return it alongside the randomly chosen initialization vector
     * @return Encrypted data and initialization vector
     */
    fun encrypt(): EncryptedDataAndInitializationVector {
        if (bufferCounter > 0) {
            val lastBlockPadded = padToBlock(buffer)
            if (lastBlockPadded.size > BLOCK_BYTES) {
                val chunks = lastBlockPadded.chunked(BLOCK_BYTES)
                output += consumeBlock(chunks[0])
                output += consumeBlock(chunks[1])
            } else {
                output += consumeBlock(lastBlockPadded)
            }
        }
        return EncryptedDataAndInitializationVector(
            output.reversed().foldRight(Array<UByte>(0) { 0U }) { arrayOfUBytes, acc -> acc + arrayOfUBytes },
            initVector
        )
    }

    /**
     * Decrypt data
     * @return Decrypted data
     */
    fun decrypt(): Array<UByte> {
        val removePaddingCount = output.last().last()


        val removedPadding = if (removePaddingCount > 0U && removePaddingCount < 16U) {
            output.last().dropLast(removePaddingCount.toInt() and 0x7F)
        } else {
            output.last().toList()
        }
        val preparedOutput = output.dropLast(1).toTypedArray() + removedPadding.toTypedArray()
        //JS compiler freaks out here if we don't supply exact type
        val reversed : List<Array<UByte>> = preparedOutput.reversed() as List<Array<UByte>>
        val folded : Array<UByte> = reversed.foldRight(Array<UByte>(0) { 0U }) { arrayOfUBytes, acc ->
            acc + arrayOfUBytes }
        return folded

    }

    private fun appendToBuffer(array: Array<UByte>, start: Int) {
        array.copyInto(destination = buffer, destinationOffset = start, startIndex = 0, endIndex = array.size)
        bufferCounter += array.size
    }

    private fun consumeBlock(data: Array<UByte>): Array<UByte> {
        return when (mode) {
            Mode.ENCRYPT -> {
                currentOutput = if (currentOutput.isEmpty()) {
                    println("IV: $initVector")
                    Aes.encrypt(aesKey, data xor initVector)
                } else {
                    Aes.encrypt(aesKey, data xor currentOutput)
                }
                currentOutput
            }
            Mode.DECRYPT -> {
                if (currentOutput.isEmpty()) {
                    currentOutput = Aes.decrypt(aesKey, data) xor initVector
                } else {
                    currentOutput = Aes.decrypt(aesKey, data) xor previousEncrypted
                }
                previousEncrypted = data
                currentOutput
            }
        }

    }

}

@ExperimentalUnsignedTypes
data class EncryptedDataAndInitializationVector(val encryptedData : Array<UByte>, val initilizationVector : Array<UByte>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as EncryptedDataAndInitializationVector

        if (!encryptedData.contentEquals(other.encryptedData)) return false
        if (!initilizationVector.contentEquals(other.initilizationVector)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = encryptedData.contentHashCode()
        result = 31 * result + initilizationVector.contentHashCode()
        return result
    }
}