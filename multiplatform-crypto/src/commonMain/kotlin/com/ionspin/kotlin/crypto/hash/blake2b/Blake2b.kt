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

package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.ionspin.kotlin.crypto.*
import com.ionspin.kotlin.crypto.hash.StatelessHash
import com.ionspin.kotlin.crypto.hash.UpdateableHash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */

@ExperimentalUnsignedTypes
class Blake2b(val key: Array<UByte>? = null, val hashLength: Int = 64) : UpdateableHash {

    companion object : StatelessHash {

        const val BITS_IN_WORD = 64
        const val ROUNDS_IN_COMPRESS = 12
        const val BLOCK_BYTES = 128
        override val MAX_HASH_BYTES = 64
        const val MIN_HASH_BYTES = 1
        const val MAX_KEY_BYTES = 64
        const val MIN_KEY_BYTES = 0
        val MAX_INPUT_BYTES = 2.toBigInteger() shl 128

        private val sigma = arrayOf(
            arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15),
            arrayOf(14, 10, 4, 8, 9, 15, 13, 6, 1, 12, 0, 2, 11, 7, 5, 3),
            arrayOf(11, 8, 12, 0, 5, 2, 15, 13, 10, 14, 3, 6, 7, 1, 9, 4),
            arrayOf(7, 9, 3, 1, 13, 12, 11, 14, 2, 6, 5, 10, 4, 0, 15, 8),
            arrayOf(9, 0, 5, 7, 2, 4, 10, 15, 14, 1, 11, 12, 6, 8, 3, 13),
            arrayOf(2, 12, 6, 10, 0, 11, 8, 3, 4, 13, 7, 5, 15, 14, 1, 9),
            arrayOf(12, 5, 1, 15, 14, 13, 4, 10, 0, 7, 6, 3, 9, 2, 8, 11),
            arrayOf(13, 11, 7, 14, 12, 1, 3, 9, 5, 0, 15, 4, 8, 6, 2, 10),
            arrayOf(6, 15, 14, 9, 11, 3, 0, 8, 12, 2, 13, 7, 1, 4, 10, 5),
            arrayOf(10, 2, 8, 4, 7, 6, 1, 5, 15, 11, 9, 14, 3, 12, 13, 0)
        )


        private val iv = arrayOf(
            0X6A09E667F3BCC908UL,
            0XBB67AE8584CAA73BUL,
            0X3C6EF372FE94F82BUL,
            0XA54FF53A5F1D36F1UL,
            0X510E527FADE682D1UL,
            0X9B05688C2B3E6C1FUL,
            0X1F83D9ABFB41BD6BUL,
            0X5BE0CD19137E2179UL
        )

        const val R1 = 32
        const val R2 = 24
        const val R3 = 16
        const val R4 = 63

        internal fun mixRound(input: Array<ULong>, message: Array<ULong>, round: Int): Array<ULong> {
            var v = input
            val selectedSigma = sigma[round % 10]
            v = mix(v, 0, 4, 8, 12, message[selectedSigma[0]], message[selectedSigma[1]])
            v = mix(v, 1, 5, 9, 13, message[selectedSigma[2]], message[selectedSigma[3]])
            v = mix(v, 2, 6, 10, 14, message[selectedSigma[4]], message[selectedSigma[5]])
            v = mix(v, 3, 7, 11, 15, message[selectedSigma[6]], message[selectedSigma[7]])
            v = mix(v, 0, 5, 10, 15, message[selectedSigma[8]], message[selectedSigma[9]])
            v = mix(v, 1, 6, 11, 12, message[selectedSigma[10]], message[selectedSigma[11]])
            v = mix(v, 2, 7, 8, 13, message[selectedSigma[12]], message[selectedSigma[13]])
            v = mix(v, 3, 4, 9, 14, message[selectedSigma[14]], message[selectedSigma[15]])
            return v

        }

        private fun mix(v: Array<ULong>, a: Int, b: Int, c: Int, d: Int, x: ULong, y: ULong): Array<ULong> {
            v[a] = (v[a] + v[b] + x)
            v[d] = (v[d] xor v[a]) rotateRight R1
            v[c] = (v[c] + v[d])
            v[b] = (v[b] xor v[c]) rotateRight R2
            v[a] = (v[a] + v[b] + y)
            v[d] = (v[d] xor v[a]) rotateRight R3
            v[c] = (v[c] + v[d])
            v[b] = (v[b] xor v[c]) rotateRight R4
            return v
        }

        fun compress(
            h: Array<ULong>,
            input: Array<UByte>,
            offsetCounter: BigInteger,
            finalBlock: Boolean
        ): Array<ULong> {
            var v = Array(16) {
                when (it) {
                    in 0..7 -> h[it]
                    else -> iv[it - 8]
                }
            }

            val m = input.foldIndexed(Array(16) { 0UL }) { index, acc, byte ->
                val slot = index / 8
                val position = index % 8
                acc[slot] = acc[slot] + (byte.toULong() shl ((position) * 8))
                acc
            }
            if (Config.DEBUG) {
                val printout = m.map { it.toString(16) }.chunked(4)
                printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
                println("Offset ${offsetCounter}")
            }

            v[12] = v[12] xor offsetCounter.ulongValue()
            v[13] = v[13] xor (offsetCounter shr BITS_IN_WORD).ulongValue()

            if (finalBlock) {
//            v[14] = v[14] xor 0xFFFFFFFFFFFFFFFFUL
                v[14] = v[14].inv()
            }

            for (i in 0 until ROUNDS_IN_COMPRESS) {
                mixRound(v, m, i)
            }

            for (i in 0..7) {
                h[i] = h[i] xor v[i] xor v[i + 8]
            }
            return h
        }

