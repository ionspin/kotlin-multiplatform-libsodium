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

package com.ionspin.kotlin.crypto.util

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */
@ExperimentalUnsignedTypes
class UtilTest {

    @Test
    fun testSlicer() {
        val array = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        val chunked = array.chunked(2)
        assertTrue {
            chunked.size == 9 && chunked[8][0] == 17
        }
    }

    @Test
    fun testUIntToBigEndianArray() {
        assertTrue {
            val original = 1U
            val converted = original.toBigEndianUByteArray()
            converted[0] = 1U
            true
        }
        assertTrue {
            val original = 0xAABBCCDDU
            val converted = original.toBigEndianUByteArray()
            converted[0] == 0xAAU.toUByte() &&
                    converted[1] == 0xBBU.toUByte() &&
                    converted[2] == 0xCCU.toUByte() &&
                    converted[3] == 0xDDU.toUByte()

        }
    }

    @Test
    fun testUIntToLittleEndianArray() {
        assertTrue {
            val original = 1U
            val converted = original.toBigEndianUByteArray()
            converted[4] = 1U
            true
        }
        assertTrue {
            val original = 0xAABBCCDDU
            val converted = original.toBigEndianUByteArray()
            converted[0] == 0xDDU.toUByte() &&
                    converted[1] == 0xCCU.toUByte() &&
                    converted[2] == 0xBBU.toUByte() &&
                    converted[3] == 0xAAU.toUByte()

        }
    }

    @Test
    fun testFromBigEndianByteArrayToLong() {

        assertTrue {
            val ubyteArray = ubyteArrayOf(0xA1U, 0xA2U, 0xB1U, 0xB2U, 0xC1U, 0xC2U, 0xD1U, 0xD2U).toTypedArray()
            val expected = 0xA1A2B1B2C1C2D1D2U
            val reconstructed = ubyteArray.fromBigEndianArrayToULong();
            reconstructed == expected
        }

    }

    @Test
    fun testFromLittleEndianByteArrayToLong() {

        assertTrue {
            val ubyteArray = ubyteArrayOf(0xA1U, 0xA2U, 0xB1U, 0xB2U, 0xC1U, 0xC2U, 0xD1U, 0xD2U).toTypedArray()
            val expected = 0xD2D1C2C1B2B1A2A1UL
            val reconstructed = ubyteArray.fromLittleEndianArrayToULong();
            reconstructed == expected
        }

    }
}