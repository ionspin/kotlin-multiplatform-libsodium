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

import com.ionspin.kotlin.bignum.integer.toBigInteger
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
@ExperimentalStdlibApi
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

        fun Array<UByte>.xor(target: Array<UByte>, other: Array<UByte>) {
            if (this.size != other.size || this.size != target.size) {
                throw RuntimeException("Invalid array sizes, this ${this.size}, other ${other.size}")
            }
            target.mapIndexed { index, _ -> this[index] xor other[index] }
        }


        fun argonBlake2bArbitraryLenghtHash(input: Array<UByte>, length: UInt): Array<UByte> {
            if (length <= 64U) {
                return Blake2b.digest(length + input)
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


        fun compressionFunctionG(
            previousBlock: Array<UByte>,
            referenceBlock: Array<UByte>,
            currentBlock: Array<UByte>,
            xorWithCurrentBlock: Boolean
        ): Array<UByte> {
            val r = referenceBlock xor previousBlock
//            println("R = X xor Y")
//            r.hexColumsPrint(16)
//            val r = Array<UByte>(1024) { 0U } // view as 8x8 matrix of 16 byte registers
//            x.forEachIndexed { index, it -> r[index] = it xor y[index] } // R = X xor Y
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
//            println("---- Q -----")
//            q.hexColumsPrint(16)
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
//            println("---- Z -----")
//            z.hexColumsPrint(16)
            val final = if (xorWithCurrentBlock) {
//                println("Z xor R xor CURRENT")
                (z xor r) xor currentBlock
            } else {
//                println("Z xor R")
                z xor r
            }

//            final.hexColumsPrint(16)
            return final
        }

        private fun extractColumnFromGBlock(gBlock: Array<UByte>, columnPosition: Int): Array<UByte> {
            val result = Array<UByte>(128) { 0U }
            for (i in 0..7) {
                gBlock.copyOfRange(i * 128 + (columnPosition * 16), i * 128 + (columnPosition * 16) + 16).copyInto(result, i * 16)
            }
            return result
        }

        private fun copyIntoGBlockColumn(gBlock: Array<UByte>, columnPosition: Int, columnData: Array<UByte>) {
            for (i in 0..7) {
                val column = columnData.copyOfRange(i * 16, i * 16 + 16)
                column.copyInto(gBlock, i * 128 + columnPosition * 16)
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

        private fun computeIndexes(
            indexContext: IndexContext,
            matrix: Array<Array<Array<UByte>>>
        ): Pair<Int, Int> {
            val block = indexContext.indexMatrix
            val parallelism = indexContext.parallelism
            val pass = indexContext.pass
            val lane = indexContext.lane
            val column = indexContext.column
            val blockCount = indexContext.blockCount
            val iterationCount = indexContext.iterationCount
            val type = indexContext.type
            val laneCounter = indexContext.laneCounter

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
                                Array<UByte>(968) { 0U },
                        emptyArray(),
                        false
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
                                Array<UByte>(968) { 0U },
                        emptyArray(),
                        false
                    )
                    secondPass.hexColumsPrint()
                    Pair(1U, 1U)
                }
                ArgonType.Argon2d -> {
                    Pair(
                        (matrix[laneCounter][column - 1].sliceArray(0..3).fromLittleEndianArrayToUInt()),
                        (matrix[laneCounter][column - 1].sliceArray(4..7).fromLittleEndianArrayToUInt())
                    )
                }
                ArgonType.Argon2id -> {
                    Pair(1U, 1U)
                }
            }

            val l = if (pass == 0L && sliceNumber == 0) {
                2U
            } else {
                j2 % parallelism
            }

//            val availableIndices = if ()


            return Pair(1, 1)

        }

        data class IndexContext(
            val indexMatrix: Array<UByte>,
            val parallelism: UInt,
            val pass: Long,
            val lane: Int,
            val column: Int,
            val blockCount: UInt,
            val iterationCount: UInt,
            val type: ArgonType,
            val laneCounter: Int
        )

        private fun computeIndexNew(
            matrix: Array<Array<Array<UByte>>>,
            lane: Int,
            column: Int,
            columnCount: Int,
            parallelism: Int,
            iteration: Int,
            slice: Int,
            argonType: ArgonType
        ): Pair<Int, Int> {
            val (j1, j2) = when (argonType) {
                ArgonType.Argon2d -> {
                    val previousBlock = if (column == 0) {
                        matrix[lane][columnCount - 1] //Get last block in the SAME lane
                    } else {
                        matrix[lane][column - 1]
                    }
                    val first32Bit = previousBlock.sliceArray(0 until 4).fromLittleEndianArrayToUInt()
                    val second32Bit = previousBlock.sliceArray(4 until 8).fromLittleEndianArrayToUInt()
                    Pair(first32Bit, second32Bit)
                }
                ArgonType.Argon2i -> TODO()
                ArgonType.Argon2id -> TODO()
            }


            //If this is first iteration and first slice, block is taken from the current lane
            val l = if (iteration == 0 && slice == 0) {
                lane
            } else {
                (j2.toBigInteger() % parallelism).intValue()

            }

            //From Argon 2 2020 draft

            // The set W contains the indices that can be referenced according to
            // the following rules:
            // 1.  If l is the current lane, then W includes the indices of all
            // blocks in the last SL - 1 = 3 segments computed and finished, as
            // well as the blocks computed in the current segment in the current
            //         pass excluding B[i][j-1].
            //
            // 2.  If l is not the current lane, then W includes the indices of all
            // blocks in the last SL - 1 = 3 segments computed and finished in
            //         lane l.  If B[i][j] is the first block of a segment, then the
            // very last index from W is excluded.
            val segmentIndex = column - (slice * (columnCount / 4))
            val referenceAreaSize = if (iteration == 0) {
                if (slice == 0) {
                    //All indices except the previous
                    (column % (columnCount / 4)) - 1
                } else {
                    if (lane == l) {
                        //Same lane
                        column - 1
                    } else {
                        slice * (columnCount / 4) + if (column % (columnCount / 4) == 0) { // Check if column is first block of the SEGMENT
                            -1
                        } else {
                            0
                        }
                    }
                }
            } else {
                if (lane == l) {
                    columnCount - (columnCount / 4) + (column % (columnCount / 4) - 1)
                } else {
                    columnCount - (columnCount / 4) + if (column % (columnCount / 4) == 0)  {
                        -1
                    } else {
                        0
                    }
                }
            }

            val x = (j1.toULong() * j1) shr 32
            val y = (referenceAreaSize.toULong() * x) shr 32
            val z = referenceAreaSize.toULong() - 1U - y

            val startPosition = if (iteration == 0) {
                0
            } else {
                if (slice == 3) {
                    0
                } else {
                    (slice + 1) * (columnCount / 4) //TODO replace all of these with segment length when consolidating variables
                }
            }
            if ( (startPosition + z.toInt()) % columnCount == -1) {
                println("Debug")
            }
            val absolutePosition = (startPosition + z.toInt()) % columnCount

            return Pair(l, absolutePosition)
        }

        data class ArgonContext(
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
        )

        data class ArgonInternalContext(
            val matrix: Array<Array<Array<UByte>>>,
            val blockCount: UInt,
            val columnCount: Int,
            val segmentLength: Int
        )

        data class SegmentPosition(
            val iteration: Int,
            val lane: Int,
            val slice: Int
        )

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
            val argonContext = ArgonContext(
                password = password,
                salt = salt,
                parallelism = parallelism,
                tagLength = tagLength,
                memorySize = memorySize,
                numberOfIterations = numberOfIterations,
                versionNumber = versionNumber,
                key = key,
                associatedData = associatedData,
                type = type
            )

            println("H0 Input")
            val toDigest =
                parallelism.toLittleEndianUByteArray() + tagLength.toLittleEndianUByteArray() + memorySize.toLittleEndianUByteArray() +
                        numberOfIterations.toLittleEndianUByteArray() + versionNumber.toLittleEndianUByteArray() + type.typeId.toUInt()
                    .toLittleEndianUByteArray() +
                        password.size.toUInt().toLittleEndianUByteArray() + password +
                        salt.size.toUInt().toLittleEndianUByteArray() + salt +
                        key.size.toUInt().toLittleEndianUByteArray() + key +
                        associatedData.size.toUInt().toLittleEndianUByteArray() + associatedData
            toDigest.hexColumsPrint(16)
            println("Marker H0 Input end")
            val h0 = Blake2b.digest(
                parallelism.toLittleEndianUByteArray() + tagLength.toLittleEndianUByteArray() + memorySize.toLittleEndianUByteArray() +
                        numberOfIterations.toLittleEndianUByteArray() + versionNumber.toLittleEndianUByteArray() + type.typeId.toUInt()
                    .toLittleEndianUByteArray() +
                        password.size.toUInt().toLittleEndianUByteArray() + password +
                        salt.size.toUInt().toLittleEndianUByteArray() + salt +
                        key.size.toUInt().toLittleEndianUByteArray() + key +
                        associatedData.size.toUInt().toLittleEndianUByteArray() + associatedData
            )

            h0.hexColumsPrint(8)
            println("Marker H0")

            val blockCount = (memorySize / (4U * parallelism)) * (4U * parallelism)
            val columnCount = (blockCount / parallelism).toInt()
            val segmentLength = columnCount / 4

            // First iteration

            //Allocate memory as Array of parallelism rows (lanes) and columnCount columns
            val matrix = Array(parallelism.toInt()) {
                Array(columnCount) {
                    Array<UByte>(1024) { 0U }
                }
            }
//            matrix.hexPrint()

            //Compute B[i][0]
            for (i in 0 until parallelism.toInt()) {
                matrix[i][0] =
                    argonBlake2bArbitraryLenghtHash(
                        h0 + 0.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(),
                        1024U
                    )
//                println("Start, matrix [$i][0]")
//                matrix[i][0].hexColumsPrint(16)
//                println("Marker, matrix [$i][0]")
            }

            //Compute B[i][1]
            for (i in 0 until parallelism.toInt()) {
                matrix[i][1] =
                    argonBlake2bArbitraryLenghtHash(
                        h0 + 1.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(),
                        1024U
                    )
//                println("Start, matrix [$i][1]")
//                matrix[i][1].hexColumsPrint(16)
//                println("Marker, matrix [$i][1]")
            }

            // ---- Good until here at least ----
            val argonInternalContext = ArgonInternalContext(
                matrix, blockCount, columnCount, segmentLength
            )
            singleThreaded(argonContext, argonInternalContext)

            return emptyArray()
        }

        fun singleThreaded(argonContext: ArgonContext, argonInternalContext: ArgonInternalContext) {
            for (iteration in 0 until argonContext.numberOfIterations.toInt()) {
                for (slice in 0 until 4) {
                    for (lane in 0 until argonContext.parallelism.toInt()) {
                        println("Processing segment I: $iteration, S: $slice, L: $lane")
                        val segmentPosition = SegmentPosition(iteration, lane, slice)
                        processSegment(argonContext, argonInternalContext, segmentPosition)
                    }
                }
                println("Done with $iteration")
                argonInternalContext.matrix[0][0].slice(0 .. 7).toTypedArray().hexColumsPrint(8)
                argonInternalContext.matrix[argonContext.parallelism.toInt() - 1][argonInternalContext.columnCount - 1].slice(1016 .. 1023).toTypedArray().hexColumsPrint(8)
            }
        }

        fun processSegment(
            argonContext: ArgonContext,
            argonInternalContext: ArgonInternalContext,
            segmentPosition: SegmentPosition
        ) {
            val password = argonContext.password
            val salt = argonContext.salt
            val parallelism = argonContext.parallelism
            val tagLength = argonContext.tagLength
            val memorySize = argonContext.memorySize
            val numberOfIterations = argonContext.numberOfIterations
            val versionNumber = argonContext.versionNumber
            val key = argonContext.key
            val associatedData = argonContext.associatedData
            val type = argonContext.type

            val matrix = argonInternalContext.matrix
            val blockCount = argonInternalContext.blockCount
            val columnCount = argonInternalContext.columnCount
            val segmentLength = argonInternalContext.segmentLength

            val iteration = segmentPosition.iteration
            val lane = segmentPosition.lane
            val slice = segmentPosition.slice


            if (iteration == 0) {
                //Compute B[i][j]
                //Using B[i][j] = G(B[i][j], B[l][z]) where l and z are provided bu computeIndexes
                //Because this is iteration 0 we have B[i][0] and B[i][1] already filled, so whenever we
                //are processing first segment we skip these two blocks
                if (slice == 0) {
                    for (column in 2..(slice * segmentLength)) {
                        val (l, z) = computeIndexNew(matrix, lane, column, columnCount, parallelism.toInt(), 0, 0, type)
                        println("Calling compress for I: $iteration S: $slice Lane: $lane Column: $column with l: $l z: $z")
                        matrix[lane][column] =
                            compressionFunctionG(matrix[lane][column - 1], matrix[l][z], matrix[lane][column], false)
//                        matrix[lane][column].hexColumsPrint(16)
                    }

                } else {
                    for (column in (slice * segmentLength) until ((slice + 1) * segmentLength)) {
                        val (l, z) = computeIndexNew(
                            matrix,
                            lane,
                            column,
                            columnCount,
                            parallelism.toInt(),
                            iteration,
                            slice,
                            type
                        )
                        println("Calling compress for I: $iteration S: $slice Lane: $lane Column: $column with l: $l z: $z")
                        matrix[lane][column] =
                            compressionFunctionG(matrix[lane][column - 1], matrix[l][z], matrix[lane][column], false)
//                        matrix[lane][column].hexColumsPrint(16)
                        println("debug")
                    }
                }
            } else {
                if (slice == 0) {
                    val (l, z) = computeIndexNew(matrix, lane, 0, columnCount, parallelism.toInt(), iteration, slice, type)
                    matrix[lane][0] = compressionFunctionG(matrix[lane][columnCount - 1], matrix[l][z], matrix[lane][0], true)
                    for (column in 1 until segmentLength) {
                        val (l, z) = computeIndexNew(matrix, lane, column, columnCount, parallelism.toInt(), iteration, slice, type)
                        println("Calling compress for I: $iteration S: $slice Lane: $lane Column: $column with l: $l z: $z")
                        matrix[lane][column] =
                            compressionFunctionG(matrix[lane][column - 1], matrix[l][z], matrix[lane][column], true)
//                    matrix[lane][column].hexColumsPrint(16)
                    }
                } else {
                    for (column in slice * segmentLength until (slice + 1) * segmentLength) {
                        val (l, z) = computeIndexNew(matrix, lane, column, columnCount, parallelism.toInt(), iteration, slice, type)
                        println("Calling compress for I: $iteration S: $slice Lane: $lane Column: $column with l: $l z: $z")
                        matrix[lane][column] =
                            compressionFunctionG(matrix[lane][column - 1], matrix[l][z], matrix[lane][column], true)
//                    matrix[lane][column].hexColumsPrint(16)
                    }
                }


            }




//            //Remaining iteration
//            val remainingIterations = (1..numberOfIterations.toInt()).map { iteration ->
//
//                for (i in 0 until parallelism.toInt()) {
//                    for (j in 0 until columnCount) {
//                        val (l, z) = computeIndexNew(
//                            matrix,
//                            i,
//                            j,
//                            columnCount,
//                            parallelism.toInt(),
//                            iteration,
//                            iteration / segmentLength,
//                            type
//                        )
//                        if (j == 0) {
//                            matrix[i][j] = compressionFunctionG(matrix[i][columnCount - 1], matrix[l][z])
//                        } else {
//                            matrix[i][j] = compressionFunctionG(matrix[i][j - 1], matrix[l][z])
//                        }
//
//                    }
//                }
//
//
//                val result = matrix.foldIndexed(emptyArray<UByte>()) { lane, acc, laneArray ->
//                    return if (acc.size == 0) {
//                        acc + laneArray[columnCount - 1] // add last element in first lane to the accumulator
//                    } else {
//                        // For each element in our accumulator, xor it with an appropriate element from the last column in current lane (from 1 to `parallelism`)
//                        acc.mapIndexed { index, it -> it xor laneArray[columnCount - 1][index] }
//                            .toTypedArray()
//                    }
//                }
//                result
//            }


//            return remainingIterations.foldRight(emptyArray()) { arrayOfUBytes, acc -> acc xor arrayOfUBytes } //TODO placeholder
        }

    }


    fun calculate(): Array<UByte> {
        return derive(
            password,
            salt,
            parallelism,
            tagLength,
            memorySize,
            numberOfIterations,
            versionNumber,
            key,
            associatedData,
            type
        )
    }


}

internal object ArgonDebugUtils {
    fun Array<Array<Array<UByte>>>.hexPrint() {
        forEachIndexed { i, lane ->
            lane.forEachIndexed { j, column ->
                println("Printing position at [$i], [$j]")
                column.hexColumsPrint(32)
            }
        }
    }
}
