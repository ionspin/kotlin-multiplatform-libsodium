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

@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.ionspin.kotlin.crypto.keyderivation.argon2

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.util.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-May-2020
 */
object Argon2Utils {

    const val R1 = 32
    const val R2 = 24
    const val R3 = 16
    const val R4 = 63

    //based on Blake2b mixRound
    internal inline fun mixRound(input: Array<UByte>): Array<ULong> {
        var v = input.chunked(8).map { it.fromLittleEndianArrayToULong() }.toTypedArray()
        v = mix(v, 0, 4, 8, 12)
        v = mix(v, 1, 5, 9, 13)
        v = mix(v, 2, 6, 10, 14)
        v = mix(v, 3, 7, 11, 15)
        v = mix(v, 0, 5, 10, 15)
        v = mix(v, 1, 6, 11, 12)
        v = mix(v, 2, 7, 8, 13)
        v = mix(v, 3, 4, 9, 14)
        return v
    }

    //Based on Blake2b mix
    private inline fun mix(v: Array<ULong>, a: Int, b: Int, c: Int, d: Int): Array<ULong> {
        v[a] = (v[a] + v[b] + 2U * (v[a] and 0xFFFFFFFFUL) * (v[b] and 0xFFFFFFFFUL))
        v[d] = (v[d] xor v[a]) rotateRight R1
        v[c] = (v[c] + v[d] + 2U * (v[c] and 0xFFFFFFFFUL) * (v[d] and 0xFFFFFFFFUL))
        v[b] = (v[b] xor v[c]) rotateRight R2
        v[a] = (v[a] + v[b] + 2U * (v[a] and 0xFFFFFFFFUL) * (v[b] and 0xFFFFFFFFUL))
        v[d] = (v[d] xor v[a]) rotateRight R3
        v[c] = (v[c] + v[d] + 2U * (v[c] and 0xFFFFFFFFUL) * (v[d] and 0xFFFFFFFFUL))
        v[b] = (v[b] xor v[c]) rotateRight R4
        return v
    }

    private fun extractColumnFromGBlock(gBlock: Array<UByte>, columnPosition: Int): Array<UByte> {
        val result = Array<UByte>(128) { 0U }
        for (i in 0..7) {
            gBlock.copyOfRange(i * 128 + (columnPosition * 16), i * 128 + (columnPosition * 16) + 16)
                .copyInto(result, i * 16)
        }
        return result
    }

    private fun copyIntoGBlockColumn(gBlock: Array<UByte>, columnPosition: Int, columnData: Array<UByte>) {
        for (i in 0..7) {
            val column = columnData.copyOfRange(i * 16, i * 16 + 16)
            column.copyInto(gBlock, i * 128 + columnPosition * 16)
        }
    }

    fun compressionFunctionG(
        previousBlock: Array<UByte>,
        referenceBlock: Array<UByte>,
        currentBlock: Array<UByte>,
        xorWithCurrentBlock: Boolean
    ): Array<UByte> {
        val r = referenceBlock xor previousBlock
        val q = Array<UByte>(1024) { 0U }
        val z = Array<UByte>(1024) { 0U }
        // Do the argon/blake2b mixing on rows
        for (i in 0..7) {
            val startOfRow = (i * 8 * 16)
            val endOfRow = startOfRow + (8 * 16)
            val rowToMix = r.copyOfRange(startOfRow, endOfRow)
            mixRound(rowToMix)
                .map { it.toLittleEndianUByteArray() }
                .flatMap { it.asIterable() }
                .toTypedArray()
                .copyInto(q, startOfRow)
        }
        // Do the argon/blake2b mixing on columns
        for (i in 0..7) {
            copyIntoGBlockColumn(
                z,
                i,
                mixRound(extractColumnFromGBlock(q, i))
                    .map { it.toLittleEndianUByteArray() }
                    .flatMap { it.asIterable() }
                    .toTypedArray()
            )
        }
        val final = if (xorWithCurrentBlock) {
            (z xor r) xor currentBlock
        } else {
            z xor r
        }
        return final
    }

    fun argonBlake2bArbitraryLenghtHash(input: Array<UByte>, length: UInt): Array<UByte> {
        if (length <= 64U) {
            return Blake2b.digest(inputMessage = length + input, hashLength = length.toInt())
        }
        //We can cast to int because UInt even if MAX_VALUE divided by 32 is guaranteed not to overflow
        val numberOf64ByteBlocks = (1U + ((length - 1U) / 32U) - 2U).toInt() // equivalent  to ceil(length/32) - 2
        val v = Array<Array<UByte>>(numberOf64ByteBlocks) { emptyArray() }
        v[0] = Blake2b.digest(length + input)
        for (i in 1 until numberOf64ByteBlocks) {
            v[i] = Blake2b.digest(v[i - 1])
        }
        val remainingPartOfInput = length.toInt() - numberOf64ByteBlocks * 32
        val vLast = Blake2b.digest(v[numberOf64ByteBlocks - 1], hashLength = remainingPartOfInput)
        val concat =
            (v.map { it.copyOfRange(0, 32) })
                .plus(listOf(vLast))
                .foldRight(emptyArray<UByte>()) { arrayOfUBytes, acc -> arrayOfUBytes + acc }

        return concat
    }
}