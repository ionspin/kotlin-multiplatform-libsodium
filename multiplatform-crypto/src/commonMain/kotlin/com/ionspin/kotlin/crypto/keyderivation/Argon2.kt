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

package com.ionspin.kotlin.crypto.keyderivation

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.util.*

/**
 *
 * Further resources and examples of implementation:
 * https://tools.ietf.org/html/draft-irtf-cfrg-argon2-03
 * https://en.wikipedia.org/wiki/Argon2
 * https://www.cryptolux.org/images/0/0d/Argon2.pdf
 * https://github.com/LoupVaillant/Monocypher/blob/master/src/monocypher.c
 * https://github.com/jedisct1/libsodium/blob/master/src/libsodium/crypto_pwhash/argon2/argon2.c
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 08-Jan-2020
 *
 */
@ExperimentalUnsignedTypes
class Argon2 internal constructor(
    val password: Array<UByte>,
    val salt: Array<UByte>,
    val parallelism: UInt,
    val tagLength: UInt,
    val memorySize: UInt,
    val numberOfIterations: UInt,
    val versionNumber: UInt,
    val key: Array<UByte>,
    val associatedData: Array<UByte>,
    val type: ArgonType
) {
    enum class ArgonType(val typeId: Int) {
        Argon2d(0), Argon2i(1), Argon2id(2)
    }

    data class Argon2StreamGContext(
        val block: Array<UByte>,
        val passNumber: Int,
        val sliceNumber: Int,
        val blockCount: UInt,
        val numberOfIterations: UInt,
        val counter: UInt,
        val type: ArgonType
    ) {

    }


    @ExperimentalStdlibApi
    companion object {


        fun argonBlake2bArbitraryLenghtHash(input: Array<UByte>, length: UInt): Array<UByte> {
            if (length <= 64U) {
                return Blake2b.digest(length + input)
            }
            //We can cast to int because UInt even if MAX_VALUE divided by 32 is guaranteed not to overflow
            val numberOfBlocks = (1U + ((length - 1U) / 32U) - 1U).toInt() // equivalent  to ceil(length/32) - 1
            val v = Array<Array<UByte>>(numberOfBlocks) { emptyArray() }
            v[0] = Blake2b.digest(length + input)
            for (i in 1 until numberOfBlocks - 1) {
                v[i] = Blake2b.digest(v[i - 1])
            }
            val remainingPartOfInput = input.copyOfRange(input.size - numberOfBlocks * 32, input.size)
            val vLast = Blake2b.digest(remainingPartOfInput, hashLength = remainingPartOfInput.size)
            val concat =
                (v.map { it.copyOfRange(0, 32) })
                    .plus(listOf(vLast))
                    .foldRight(emptyArray<UByte>()) { arrayOfUBytes, acc -> arrayOfUBytes + acc }

            return concat
        }

        fun compressionFunctionG(x: Array<UByte>, y: Array<UByte>): Array<UByte> {
            val r = Array<UByte>(1024) { 0U } // view as 8x8 matrix of 16 byte registers
            x.forEachIndexed { index, it -> r[index] = it xor y[index] }
            val q = Array<UByte>(1024) { 0U }
            val z = Array<UByte>(1024) { 0U }
            // Do the argon/blake2b mixing on rows
            for (i in 0..7) {
                val startOfRow = (i * 8 * 16)
                val endOfRow = startOfRow + (8 * 16)
                mixRound(r.copyOfRange(startOfRow, endOfRow))
                    .map { it.toLittleEndianUByteArray() }
                    .flatMap { it.asIterable() }
                    .toTypedArray()
                    .copyInto(q, startOfRow, endOfRow)
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
            // Z = Z xor R
            r.forEachIndexed { index, it -> z[index] = it xor z[index] }
            return z
        }

        private fun extractColumnFromGBlock(gBlock: Array<UByte>, columnPosition: Int): Array<UByte> {
            val result = Array<UByte>(128) { 0U }
            for (i in 0..7) {
                result[i] = gBlock[i * 8 + columnPosition]
            }
            return result
        }

        private fun copyIntoGBlockColumn(gBlock: Array<UByte>, columnPosition: Int, columnData: Array<UByte>) {
            for (i in 0..7) {
                gBlock[i * 8 + columnPosition] = columnData[i]
            }
        }


        //based on Blake2b mixRound
        internal fun mixRound(input: Array<UByte>): Array<ULong> {
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

        const val R1 = 32
        const val R2 = 24
        const val R3 = 16
        const val R4 = 63

        //Based on Blake2b mix
        private fun mix(v: Array<ULong>, a: Int, b: Int, c: Int, d: Int): Array<ULong> {
            v[a] = (v[a] + v[b] * 2U * a.toUInt() * b.toUInt())
            v[d] = (v[d] xor v[a]) rotateRight R1
            v[c] = (v[c] + v[d] * 2U * c.toUInt() * d.toUInt())
            v[b] = (v[b] xor v[c]) rotateRight R2
            v[a] = (v[a] + v[b] * 2U * a.toUInt() * b.toUInt())
            v[d] = (v[d] xor v[a]) rotateRight R3
            v[c] = (v[c] + v[d] * 2U * c.toUInt() * d.toUInt())
            v[b] = (v[b] xor v[c]) rotateRight R4
            return v
        }

        private fun computeIndexes(
            block: Array<Array<Array<UByte>>>,
            pass: Long,
            lane: Int,
            column: Int,
            blockCount: UInt,
            iterationCount: UInt,
            type: ArgonType,
            laneCounter : Int

        ): Pair<Int, Int> {
            var counter = laneCounter
            val sliceNumber = column / 4
            val sliceLength = blockCount / 4U
            val (j1, j2) = when (type) {
                ArgonType.Argon2i -> {
                    val firstPass = compressionFunctionG(
                        Array<UByte>(1024) { 0U },
                        pass.toULong().toLittleEndianUByteArray() +
                                lane.toULong().toLittleEndianUByteArray() +
                                sliceNumber.toULong().toLittleEndianUByteArray() +
                                blockCount.toULong().toLittleEndianUByteArray() +
                                iterationCount.toULong().toLittleEndianUByteArray() +
                                type.typeId.toULong().toLittleEndianUByteArray() +
                                counter.toUInt().toLittleEndianUByteArray() +
                                Array<UByte>(968) { 0U }
                    )
                    val secondPass = compressionFunctionG(
                        firstPass,
                        pass.toULong().toLittleEndianUByteArray() +
                                lane.toULong().toLittleEndianUByteArray() +
                                sliceNumber.toULong().toLittleEndianUByteArray() +
                                blockCount.toULong().toLittleEndianUByteArray() +
                                iterationCount.toULong().toLittleEndianUByteArray() +
                                type.typeId.toULong().toLittleEndianUByteArray() +
                                counter.toUInt().toLittleEndianUByteArray() +
                                Array<UByte>(968) { 0U }
                    )
                    Pair(firstPass, secondPass)
                }
                ArgonType.Argon2d -> {
                    Pair(
                        (block[laneCounter][column - 1].sliceArray(0..3).fromLittleEndianArrayToUInt()),
                        (block[laneCounter][column - 1].sliceArray(4..7).fromLittleEndianArrayToUInt())
                    )
                }
                ArgonType.Argon2id -> {
                    Pair(emptyArray<UByte>(), emptyArray<UByte>())
                }
            }

            return Pair(1, 1)

        }

        fun populateSegment(
            matrix: Array<Array<Array<UByte>>>,
            pass: Long,
            lane: Int,
            column: Int,
            blockCount: UInt,
            iterationCount: UInt,
            type: ArgonType,
            laneCounter : Int
        ) {
            //TODO handle segment by segment
        }


        internal fun derive(
            password: Array<UByte>,
            salt: Array<UByte>,
            parallelism: UInt,
            tagLength: UInt,
            memorySize: UInt,
            numberOfIterations: UInt,
            versionNumber: UInt,
            key: Array<UByte>,
            associatedData: Array<UByte>,
            type: ArgonType
        ): Array<UByte> {
            val h0 = Blake2b.digest(
                parallelism.toLittleEndianUByteArray() + tagLength.toLittleEndianUByteArray() + memorySize.toLittleEndianUByteArray() +
                        numberOfIterations.toLittleEndianUByteArray() + versionNumber.toLittleEndianUByteArray() +
                        password.size.toUInt().toLittleEndianUByteArray() + password +
                        salt.size.toUInt().toLittleEndianUByteArray() + salt +
                        key.size.toUInt().toLittleEndianUByteArray() + key +
                        associatedData.size.toUInt().toLittleEndianUByteArray() + associatedData
            )

            val blockCount = (memorySize / (4U * parallelism)) * (4U * parallelism) //
            val columnCount = blockCount / parallelism

            //TODO pass handling
            val allPasses = (0 .. numberOfIterations.toLong()).map { pass ->
                //Allocate memory as Array of parallelism rows and columnCount colums
                val matrix = Array(parallelism.toInt()) {
                    Array(columnCount.toInt()) {
                        Array<UByte>(1024) { 0U }
                    }
                }

                //Compute B[i][0]
                for (i in 0..parallelism.toInt()) {
                    matrix[i][0] =
                        argonBlake2bArbitraryLenghtHash(
                            h0 + 0.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(),
                            64U
                        )
                }

                //Compute B[i][1]
                for (i in 0..parallelism.toInt()) {
                    matrix[i][0] =
                        argonBlake2bArbitraryLenghtHash(
                            h0 + 1.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(),
                            64U
                        )
                }

                for (i in 0..parallelism.toInt()) {
                    for (j in 1..columnCount.toInt()) {

                        val counter = 0 //TODO handle counter
                        computeIndexes(matrix, pass, i, j, blockCount, numberOfIterations, type)
                        val iPrim = -1
                        val jPrim = -1
                        matrix[i][j] = compressionFunctionG(matrix[i][j - 1], matrix[iPrim][jPrim])
                    }
                }

                val result = matrix.foldIndexed(emptyArray<UByte>()) { index, acc, arrayOfArrays ->
                    return if (acc.size == 0) {
                        acc + arrayOfArrays[columnCount.toInt() - 1]
                    } else {
                        acc.mapIndexed { index, it -> it xor arrayOfArrays[columnCount.toInt() - 1][index] }
                            .toTypedArray()
                    }
                }
                result
            }



            return allPasses.foldRight(emptyArray()) { arrayOfUBytes, acc -> acc xor arrayOfUBytes } //TODO placeholder
        }

    }


}
