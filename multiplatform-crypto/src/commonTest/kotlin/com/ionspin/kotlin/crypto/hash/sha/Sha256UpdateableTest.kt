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

package com.ionspin.kotlin.crypto.hash.sha

import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */
@ExperimentalUnsignedTypes
class Sha256UpdateableTest {

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownValue() {
        val sha256 = Sha256()
        sha256.update("abc")
        val result = sha256.digest()
        val expectedResult = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }


    }

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownDoubleBlock() {
        val sha256 = Sha256()
        sha256.update(data = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq")
        val resultDoubleBlock = sha256.digest()
        println(resultDoubleBlock.map{ it.toString(16)}.joinToString(separator = ""))
        val expectedResultForDoubleBlock = "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun testWellKnown3() { //It's good that I'm consistent with names.
        val sha256 = Sha256()
        sha256.update(data = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu")
        val resultDoubleBlock = sha256.digest()
        println(resultDoubleBlock.map{ it.toString(16)}.joinToString(separator = ""))
        val expectedResultForDoubleBlock = "cf5b16a778af8380036ce59e7b0492370b249b11e8f07a51afac45037afee9d1"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownLong() {
        val sha256 = Sha256()
        for (i in 0 until 10000) {
            sha256.update("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        }
        val resultDoubleBlock = sha256.digest()
        val expectedResultForDoubleBlock = "cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }

    @Ignore()
    @ExperimentalStdlibApi
    @Test
    fun testWellKnownLonger() {
        val sha256 = Sha256()
        for (i in 0 until 16_777_216) {
            if (i % 10000 == 0) {
                println("$i/16777216")
            }
            sha256.update("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmno")
        }
        val resultDoubleBlock = sha256.digest()
        val expectedResultForDoubleBlock = "50e72a0e26442fe2552dc3938ac58658228c0cbfb1d2ca872ae435266fcd055e"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }
    //50e72a0e 26442fe2 552dc393 8ac58658 228c0cbf b1d2ca87 2ae43526 6fcd055e
}