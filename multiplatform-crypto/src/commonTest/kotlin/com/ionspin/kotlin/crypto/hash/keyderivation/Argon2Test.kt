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

@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS", "EXPERIMENTAL_API_USAGE")

package com.ionspin.kotlin.crypto.hash.keyderivation

import com.ionspin.kotlin.crypto.keyderivation.Argon2Template
import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2
import com.ionspin.kotlin.crypto.keyderivation.argon2.ArgonType
import com.ionspin.kotlin.crypto.util.hexColumsPrint
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 10-May-2020
 */
@ExperimentalStdlibApi
class Argon2Test {

    @Test
    fun debugTest() {
        val memory = 32U //KiB
        val iterations = 3U
        val parallelism = 4U
        val tagLength = 32U
        val password: Array<UByte> = arrayOf(
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U
        )
        val salt: Array<UByte> =           arrayOf(0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U)
        val secret: Array<UByte> =         arrayOf(0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U)
        val associatedData: Array<UByte> = arrayOf(0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U)

        val digest = Argon2Template(
            password,
            salt,
            parallelism,
            tagLength,
            memory,
            iterations,
            0x13U,
            secret,
            associatedData,
            type = Argon2Template.ArgonType.Argon2d
        )
        val result = digest.calculate()
        result.hexColumsPrint(8)

    }

    @Test
    fun argon2dKATTest() {
        val expected : Array<UByte> = arrayOf(
            0x51U, 0x2BU, 0x39U, 0x1BU, 0x6FU, 0x11U, 0x62U, 0x97U,
            0x53U, 0x71U, 0xD3U, 0x09U, 0x19U, 0x73U, 0x42U, 0x94U,
            0xF8U, 0x68U, 0xE3U, 0xBEU, 0x39U, 0x84U, 0xF3U, 0xC1U,
            0xA1U, 0x3AU, 0x4DU, 0xB9U, 0xFAU, 0xBEU, 0x4AU, 0xCBU
        )


        val memory = 32U //KiB
        val iterations = 3U
        val parallelism = 4U
        val tagLength = 32U
        val password: Array<UByte> = arrayOf(
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U
        )
        val salt: Array<UByte> =           arrayOf(0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U)
        val secret: Array<UByte> =         arrayOf(0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U)
        val associatedData: Array<UByte> = arrayOf(0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U)

        val digest = Argon2(
            password,
            salt,
            parallelism.toInt(),
            tagLength,
            memory,
            iterations,
            secret,
            associatedData,
            ArgonType.Argon2d
        )
        val result = digest.derive()
        result.hexColumsPrint(8)
        assertTrue { expected.contentEquals(result) }

    }

    @Test
    fun argon2iKATTest() {
        val expected : Array<UByte> = arrayOf(
                    0xc8U, 0x14U, 0xd9U, 0xd1U, 0xdcU, 0x7fU, 0x37U, 0xaaU,
                    0x13U, 0xf0U, 0xd7U, 0x7fU, 0x24U, 0x94U, 0xbdU, 0xa1U,
                    0xc8U, 0xdeU, 0x6bU, 0x01U, 0x6dU, 0xd3U, 0x88U, 0xd2U,
                    0x99U, 0x52U, 0xa4U, 0xc4U, 0x67U, 0x2bU, 0x6cU, 0xe8U
        )


        val memory = 32U //KiB
        val iterations = 3U
        val parallelism = 4U
        val tagLength = 32U
        val password: Array<UByte> = arrayOf(
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U,
            0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U, 0x01U
        )
        val salt: Array<UByte> =           arrayOf(0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U, 0x02U)
        val secret: Array<UByte> =         arrayOf(0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U, 0x03U)
        val associatedData: Array<UByte> = arrayOf(0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U, 0x04U)

        val digest = Argon2(
            password,
            salt,
            parallelism.toInt(),
            tagLength,
            memory,
            iterations,
            secret,
            associatedData,
            ArgonType.Argon2i
        )
        val result = digest.derive()
        result.hexColumsPrint(8)
        assertTrue { expected.contentEquals(result) }

    }
}