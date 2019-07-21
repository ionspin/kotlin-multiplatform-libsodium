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

package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.chunked
import com.ionspin.kotlin.crypto.hash.StatelessHash
import com.ionspin.kotlin.crypto.hash.UpdateableHash
import com.ionspin.kotlin.crypto.rotateRight


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


@ExperimentalUnsignedTypes
class Sha256 : UpdateableHash {

    override val MAX_HASH_BYTES: Int = 32


    companion object : StatelessHash {
        const val BLOCK_SIZE = 512
        const val BLOCK_SIZE_IN_BYTES = 64
        const val UINT_MASK = 0xFFFFFFFFU
        const val BYTE_MASK_FROM_ULONG = 0xFFUL
        const val BYTE_MASK_FROM_UINT = 0xFFU

        override val MAX_HASH_BYTES: Int = 32

        val iv = arrayOf(
            0x6a09e667U,
            0xbb67ae85U,
            0x3c6ef372U,
            0xa54ff53aU,
            0x510e527fU,
            0x9b05688cU,
            0x1f83d9abU,
            0x5be0cd19U
        )

        val k = arrayOf(
            0x428a2f98U, 0x71374491U, 0xb5c0fbcfU, 0xe9b5dba5U, 0x3956c25bU, 0x59f111f1U, 0x923f82a4U, 0xab1c5ed5U,
            0xd807aa98U, 0x12835b01U, 0x243185beU, 0x550c7dc3U, 0x72be5d74U, 0x80deb1feU, 0x9bdc06a7U, 0xc19bf174U,
            0xe49b69c1U, 0xefbe4786U, 0x0fc19dc6U, 0x240ca1ccU, 0x2de92c6fU, 0x4a7484aaU, 0x5cb0a9dcU, 0x76f988daU,
            0x983e5152U, 0xa831c66dU, 0xb00327c8U, 0xbf597fc7U, 0xc6e00bf3U, 0xd5a79147U, 0x06ca6351U, 0x14292967U,
            0x27b70a85U, 0x2e1b2138U, 0x4d2c6dfcU, 0x53380d13U, 0x650a7354U, 0x766a0abbU, 0x81c2c92eU, 0x92722c85U,
            0xa2bfe8a1U, 0xa81a664bU, 0xc24b8b70U, 0xc76c51a3U, 0xd192e819U, 0xd6990624U, 0xf40e3585U, 0x106aa070U,
            0x19a4c116U, 0x1e376c08U, 0x2748774cU, 0x34b0bcb5U, 0x391c0cb3U, 0x4ed8aa4aU, 0x5b9cca4fU, 0x682e6ff3U,
            0x748f82eeU, 0x78a5636fU, 0x84c87814U, 0x8cc70208U, 0x90befffaU, 0xa4506cebU, 0xbef9a3f7U, 0xc67178f2U
        )

        @ExperimentalStdlibApi
        override fun digest(inputString: String, key: String?, hashLength: Int): Array<UByte> {
            return digest(
                inputString.encodeToByteArray().map { it.toUByte() }.toTypedArray(),
                key?.run { encodeToByteArray().map { it.toUByte() }.toTypedArray() } ?: emptyArray<UByte>(),
                hashLength)
        }

        override fun digest(inputMessage: Array<UByte>, key: Array<UByte>, hashLength: Int): Array<UByte> {

            var h = iv.copyOf()

            val expansionArray = createExpansionArray(inputMessage.size)

            val chunks = (
                    inputMessage +
                            expansionArray +
                            (inputMessage.size * 8).toULong().toPaddedByteArray()
                    )
                .chunked(BLOCK_SIZE_IN_BYTES)

            chunks.forEach { chunk ->
                val w = expandChunk(chunk)
                mix(h, w).copyInto(h)

            }

            val digest = h[0].toPaddedByteArray() +
                    h[1].toPaddedByteArray() +
                    h[2].toPaddedByteArray() +
                    h[3].toPaddedByteArray() +
                    h[4].toPaddedByteArray() +
                    h[5].toPaddedByteArray() +
                    h[6].toPaddedByteArray() +
                    h[7].toPaddedByteArray()
            return digest
        }

        private fun scheduleSigma0(value: UInt): UInt {
            return value.rotateRight(7) xor value.rotateRight(18) xor (value shr 3)
        }

        private fun scheduleSigma1(value: UInt): UInt {
            return value.rotateRight(17) xor value.rotateRight(19) xor (value shr 10)
        }

        private fun compressionSigma0(a: UInt): UInt {
            return (a rotateRight 2) xor (a rotateRight 13) xor (a rotateRight 22)
        }

        private fun compressionSigma1(e: UInt): UInt {
            return (e rotateRight 6) xor (e rotateRight 11) xor (e rotateRight 25)
        }

        private fun ch(x: UInt, y: UInt, z: UInt): UInt {
            return ((x and y) xor ((x xor UINT_MASK) and z))
        }

        private fun maj(x: UInt, y: UInt, z: UInt): UInt {
            return (((x and y) xor (x and z) xor (y and z)))
        }

        private fun expandChunk(chunk: Array<UByte>): Array<UInt> {
            val w = Array<UInt>(BLOCK_SIZE_IN_BYTES) {
                when (it) {
                    in 0 until 16 -> {
                        var collected = (chunk[(it * 4)].toUInt() shl 24) +
                                (chunk[(it * 4) + 1].toUInt() shl 16) +
                                (chunk[(it * 4) + 2].toUInt() shl 8) +
                                (chunk[(it * 4) + 3].toUInt())
                        collected
                    }
                    else -> 0U
                }
            }
            for (i in 16 until BLOCK_SIZE_IN_BYTES) {
                val s0 = scheduleSigma0(w[i - 15])
                val s1 = scheduleSigma1(w[i - 2])
                w[i] = w[i - 16] + s0 + w[i - 7] + s1
            }
            return w
        }

        private fun mix(h: Array<UInt>, w: Array<UInt>): Array<UInt> {
            var paramA = h[0]
            var paramB = h[1]
            var paramC = h[2]
            var paramD = h[3]
            var paramE = h[4]
            var paramF = h[5]
            var paramG = h[6]
            var paramH = h[7]

            for (i in 0 until BLOCK_SIZE_IN_BYTES) {
                val s1 = compressionSigma1(paramE)
                val ch = ch(paramE, paramF, paramG)
                val temp1 = paramH + s1 + ch + k[i] + w[i]
                val s0 = compressionSigma0(paramA)
                val maj = maj(paramA, paramB, paramC)
                val temp2 = s0 + maj
                paramH = paramG
                paramG = paramF
                paramF = paramE
                paramE = paramD + temp1
                paramD = paramC
                paramC = paramB
                paramB = paramA
                paramA = temp1 + temp2
            }

            h[0] += paramA
            h[1] += paramB
            h[2] += paramC
            h[3] += paramD
            h[4] += paramE
            h[5] += paramF
            h[6] += paramG
            h[7] += paramH
            return h
        }


        fun createExpansionArray(originalSizeInBytes: Int): Array<UByte> {
            val originalMessageSizeInBits = originalSizeInBytes * 8


            //K such that L + 1 + K + 64 is a multiple of 512
            val expandedRemainderOf512 = (originalMessageSizeInBits + BLOCK_SIZE_IN_BYTES + 1) % BLOCK_SIZE
            val zeroAddAmount = when (expandedRemainderOf512) {
                0 -> 0
                else -> (BLOCK_SIZE - expandedRemainderOf512) / 8
            }
            val expansionArray = Array<UByte>(zeroAddAmount + 1) {
                when (it) {
                    0 -> 0b10000000U
                    else -> 0U
                }
            }
            return expansionArray
        }

        private fun ULong.toPaddedByteArray(): Array<UByte> {
            val byteMask = BYTE_MASK_FROM_ULONG
            return Array(8) {
                when (it) {
                    7 -> (this and byteMask).toUByte()
                    6 -> ((this shr 8) and byteMask).toUByte()
                    5 -> ((this shr 16) and byteMask).toUByte()
                    4 -> ((this shr 24) and byteMask).toUByte()
                    3 -> ((this shr 32) and byteMask).toUByte()
                    2 -> ((this shr 40) and byteMask).toUByte()
                    1 -> ((this shr 48) and byteMask).toUByte()
                    0 -> ((this shr 54) and byteMask).toUByte()
                    else -> throw RuntimeException("Invalid conversion")
                }
            }
        }

        private fun UInt.toPaddedByteArray(): Array<UByte> {
            val byteMask = BYTE_MASK_FROM_UINT
            return Array(4) {
                when (it) {
                    3 -> (this and byteMask).toUByte()
                    2 -> ((this shr 8) and byteMask).toUByte()
                    1 -> ((this shr 16) and byteMask).toUByte()
                    0 -> ((this shr 24) and byteMask).toUByte()
                    else -> throw RuntimeException("Invalid conversion")
                }
            }
        }

    }

