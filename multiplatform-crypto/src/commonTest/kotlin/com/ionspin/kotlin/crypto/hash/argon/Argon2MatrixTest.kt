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

package com.ionspin.kotlin.crypto.hash.argon

import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Matrix
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Utils
import com.ionspin.kotlin.crypto.keyderivation.argon2.Block
import com.ionspin.kotlin.crypto.util.arrayChunked
import com.ionspin.kotlin.crypto.util.fromLittleEndianArrayToULong
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-May-2020
 */
class Argon2MatrixTest {
    val zeroesBlock = UByteArray(1024) { 0U }
    val onesBlock = UByteArray(1024) { 1U }
    val twosBlock = UByteArray(1024) { 2U }
    val threesBlock = UByteArray(1024) { 3U }

    val random = Random(1)
    val randomBlockArray = random.nextUBytes(1024)


    @Test
    fun indexAccessTest() {
        val argon2Matrix = Argon2Matrix(2, 2)
        (zeroesBlock + onesBlock + twosBlock + threesBlock).copyInto(argon2Matrix.storage)
        println(argon2Matrix[0, 0, 0])
        println(argon2Matrix[0, 1, 0])
        println(argon2Matrix[1, 0, 0])
        println(argon2Matrix[1, 1, 0])
//        argon2Matrix.storage.hexColumsPrint(1024)
        var expectedByteValue = 0U.toUByte()
        for (lane in 0 until 2) {
            for (column in 0 until 2) {
                for (blockPosition in 0 until 1024) {
                    assertTrue {
                        argon2Matrix[lane, column, blockPosition] == expectedByteValue
                    }
                }
                expectedByteValue++
            }
        }
        assertTrue {
            argon2Matrix[0, 0, 0] == 0U.toUByte() &&
                    argon2Matrix[0, 1, 0] == 1U.toUByte() &&
                    argon2Matrix[1, 0, 0] == 2U.toUByte() &&
                    argon2Matrix[1, 1, 0] == 3U.toUByte()

        }
    }

    @Test
    fun blockRetrievalTest() {
        val argon2Matrix = Argon2Matrix(2, 2)
        (zeroesBlock + onesBlock + twosBlock + threesBlock).copyInto(argon2Matrix.storage)
        assertTrue {
            zeroesBlock.contentEquals(argon2Matrix.getBlockAt(0, 0)) &&
                    onesBlock.contentEquals(argon2Matrix.getBlockAt(0, 1)) &&
                    twosBlock.contentEquals(argon2Matrix.getBlockAt(1, 0)) &&
                    threesBlock.contentEquals(argon2Matrix.getBlockAt(1, 1))
        }
    }

    @Test
    fun blockColumnToUlongTest() {
        val randomBlock = Block(randomBlockArray)
        for (columnIndex in 0 until 8) {
            val startOfRow = (columnIndex * 8 * 16)
            val endOfRow = startOfRow + (8 * 16)
            val rowToMix = randomBlockArray.copyOfRange(startOfRow, endOfRow)
            val expected = rowToMix.arrayChunked(8).map { it.fromLittleEndianArrayToULong() }.toULongArray()

            val result = randomBlock.getRowOfULongsForMixing(columnIndex)

            assertTrue { expected.contentEquals(result) }
        }
    }

    @Test
    fun blockRowToULongTest() {
        val randomBlock = Block(randomBlockArray)
        val columnToMix = Argon2Utils.extractColumnFromGBlock(randomBlockArray, 0)
        val expected = columnToMix.arrayChunked(8).map { it.fromLittleEndianArrayToULong() }.toULongArray()
        val result = randomBlock.getColumnOfULongsForMixing(0)

        assertTrue { expected.contentEquals(result) }
    }

}