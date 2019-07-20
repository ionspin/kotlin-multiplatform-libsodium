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

package com.ionspin.kotlin.crypto.sha

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */
class Sha512Test {

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownValue() {

        val result = Sha512.digest(message = "abc".encodeToByteArray().map { it.toUByte() }.toTypedArray())
        println(result.map {it.toString(16)})
        val expectedResult = "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a" +
            "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }


    }

    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    @Test
    fun testWellKnownDoubleBlock() {

        val resultDoubleBlock = Sha512.digest(message = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmn" +
            "hijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu")
        val expectedResultForDoubleBlock = "8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018" +
            "501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownLong() {
        val inputBuilder = StringBuilder()
        for (i in 0 until 1000000) {
            inputBuilder.append("a")
        }
        val resultDoubleBlock = Sha512.digest(message = (inputBuilder.toString()).encodeToByteArray().map { it.toUByte() }.toTypedArray())
        val expectedResultForDoubleBlock = "e718483d0ce769644e2e42c7bc15b4638e1f98b13b2044285632a803afa973ebde0ff244877ea60a4cb0432ce577c31beb009c5c2c49aa2e4eadb217ad8cc09b"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }
}