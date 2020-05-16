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

import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Utils.argonBlake2bArbitraryLenghtHash
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Utils.compressionFunctionG
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Utils.validateArgonParameters
import com.ionspin.kotlin.crypto.util.fromLittleEndianArrayToUInt
import com.ionspin.kotlin.crypto.util.hexColumsPrint
import com.ionspin.kotlin.crypto.util.toLittleEndianUByteArray

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-May-2020
 */

enum class ArgonType(val typeId: Int) {
    Argon2d(0), Argon2i(1), Argon2id(2)
}

data class SegmentPosition(
    val iteration: Int,
    val lane: Int,
    val slice: Int
)

class Argon2(
    val password: Array<UByte>,
    val salt: Array<UByte> = emptyArray(),
    val parallelism: Int = 1,
    val tagLength: UInt = 64U,
    requestedMemorySize: UInt = 0U,
    val numberOfIterations: UInt = 1U,
    val key: Array<UByte> = emptyArray(),
    val associatedData: Array<UByte> = emptyArray(),
    val argonType: ArgonType = ArgonType.Argon2id
) {
    init {
        validateArgonParameters(
            password,
            salt,
            parallelism,
            tagLength,
            requestedMemorySize,
            numberOfIterations,
            key,
            associatedData,
            argonType
        )
    }
    //We support only the latest version
    val versionNumber: UInt = 0x13U

    //Use either requested memory size, or default, or throw exception if the requested amount is less than 8*parallelism
    val memorySize = if (requestedMemorySize == 0U) {
        ((8 * parallelism) * 2).toUInt()
    } else {
        requestedMemorySize
    }
    val blockCount = (memorySize / (4U * parallelism.toUInt())) * (4U * parallelism.toUInt())
    val columnCount = (blockCount / parallelism.toUInt()).toInt()
    val segmentLength = columnCount / 4

    val useIndependentAddressing = argonType == ArgonType.Argon2id || argonType == ArgonType.Argon2i


    // State
    val matrix = Array(parallelism) {
        Array(columnCount) {
            Array<UByte>(1024) { 0U }
        }
    }

    private fun clearMatrix() {
        matrix.forEachIndexed { laneIndex, lane ->
            lane.forEachIndexed { columnIndex, block ->
                block.forEachIndexed { byteIndex, byte ->
                    matrix[laneIndex][columnIndex][byteIndex] = 0U
                }
            }
        }
    }

    private fun populateAddressBlock(
        iteration: Int,
        slice: Int,
        lane: Int,
        addressBlock: Array<UByte>,
        addressCounter: ULong
    ): Array<UByte> {
        //Calculate first pass
        val firstPass = compressionFunctionG(
            Array<UByte>(1024) { 0U },
            iteration.toULong().toLittleEndianUByteArray() +
                    lane.toULong().toLittleEndianUByteArray() +
                    slice.toULong().toLittleEndianUByteArray() +
                    blockCount.toULong().toLittleEndianUByteArray() +
                    numberOfIterations.toULong().toLittleEndianUByteArray() +
                    argonType.typeId.toULong().toLittleEndianUByteArray() +
                    addressCounter.toLittleEndianUByteArray() +
                    Array<UByte>(968) { 0U },
            addressBlock,
            false
        )
        val secondPass = compressionFunctionG(
            Array<UByte>(1024) { 0U },
            firstPass,
            firstPass,
            false
        )
        return secondPass
    }


    private fun computeReferenceBlockIndexes(iteration: Int, slice: Int, lane: Int, column: Int, addressBlock: Array<UByte>?): Pair<Int, Int> {
        val segmentIndex = (column % segmentLength)
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
            ArgonType.Argon2i -> {
                val selectedAddressBlock = addressBlock!!.sliceArray((segmentIndex * 8) until (segmentIndex * 8) + 8)
                val first32Bit = selectedAddressBlock.sliceArray(0 until 4).fromLittleEndianArrayToUInt()
                val second32Bit = selectedAddressBlock.sliceArray(4 until 8).fromLittleEndianArrayToUInt()
                Pair(first32Bit, second32Bit)
            }
            ArgonType.Argon2id -> {
                if (iteration == 0 && (slice == 0 || slice == 1)) {
                    val selectedAddressBlock = addressBlock!!.sliceArray((segmentIndex * 8) until (segmentIndex * 8) + 8)
                    val first32Bit = selectedAddressBlock.sliceArray(0 until 4).fromLittleEndianArrayToUInt()
                    val second32Bit = selectedAddressBlock.sliceArray(4 until 8).fromLittleEndianArrayToUInt()
                    Pair(first32Bit, second32Bit)
                } else {
                    val previousBlock = if (column == 0) {
                        matrix[lane][columnCount - 1] //Get last block in the SAME lane
                    } else {
                        matrix[lane][column - 1]
                    }
                    val first32Bit = previousBlock.sliceArray(0 until 4).fromLittleEndianArrayToUInt()
                    val second32Bit = previousBlock.sliceArray(4 until 8).fromLittleEndianArrayToUInt()
                    Pair(first32Bit, second32Bit)
                }

            }
        }

        //If this is first iteration and first slice, block is taken from the current lane
        val l = if (iteration == 0 && slice == 0) {
            lane
        } else {
            (j2.toBigInteger() % parallelism).intValue()

        }


        val referenceAreaSize = if (iteration == 0) {
            if (slice == 0) {
                //All indices except the previous
                segmentIndex - 1
            } else {
                if (lane == l) {
                    //Same lane
                    column - 1
                } else {
                    slice * (columnCount / 4) + if (segmentIndex == 0) { // Check if column is first block of the SEGMENT
                        -1
                    } else {
                        0
                    }
                }
            }
        } else {
            if (lane == l) {
                columnCount - (columnCount / 4) + (segmentIndex - 1)
            } else {
                columnCount - (columnCount / 4) + if (segmentIndex == 0) {
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
                (slice + 1) * segmentLength
            }
        }
        val absolutePosition = (startPosition + z.toInt()) % columnCount

        return Pair(l, absolutePosition)
    }

    fun derive(): Array<UByte> {
        val h0 = Blake2b.digest(
            parallelism.toUInt()
                .toLittleEndianUByteArray() + tagLength.toLittleEndianUByteArray() + memorySize.toLittleEndianUByteArray() +
                    numberOfIterations.toLittleEndianUByteArray() + versionNumber.toLittleEndianUByteArray() + argonType.typeId.toUInt()
                .toLittleEndianUByteArray() +
                    password.size.toUInt().toLittleEndianUByteArray() + password +
                    salt.size.toUInt().toLittleEndianUByteArray() + salt +
                    key.size.toUInt().toLittleEndianUByteArray() + key +
                    associatedData.size.toUInt().toLittleEndianUByteArray() + associatedData
        )

        //Compute B[i][0]
        for (i in 0 until parallelism.toInt()) {
            matrix[i][0] =
                argonBlake2bArbitraryLenghtHash(
                    h0 + 0.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(),
                    1024U
                )
        }

        //Compute B[i][1]
        for (i in 0 until parallelism.toInt()) {
            matrix[i][1] =
                argonBlake2bArbitraryLenghtHash(
                    h0 + 1.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(),
                    1024U
                )
        }
        executeArgonWithSingleThread()

        val result = matrix.foldIndexed(emptyArray<UByte>()) { lane, acc, laneArray ->
            if (acc.size == 0) {
                acc + laneArray[columnCount - 1] // add last element in first lane to the accumulator
            } else {
                // For each element in our accumulator, xor it with an appropriate element from the last column in current lane (from 1 to `parallelism`)
                acc.mapIndexed { index, it -> it xor laneArray[columnCount - 1][index] }
                    .toTypedArray()
            }
        }
        //Hash the xored last blocks
        println("Tag:")
        val hash = argonBlake2bArbitraryLenghtHash(result, tagLength)
        return hash


    }

    fun executeArgonWithSingleThread() {
        for (iteration in 0 until numberOfIterations.toInt()) {
            for (slice in 0 until 4) {
                for (lane in 0 until parallelism.toInt()) {
                    println("Processing segment I: $iteration, S: $slice, L: $lane")
                    val segmentPosition = SegmentPosition(iteration, lane, slice)
                    processSegment(segmentPosition)
                }
            }
            //Debug prints
//            println("Done with $iteration")
//            matrix[0][0].slice(0..7).toTypedArray().hexColumsPrint(8)
//            matrix[parallelism.toInt() - 1][columnCount - 1].slice(
//                1016..1023
//            ).toTypedArray().hexColumsPrint(8)

        }
    }

    fun processSegment(segmentPosition: SegmentPosition) {
        val iteration = segmentPosition.iteration
        val slice = segmentPosition.slice
        val lane = segmentPosition.lane

        var addressBlock : Array<UByte>? = null
        var addressCounter = 1UL //Starts from 1 in each segment as defined by the spec

        //Generate initial segment address block
        if (useIndependentAddressing) {
            addressBlock = Array<UByte>(1024) {
                0U
            }
            addressBlock = populateAddressBlock(iteration, slice, lane, addressBlock, addressCounter)
            addressCounter++
        }

        val startColumn = if (iteration == 0 && slice == 0) {
            2
        } else {
            slice * segmentLength
        }

        for (column in startColumn until (slice + 1) * segmentLength) {
            //Each address block contains 128 addresses, and we use one per iteration,
            //so once we do 128 iterations we need to calculate a new address block
            if (useIndependentAddressing && column != 0 && column % 128 == 0) {
                addressBlock = populateAddressBlock(iteration, slice, lane, addressBlock!!, addressCounter)
                addressCounter++
            }
            val previousColumn = if (column == 0) {
                columnCount - 1
            } else {
                column - 1
            }
            if (iteration == 1) {
                println("Breakpoint")
            }
            val (l, z) = computeReferenceBlockIndexes(
                iteration,
                slice,
                lane,
                column,
                addressBlock
            )
//            println("Calling compress for I: $iteration S: $slice Lane: $lane Column: $column with l: $l z: $z")
            matrix[lane][column] =
                compressionFunctionG(
                    matrix[lane][previousColumn],
                    matrix[l][z],
                    matrix[lane][column],
                    true
                )
        }

    }


}
