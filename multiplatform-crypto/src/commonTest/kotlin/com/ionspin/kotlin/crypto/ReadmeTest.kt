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

package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bPure
import com.ionspin.kotlin.crypto.hash.sha.Sha256Pure
import com.ionspin.kotlin.crypto.hash.sha.Sha512Pure
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2Pure
import com.ionspin.kotlin.crypto.keyderivation.argon2.ArgonType
import com.ionspin.kotlin.crypto.util.testBlocking
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Jul-2019
 */


class ReadmeTest {

    @Test
    fun blake2bObjectExample() {
        val input = "abc"
        val result = Blake2bPure.digest(input)
        //@formatter:off
        val expectedResult = ubyteArrayOf(
            0xBAU,0x80U,0xA5U,0x3FU,0x98U,0x1CU,0x4DU,0x0DU,0x6AU,0x27U,0x97U,0xB6U,0x9FU,0x12U,0xF6U,0xE9U,
            0x4CU,0x21U,0x2FU,0x14U,0x68U,0x5AU,0xC4U,0xB7U,0x4BU,0x12U,0xBBU,0x6FU,0xDBU,0xFFU,0xA2U,0xD1U,
            0x7DU,0x87U,0xC5U,0x39U,0x2AU,0xABU,0x79U,0x2DU,0xC2U,0x52U,0xD5U,0xDEU,0x45U,0x33U,0xCCU,0x95U,
            0x18U,0xD3U,0x8AU,0xA8U,0xDBU,0xF1U,0x92U,0x5AU,0xB9U,0x23U,0x86U,0xEDU,0xD4U,0x00U,0x99U,0x23U
        )
        //@formatter:on

        assertTrue {
            result.contentEquals(expectedResult)
        }
    }

    @Test
    fun blake2bInstanceExample() {
        val test = "abc"
        val key = "key"
        val blake2b = Blake2bPure(key)
        blake2b.update(test)
        val result = blake2b.digest()

        assertTrue {
            result.isNotEmpty()
        }
        val expectedResult = ("5c6a9a4ae911c02fb7e71a991eb9aea371ae993d4842d206e" +
                "6020d46f5e41358c6d5c277c110ef86c959ed63e6ecaaaceaaff38019a43264ae06acf73b9550b1")
            .chunked(2).map { it.toUByte(16) }.toUByteArray()

        assertTrue {
            result.contentEquals(expectedResult)
        }
    }


    @Test
    fun sha256Example() {
        val input = "abc"
        val result = Sha256Pure.digest(inputString = input)
        val expectedResult = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toUByteArray())
        }


    }


    @Test
    fun sha512Example() {
        val input = "abc"
        val result = Sha512Pure.digest(inputMessage = input.encodeToByteArray().map { it.toUByte() }.toUByteArray())
        println(result.map { it.toString(16) })
        val expectedResult = "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a" +
                "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toUByteArray())
        }


    }


    @Test
    fun sha256UpdatableExample() {
        val sha256 = Sha256Pure()
        sha256.update("abc")
        val result = sha256.digest()
        val expectedResult = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toUByteArray())
        }
    }


    @Test
    fun sha512UpdatableExample() {
        val sha512 = Sha512Pure()
        sha512.update("abc")
        val result = sha512.digest()
        val expectedResult = "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a" +
                "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toUByteArray())
        }


    }

    @ExperimentalTime
    @Test
    fun argon2StringExample() = testBlocking {
        val argon2Instance = Argon2Pure(
            password = "Password",
            salt = "RandomSalt",
            parallelism = 1,
            tagLength = 64U,
            requestedMemorySize = 4096U,
            numberOfIterations = 2,
            key = "",
            associatedData = "",
            argonType = ArgonType.Argon2id
        )
        val time = measureTime {
            val tag = argon2Instance.derive()
            val tagString = tag.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "")
            val expectedTagString = "c19db7e22d1480702b943872c863baf8c43b53d0c3e2c782cd07bfc613eda159233bd821a945c239c5085c70257f7c93d8a809f81c4af367f4ad8f0443a8fc47"
            println("Tag: ${tagString}")
            assertEquals(tagString, expectedTagString)
        }
        println("Time $time")

    }

    @Test
    fun debugTest() {
        val result = Blake2bStateless.digest("test")
        println(result.toHexString())
    }


}