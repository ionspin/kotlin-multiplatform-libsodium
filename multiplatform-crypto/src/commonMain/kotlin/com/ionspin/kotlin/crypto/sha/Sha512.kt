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

package com.ionspin.kotlin.crypto.sha

import com.ionspin.kotlin.crypto.Hash
import com.ionspin.kotlin.crypto.chunked
import com.ionspin.kotlin.crypto.rotateRight

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 18-Jul-2019
 */

@ExperimentalUnsignedTypes
class Sha512 : Hash {
    companion object {
        const val BLOCK_SIZE = 1024
        const val BLOCK_SIZE_IN_BYTES = 128
        const val CHUNK_SIZE = 80
        const val ULONG_MASK = 0xFFFFFFFFFFFFFFFFUL

        val k = arrayOf(
            0x428a2f98d728ae22UL,
            0x7137449123ef65cdUL,
            0xb5c0fbcfec4d3b2fUL,
            0xe9b5dba58189dbbcUL,
            0x3956c25bf348b538UL,
            0x59f111f1b605d019UL,
            0x923f82a4af194f9bUL,
            0xab1c5ed5da6d8118UL,
            0xd807aa98a3030242UL,
            0x12835b0145706fbeUL,
            0x243185be4ee4b28cUL,
            0x550c7dc3d5ffb4e2UL,
            0x72be5d74f27b896fUL,
            0x80deb1fe3b1696b1UL,
            0x9bdc06a725c71235UL,
            0xc19bf174cf692694UL,
            0xe49b69c19ef14ad2UL,
            0xefbe4786384f25e3UL,
            0x0fc19dc68b8cd5b5UL,
            0x240ca1cc77ac9c65UL,
            0x2de92c6f592b0275UL,
            0x4a7484aa6ea6e483UL,
            0x5cb0a9dcbd41fbd4UL,
            0x76f988da831153b5UL,
            0x983e5152ee66dfabUL,
            0xa831c66d2db43210UL,
            0xb00327c898fb213fUL,
            0xbf597fc7beef0ee4UL,
            0xc6e00bf33da88fc2UL,
            0xd5a79147930aa725UL,
            0x06ca6351e003826fUL,
            0x142929670a0e6e70UL,
            0x27b70a8546d22ffcUL,
            0x2e1b21385c26c926UL,
            0x4d2c6dfc5ac42aedUL,
            0x53380d139d95b3dfUL,
            0x650a73548baf63deUL,
            0x766a0abb3c77b2a8UL,
            0x81c2c92e47edaee6UL,
            0x92722c851482353bUL,
            0xa2bfe8a14cf10364UL,
            0xa81a664bbc423001UL,
            0xc24b8b70d0f89791UL,
            0xc76c51a30654be30UL,
            0xd192e819d6ef5218UL,
            0xd69906245565a910UL,
            0xf40e35855771202aUL,
            0x106aa07032bbd1b8UL,
            0x19a4c116b8d2d0c8UL,
            0x1e376c085141ab53UL,
            0x2748774cdf8eeb99UL,
            0x34b0bcb5e19b48a8UL,
            0x391c0cb3c5c95a63UL,
            0x4ed8aa4ae3418acbUL,
            0x5b9cca4f7763e373UL,
            0x682e6ff3d6b2b8a3UL,
            0x748f82ee5defb2fcUL,
            0x78a5636f43172f60UL,
            0x84c87814a1f0ab72UL,
            0x8cc702081a6439ecUL,
            0x90befffa23631e28UL,
            0xa4506cebde82bde9UL,
            0xbef9a3f7b2c67915UL,
            0xc67178f2e372532bUL,
            0xca273eceea26619cUL,
            0xd186b8c721c0c207UL,
            0xeada7dd6cde0eb1eUL,
            0xf57d4f7fee6ed178UL,
            0x06f067aa72176fbaUL,
            0x0a637dc5a2c898a6UL,
            0x113f9804bef90daeUL,
            0x1b710b35131c471bUL,
            0x28db77f523047d84UL,
            0x32caab7b40c72493UL,
            0x3c9ebe0a15c9bebcUL,
            0x431d67c49c100d4cUL,
            0x4cc5d4becb3e42b6UL,
            0x597f299cfc657e2aUL,
            0x5fcb6fab3ad6faecUL,
            0x6c44198c4a475817UL
        )

        val iv = arrayOf(
            0x6a09e667f3bcc908UL,
            0xbb67ae8584caa73bUL,
            0x3c6ef372fe94f82bUL,
            0xa54ff53a5f1d36f1UL,
            0x510e527fade682d1UL,
            0x9b05688c2b3e6c1fUL,
            0x1f83d9abfb41bd6bUL,
            0x5be0cd19137e2179UL
        )

        @ExperimentalStdlibApi
        fun digest(message: String): Array<UByte> {
            return digest(message.encodeToByteArray().map { it.toUByte() }.toTypedArray())
        }

        fun digest(message: Array<UByte>): Array<UByte> {

            var h = iv.copyOf()

            val expansionArray = createExpansionArray(message.size)

            val chunks =
                (message + expansionArray + (message.size * 8).toULong().toPadded128BitByteArray()).chunked(
                    BLOCK_SIZE_IN_BYTES)

            chunks.forEach { chunk ->
                val w = expandChunk(chunk)
                mix(h, w)

            }

            val digest =
                    h[0].toPaddedByteArray() +
                    h[1].toPaddedByteArray() +
                    h[2].toPaddedByteArray() +
                    h[3].toPaddedByteArray() +
                    h[4].toPaddedByteArray() +
                    h[5].toPaddedByteArray() +
                    h[6].toPaddedByteArray() +
                    h[7].toPaddedByteArray()
            return digest
        }

        private fun scheduleSigma0(value: ULong): ULong {
            return value.rotateRight(1) xor value.rotateRight(8) xor (value shr 7)
        }

        private fun scheduleSigma1(value: ULong): ULong {
            return value.rotateRight(19) xor value.rotateRight(61) xor (value shr 6)
        }

        private fun compressionSigma0(e: ULong): ULong {
            return (e rotateRight 28) xor (e rotateRight 34) xor (e rotateRight 39)
        }

        private fun compressionSigma1(a: ULong): ULong {
            return (a rotateRight 14) xor (a rotateRight 18) xor (a rotateRight 41)
        }

        private fun ch(x: ULong, y: ULong, z: ULong): ULong {
            return ((x and y) xor ((x xor ULONG_MASK) and z))
        }

        private fun maj(x: ULong, y: ULong, z: ULong): ULong {
            return ((x and y) xor (x and z) xor (y and z))
        }

        private fun expandChunk(chunk: Array<UByte>): Array<ULong> {
            val w = Array<ULong>(CHUNK_SIZE) {
                when (it) {
                    in 0 until 16 -> {
                        var collected = (chunk[(it * 8)].toULong() shl 56) +
                                (chunk[(it * 8) + 1].toULong() shl 48) +
                                (chunk[(it * 8) + 2].toULong() shl 40) +
                                (chunk[(it * 8) + 3].toULong() shl 32) +
                                (chunk[(it * 8) + 4].toULong() shl 24) +
                                (chunk[(it * 8) + 5].toULong() shl 16) +
                                (chunk[(it * 8) + 6].toULong() shl 8) +
                                (chunk[(it * 8) + 7].toULong())
                        collected
                    }
                    else -> 0UL
                }
            }
            for (i in 16 until CHUNK_SIZE) {
                val s0 = scheduleSigma0(w[i - 15])
                val s1 = scheduleSigma1(w[i - 2])
                w[i] = w[i - 16] + s0 + w[i - 7] + s1
            }
            return w
        }

        private fun mix(h: Array<ULong>, w: Array<ULong>): Array<ULong> {
            var paramA = h[0]
            var paramB = h[1]
            var paramC = h[2]
            var paramD = h[3]
            var paramE = h[4]
            var paramF = h[5]
            var paramG = h[6]
            var paramH = h[7]

            for (i in 0 until CHUNK_SIZE) {
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

        fun createExpansionArray(originalSizeInBytes : Int) : Array<UByte> {
            val originalMessageSizeInBits = originalSizeInBytes * 8

            val expandedRemainderOf1024 = (originalMessageSizeInBits + 129) % BLOCK_SIZE
            val zeroAddAmount = when (expandedRemainderOf1024) {
                0 -> 0
                else -> (BLOCK_SIZE - expandedRemainderOf1024) / 8
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
            val byteMask = 0xFFUL
            //Ignore messages longer than 64 bits for now
            return Array(8) {
                when (it) {
                    7 -> (this and byteMask).toUByte()
                    6 -> ((this shr 8) and byteMask).toUByte()
                    5 -> ((this shr 16) and byteMask).toUByte()
                    4 -> ((this shr 24) and byteMask).toUByte()
                    3 -> ((this shr 32) and byteMask).toUByte()
                    2 -> ((this shr 40) and byteMask).toUByte()
                    1 -> ((this shr 48) and byteMask).toUByte()
                    0 -> ((this shr 56) and byteMask).toUByte()
                    else -> 0U
                }
            }
        }

        private fun ULong.toPadded128BitByteArray(): Array<UByte> {
            val byteMask = 0xFFUL
            //Ignore messages longer than 64 bits for now
            return Array(16) {
                when (it) {
                    15 -> (this and byteMask).toUByte()
                    14 -> ((this shr 8) and byteMask).toUByte()
                    13 -> ((this shr 16) and byteMask).toUByte()
                    12 -> ((this shr 24) and byteMask).toUByte()
                    11 -> ((this shr 32) and byteMask).toUByte()
                    10 -> ((this shr 40) and byteMask).toUByte()
                    9 -> ((this shr 48) and byteMask).toUByte()
                    8 -> ((this shr 54) and byteMask).toUByte()
                    else -> 0U
                }
            }
        }
    }

    var h = iv.copyOf()
    var counter = 0
    var bufferCounter = 0
    var buffer = Array<UByte>(BLOCK_SIZE_IN_BYTES) { 0U }

    @ExperimentalStdlibApi
    fun update(message: String) {
        return update(message.encodeToByteArray().map { it.toUByte() }.toTypedArray())
    }

    fun update(array: Array<UByte>) {
        if (array.isEmpty()) {
            throw RuntimeException("Updating with empty array is not allowed. If you need empty hash, just call digest without updating")
        }

        when {
            bufferCounter + array.size < BLOCK_SIZE_IN_BYTES -> appendToBuffer(array, bufferCounter)
            bufferCounter + array.size >= BLOCK_SIZE_IN_BYTES -> {
                val chunked = array.chunked(BLOCK_SIZE_IN_BYTES)
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

    fun digest() : Array<UByte> {
        val length = counter + bufferCounter
        val expansionArray = createExpansionArray(length)
        val finalBlock = buffer.copyOfRange(0, bufferCounter) + expansionArray + (length * 8).toULong().toPadded128BitByteArray()
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

    private fun appendToBuffer(array: Array<UByte>, start: Int) {
        array.copyInto(destination = buffer, destinationOffset = start, startIndex = 0, endIndex = array.size)
        bufferCounter += array.size
    }


}