    var h = iv.copyOf()
    var counter = 0
    var bufferCounter = 0
    var buffer = Array<UByte>(BLOCK_SIZE_IN_BYTES) { 0U }

    @ExperimentalStdlibApi
    override fun update(data: String) {
        return update(data.encodeToByteArray().map { it.toUByte() }.toTypedArray())
    }

    override fun update(data: Array<UByte>) {
        if (data.isEmpty()) {
            throw RuntimeException("Updating with empty array is not allowed. If you need empty hash, just call digest without updating")
        }

        when {
            bufferCounter + data.size < BLOCK_SIZE_IN_BYTES -> appendToBuffer(data, bufferCounter)
            bufferCounter + data.size >= BLOCK_SIZE_IN_BYTES -> {
                val chunked = data.chunked(BLOCK_SIZE_IN_BYTES)
                chunked.forEach { chunk ->
                    if (bufferCounter + chunk.size < BLOCK_SIZE_IN_BYTES) {
                        appendToBuffer(chunk, bufferCounter)
                    } else {
                        chunk.copyInto(
                            destination = buffer,
                            destinationOffset = bufferCounter,
                            startIndex = 0,
                            endIndex = BLOCK_SIZE_IN_BYTES - bufferCounter
                        )
                        counter += BLOCK_SIZE_IN_BYTES
                        consumeBlock(buffer)
                        buffer = Array<UByte>(BLOCK_SIZE_IN_BYTES) {
                            when (it) {
                                in (0 until (chunk.size - (BLOCK_SIZE_IN_BYTES - bufferCounter))) -> {
                                    chunk[it + (BLOCK_SIZE_IN_BYTES - bufferCounter)]
                                }
                                else -> {
                                    0U
                                }
                            }

                        }
                        bufferCounter = chunk.size - (BLOCK_SIZE_IN_BYTES - bufferCounter)
                    }
                }

            }
        }
    }

    private fun consumeBlock(block: Array<UByte>) {
        val w = expandChunk(block)
        mix(h, w).copyInto(h)
    }

    override fun digest(): Array<UByte> {
        val length = counter + bufferCounter
        val expansionArray = createExpansionArray(length)
        val finalBlock =
            buffer.copyOfRange(0, bufferCounter) + expansionArray + (length * 8).toULong().toPaddedByteArray()
        finalBlock.chunked(BLOCK_SIZE_IN_BYTES).forEach {
            consumeBlock(it)
        }


        val digest = h[0].toPaddedByteArray() +
                h[1].toPaddedByteArray() +
                h[2].toPaddedByteArray() +
                h[3].toPaddedByteArray() +
                h[4].toPaddedByteArray() +
                h[5].toPaddedByteArray() +
                h[6].toPaddedByteArray() +
                h[7].toPaddedByteArray()
        return digest
    }

    override fun digestString(): String {
        return digest().map { it.toString(16) }.joinToString(separator = "")
    }

    private fun appendToBuffer(array: Array<UByte>, start: Int) {
        array.copyInto(destination = buffer, destinationOffset = start, startIndex = 0, endIndex = array.size)
        bufferCounter += array.size
    }


}