        @ExperimentalStdlibApi
        override fun digest(inputString: String, key: String?, hashLength: Int): Array<UByte> {
            val array = inputString.encodeToByteArray().map { it.toUByte() }.toList().toTypedArray()
            val keyBytes = key?.run {
                encodeToByteArray().map { it.toUByte() }.toTypedArray()
            } ?: emptyArray()
            return digest(inputMessage = array, key = keyBytes)

        }

        override fun digest(
            inputMessage: Array<UByte>,
            key: Array<UByte>,
            hashLength: Int
        ): Array<UByte> {
            val chunkedMessage = inputMessage.chunked(BLOCK_BYTES)

            val h = iv.copyOf()

            h[0] = h[0] xor 0x01010000UL xor (key.size.toULong() shl 8) xor hashLength.toULong()


            val message = if (key.isEmpty()) {
                if (chunkedMessage.isEmpty()) {
                    Array(1) {
                        Array<UByte>(128) {
                            0U
                        }
                    }
                } else {
                    chunkedMessage
                }
            } else {
                arrayOf(padToBlock(key), *chunkedMessage)
            }

            if (message.size > 1) {
                for (i in 0 until message.size - 1) {
                    compress(h, message[i], ((i + 1) * BLOCK_BYTES).toBigInteger(), false).copyInto(h)
                }
            }

            val lastSize = when (message.size) {
                0 -> 0
                1 -> message[message.size - 1].size
                else -> (message.size - 1) * BLOCK_BYTES + message[message.size - 1].size

            }

            val lastBlockPadded = if (message.isNotEmpty()) {
                padToBlock(message[message.size - 1])
            } else {
                Array<UByte>(16) { 0U }
            }

            compress(h, lastBlockPadded, lastSize.toBigInteger(), true).copyInto(h)


            return formatResult(h)
        }

        private fun formatResult(h: Array<ULong>): Array<UByte> {
            return h.map {
                arrayOf(
                    (it and 0xFFUL).toUByte(),
                    (it shr 8 and 0xFFUL).toUByte(),
                    (it shr 16 and 0xFFUL).toUByte(),
                    (it shr 24 and 0xFFUL).toUByte(),
                    (it shr 32 and 0xFFUL).toUByte(),
                    (it shr 40 and 0xFFUL).toUByte(),
                    (it shr 48 and 0xFFUL).toUByte(),
                    (it shr 56 and 0xFFUL).toUByte()
                )
            }.flatMap {
                it.toList()
            }.toTypedArray()
        }

        private fun padToBlock(unpadded: Array<UByte>): Array<UByte> {
            if (unpadded.size == BLOCK_BYTES) {
                return unpadded
            }

            if (unpadded.size > BLOCK_BYTES) {
                throw IllegalStateException("Block larger than 128 bytes")
            }

            return Array(BLOCK_BYTES) {
                when (it) {
                    in 0 until unpadded.size -> unpadded[it]
                    else -> 0U
                }
            }

        }
    }
    @ExperimentalStdlibApi
    constructor(
        key: String?,
        requestedHashLenght: Int = 64
    ) : this(
        (key?.encodeToByteArray()?.map { it.toUByte() }?.toTypedArray() ?: emptyArray<UByte>()),
        requestedHashLenght
    )

    override val MAX_HASH_BYTES: Int = Blake2b.MAX_HASH_BYTES

    var h = iv.copyOf()
    var counter = BigInteger.ZERO
    var bufferCounter = 0
    var buffer = Array<UByte>(BLOCK_BYTES) { 0U }


    init {
        h[0] = h[0] xor 0x01010000UL xor (key?.run { size.toULong() shl 8 } ?: 0UL) xor hashLength.toULong()

        if (!key.isNullOrEmpty()) {
            appendToBuffer(padToBlock(key), bufferCounter)
        }
    }

    override fun update(data: Array<UByte>) {
        if (data.isEmpty()) {
            throw RuntimeException("Updating with empty array is not allowed. If you need empty hash, just call digest without updating")
        }

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
                        counter += BLOCK_BYTES
                        consumeBlock(buffer)
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
    @ExperimentalStdlibApi
    override fun update(data: String) {
        update(data.encodeToByteArray().map { it.toUByte() }.toTypedArray())
    }

    private fun appendToBuffer(array: Array<UByte>, start: Int) {
        array.copyInto(destination = buffer, destinationOffset = start, startIndex = 0, endIndex = array.size)
        bufferCounter += array.size
    }

    private fun consumeBlock(block: Array<UByte>) {
        h = compress(h, block, counter, false)
    }

    override fun digest(): Array<UByte> {
        val lastBlockPadded = padToBlock(buffer)
        counter += bufferCounter
        compress(h, lastBlockPadded, counter, true)

        val result = formatResult(h)
        reset()
        return result

    }

    override fun digestString(): String {
        return digest().map { it.toString(16) }.joinToString(separator = "")
    }

    private fun reset() {
        h = iv.copyOf()
        counter = BigInteger.ZERO
        bufferCounter = 0
        buffer = Array<UByte>(BLOCK_BYTES) { 0U }
    }


}





