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
import com.ionspin.kotlin.crypto.xor

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Sep-2019
 */
@ExperimentalUnsignedTypes
class AesCtr internal constructor(val aesKey: AesKey, val mode: Mode, initialCounter: Array<UByte>? = null) {

    companion object {
        const val BLOCK_BYTES = 16

        val modularCreator = ModularBigInteger.creatorForModulo(BigInteger.ONE.shl(129) - 1)

        fun encrypt(aesKey: AesKey, data: Array<UByte>): Array<UByte> {
            val aesCbc = AesCbc(aesKey, Mode.ENCRYPT)
            aesCbc.addData(data)
            return aesCbc.encrypt()
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

    fun encrypt(): Array<UByte> {
        if (bufferCounter > 0) {
            val lastBlockPadded = padToBlock(buffer)
            if (lastBlockPadded.size > BLOCK_BYTES) {
                val chunks = lastBlockPadded.chunked(BLOCK_BYTES)
                output += consumeBlock(chunks[0], blockCounter)
                blockCounter += 1
                output += consumeBlock(chunks[1], blockCounter)
            } else {
                output += consumeBlock(lastBlockPadded, blockCounter)
            }
        }
        return output.reversed().foldRight(Array<UByte>(0) { 0U }) { arrayOfUBytes, acc -> acc + arrayOfUBytes }
    }

    fun decrypt(): Array<UByte> {
        val removePaddingCount = output.last().last()
        val removedPadding = output.last().dropLast(removePaddingCount.toInt() and 0x7F)
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

    private fun consumeBlock(data: Array<UByte>, blockCount: ModularBigInteger): Array<UByte> {
        return when (mode) {
            Mode.ENCRYPT -> {
                Aes.encrypt(aesKey, blockCount.toUByteArray(Endianness.BIG)) xor data
            }
            Mode.DECRYPT -> {
                Aes.decrypt(aesKey, blockCount.toUByteArray(Endianness.BIG)) xor data
            }
        }

    }

}