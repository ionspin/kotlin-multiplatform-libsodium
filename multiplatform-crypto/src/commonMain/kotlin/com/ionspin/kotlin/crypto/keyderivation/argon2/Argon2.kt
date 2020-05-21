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
import com.ionspin.kotlin.crypto.SRNG
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.keyderivation.KeyDerivationFunction
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Utils.argonBlake2bArbitraryLenghtHash
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Utils.compressionFunctionG
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Utils.validateArgonParameters
import com.ionspin.kotlin.crypto.util.*

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

data class ArgonResult(
    val hashBytes: UByteArray,
    val salt: UByteArray
) {
    val hashString by lazy { hashBytes.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "") }
    val saltString by lazy { salt.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "") }

}

@ExperimentalStdlibApi
class Argon2(
    private val password: UByteArray,
    private val salt: UByteArray = ubyteArrayOf(),
    private val parallelism: Int = 1,
    private val tagLength: UInt = 64U,
    requestedMemorySize: UInt = 0U,
    private val numberOfIterations: Int = 1,
    private val key: UByteArray = ubyteArrayOf(),
    private val associatedData: UByteArray = ubyteArrayOf(),
    private val argonType: ArgonType = ArgonType.Argon2id
) : KeyDerivationFunction {

    companion object {
        fun derive(
            password: String,
            salt: String? = null,
            key: String,
            associatedData: String,
            parallelism: Int = 16,
            tagLength: Int = 64,
            memory: Int = 4096,
            numberOfIterations: Int = 10,
        ): ArgonResult {
            val salt = SRNG.getRandomBytes(64)
            val argon = Argon2(
                password.encodeToByteArray().toUByteArray(),
                salt,
                parallelism,
                tagLength.toUInt(),
                memory.toUInt(),
                numberOfIterations,
                key.encodeToByteArray().toUByteArray(),
                associatedData.encodeToByteArray().toUByteArray(),
                ArgonType.Argon2id
            )
            val resultArray = argon.derive()
            return ArgonResult(resultArray, salt)
        }
    }

    constructor(
        password: String,
        salt: String = "",
        parallelism: Int = 1,
        tagLength: UInt = 64U,
        requestedMemorySize: UInt = 0U,
        numberOfIterations: Int = 10,
        key: String = "",
        associatedData: String = "",
        argonType: ArgonType = ArgonType.Argon2id
    ) : this(
        password.encodeToByteArray().toUByteArray(),
        salt.encodeToByteArray().toUByteArray(),
        parallelism,
        tagLength,
        requestedMemorySize,
        numberOfIterations,
        key.encodeToByteArray().toUByteArray(),
        associatedData.encodeToByteArray().toUByteArray(),
        argonType
    )

    //We support only the latest version
    private val versionNumber: UInt = 0x13U

    //Use either requested memory size, or default, or throw exception if the requested amount is less than 8*parallelism
    private val memorySize = if (requestedMemorySize == 0U) {
        ((8 * parallelism) * 2).toUInt()
    } else {
        requestedMemorySize
    }
    private val blockCount = (memorySize / (4U * parallelism.toUInt())) * (4U * parallelism.toUInt())
    private val columnCount = (blockCount / parallelism.toUInt()).toInt()
    private val segmentLength = columnCount / 4

    private val useIndependentAddressing = argonType == ArgonType.Argon2id || argonType == ArgonType.Argon2i

    // State
    private val matrix: Argon2Matrix

    init {
        matrix = Argon2Matrix(columnCount, parallelism)
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



    private fun populateAddressBlock(
        iteration: Int,
        slice: Int,
        lane: Int,
        addressBlock: UByteArray,
        addressCounter: ULong
    ): UByteArray {
        //Calculate first pass
        val firstPass = compressionFunctionG(
            UByteArray(1024) { 0U },
            iteration.toULong().toLittleEndianUByteArray() +
                    lane.toULong().toLittleEndianUByteArray() +
                    slice.toULong().toLittleEndianUByteArray() +
                    blockCount.toULong().toLittleEndianUByteArray() +
                    numberOfIterations.toULong().toLittleEndianUByteArray() +
                    argonType.typeId.toULong().toLittleEndianUByteArray() +
                    addressCounter.toLittleEndianUByteArray() +
                    UByteArray(968) { 0U },
            addressBlock,
            false
        )
        val secondPass = compressionFunctionG(
            UByteArray(1024) { 0U },
            firstPass,
            firstPass,
            false
        )
        return secondPass
    }


    private fun computeReferenceBlockIndexes(
        iteration: Int,
        slice: Int,
        lane: Int,
        column: Int,
        addressBlock: UByteArray?
    ): Pair<Int, Int> {

        val segmentIndex = (column % segmentLength)
        val independentIndex = segmentIndex % 128 // 128 is the number of addresses in address block
        val (j1, j2) = when (argonType) {
            ArgonType.Argon2d -> {
                val (previousBlockStart, previousBlockEnd) = if (column == 0) {
                    matrix.getBlockStartAndEndPositions(lane, columnCount - 1) //Get last block in the SAME lane
                } else {
                    matrix.getBlockStartAndEndPositions(lane, column - 1)
                }
                val first32Bit = matrix.sliceArray(previousBlockStart until previousBlockStart + 4).fromLittleEndianArrayToUInt()
                val second32Bit = matrix.sliceArray(previousBlockStart + 4 until previousBlockStart + 8).fromLittleEndianArrayToUInt()

                Pair(first32Bit, second32Bit)
            }
            ArgonType.Argon2i -> {
                val selectedAddressBlock =
                    addressBlock!!.sliceArray((independentIndex * 8) until (independentIndex * 8) + 8)
                val first32Bit = selectedAddressBlock.sliceArray(0 until 4).fromLittleEndianArrayToUInt()
                val second32Bit = selectedAddressBlock.sliceArray(4 until 8).fromLittleEndianArrayToUInt()
                Pair(first32Bit, second32Bit)
            }
            ArgonType.Argon2id -> {
                if (iteration == 0 && (slice == 0 || slice == 1)) {
                    val selectedAddressBlock =
                        addressBlock!!.sliceArray((independentIndex * 8) until (independentIndex * 8) + 8)
                    val first32Bit = selectedAddressBlock.sliceArray(0 until 4).fromLittleEndianArrayToUInt()
                    val second32Bit = selectedAddressBlock.sliceArray(4 until 8).fromLittleEndianArrayToUInt()
                    Pair(first32Bit, second32Bit)
                } else {
                    val (previousBlockStart, previousBlockEnd) = if (column == 0) {
                        matrix.getBlockStartAndEndPositions(lane, columnCount - 1) //Get last block in the SAME lane
                    } else {
                        matrix.getBlockStartAndEndPositions(lane, column - 1)
                    }
                    val first32Bit = matrix.sliceArray(previousBlockStart until previousBlockStart + 4).fromLittleEndianArrayToUInt()
                    val second32Bit = matrix.sliceArray(previousBlockStart + 4 until previousBlockStart + 8).fromLittleEndianArrayToUInt()
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

    override fun derive(): UByteArray {
        val blakeInput = parallelism.toUInt().toLittleEndianUByteArray() +
                tagLength.toLittleEndianUByteArray() +
                memorySize.toLittleEndianUByteArray() +
                numberOfIterations.toUInt().toLittleEndianUByteArray() +
                versionNumber.toLittleEndianUByteArray() +
                argonType.typeId.toUInt().toLittleEndianUByteArray() +
                password.size.toUInt().toLittleEndianUByteArray() + password +
                salt.size.toUInt().toLittleEndianUByteArray() + salt +
                key.size.toUInt().toLittleEndianUByteArray() + key +
                associatedData.size.toUInt().toLittleEndianUByteArray() + associatedData
        val h0 = Blake2b.digest(
            blakeInput
        )

        //Compute B[i][0]
        for (i in 0 until parallelism) {
            matrix.setBlockAt(i, 0,
                argonBlake2bArbitraryLenghtHash(
                    (h0 + 0.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray()).toUByteArray(),
                    1024U
                )
            )
        }

        //Compute B[i][1]
        for (i in 0 until parallelism) {
            matrix.setBlockAt(i, 1,
                argonBlake2bArbitraryLenghtHash(
                    (h0 + 1.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray()).toUByteArray(),
                    1024U
                )
            )
        }
        //Run all iterations over all lanes and all segments
        executeArgonWithSingleThread()


        //Temporary fold
        val acc = matrix.getBlockAt(0, columnCount - 1).copyOf()
        for (i in 1 until parallelism) {
            (acc.xorWithBlock(matrix, i, columnCount - 1).copyInto(acc))
        }
        //Hash the xored last blocks
        val hash = argonBlake2bArbitraryLenghtHash(acc, tagLength)
        matrix.clearMatrix()
        return hash


    }

    private fun executeArgonWithSingleThread() {
        for (iteration in 0 until numberOfIterations) {
            for (slice in 0 until 4) {
                for (lane in 0 until parallelism) {
                    val segmentPosition = SegmentPosition(iteration, lane, slice)
                    processSegment(segmentPosition)
                }
            }
        }
    }

    private fun processSegment(segmentPosition: SegmentPosition) {
        val iteration = segmentPosition.iteration
        val slice = segmentPosition.slice
        val lane = segmentPosition.lane

        var addressBlock: UByteArray? = null
        var addressCounter = 1UL //Starts from 1 in each segment as defined by the spec

        //Generate initial segment address block
        if (useIndependentAddressing) {
            addressBlock = UByteArray(1024) {
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
            val segmentIndex = column - (slice * segmentLength)
            //Each address block contains 128 addresses, and we use one per iteration,
            //so once we do 128 iterations we need to calculate a new address block
            if (useIndependentAddressing && segmentIndex != 0 && segmentIndex % 128 == 0) {
                addressBlock = populateAddressBlock(iteration, slice, lane, addressBlock!!, addressCounter)
                addressCounter++
            }
            val previousColumn = if (column == 0) {
                columnCount - 1
            } else {
                column - 1
            }
            val (l, z) = computeReferenceBlockIndexes(
                iteration,
                slice,
                lane,
                column,
                addressBlock
            )

            matrix.setBlockAt(lane, column,
                compressionFunctionG(
                    matrix.getBlockAt(lane, previousColumn),
                    matrix.getBlockAt(l,z),
                    matrix.getBlockAt(lane,column),
                    true
                ).toUByteArray()
            )
        }

    }


}
