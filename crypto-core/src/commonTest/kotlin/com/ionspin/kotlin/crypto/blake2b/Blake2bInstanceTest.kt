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

package com.ionspin.kotlin.crypto.blake2b

import com.ionspin.kotlin.crypto.util.testBlocking
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Jul-2019
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Blake2bInstanceTest {

    @Test
    fun testUpdateableBlake2b() {
        val updates = 14
        val input = "1234567890"
        val expectedResult = arrayOf<UByte>(
            //@formatter:off
            0x2fU, 0x49U, 0xaeU, 0xb6U, 0x13U, 0xe3U, 0x4eU, 0x92U, 0x4eU, 0x17U, 0x5aU, 0x6aU, 0xf2U, 0xfaU, 0xadU,
            0x7bU, 0xc7U, 0x82U, 0x35U, 0xf9U, 0xc5U, 0xe4U, 0x61U, 0xc6U, 0x8fU, 0xd5U, 0xb4U, 0x07U, 0xeeU, 0x8eU,
            0x2fU, 0x0dU, 0x2fU, 0xb4U, 0xc0U, 0x7dU, 0x7eU, 0x4aU, 0x72U, 0x40U, 0x46U, 0x12U, 0xd9U, 0x28U, 0x99U,
            0xafU, 0x8aU, 0x32U, 0x8fU, 0x3bU, 0x61U, 0x4eU, 0xd7U, 0x72U, 0x44U, 0xb4U, 0x81U, 0x15U, 0x1dU, 0x40U,
            0xb1U, 0x1eU, 0x32U, 0xa4U
            //@formatter:on
        )

        val blake2b = Blake2b()
        for (i in 0 until updates) {
            blake2b.update(input)
        }
        val result = blake2b.digest()

        assertTrue {
            result.contentEquals(expectedResult)
        }
    }

    @Test
    fun testDigestToString() {
        val updates = 14
        val input = "1234567890"
        val expectedResult = "2F49AEB613E34E924E175A6AF2FAAD7BC78235F9C5E461C68FD5B47E".toLowerCase() +
                "E8E2FD2FB4C07D7E4A72404612D92899AF8A328F3B614ED77244B481151D40B11E32A4".toLowerCase()

        val blake2b = Blake2b()
        for (i in 0 until updates) {
            blake2b.update(input)
        }
        val result = blake2b.digestString()
        assertTrue {
            result == expectedResult
        }
    }

    @Test
    fun testDigestWithKey() {
        val test = "abc"
        val key = "key"
        val blake2b = Blake2b(key)
        blake2b.update(test)
        val result = blake2b.digest()

        assertTrue {
            result.isNotEmpty()
        }
        val expectedResult = ("5c6a9a4ae911c02fb7e71a991eb9aea371ae993d4842d206e" +
                "6020d46f5e41358c6d5c277c110ef86c959ed63e6ecaaaceaaff38019a43264ae06acf73b9550b1")
            .chunked(2).map { it.toUByte(16) }.toTypedArray()

        assertTrue {
            result.contentEquals(expectedResult)
        }
    }
}