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

import com.ionspin.kotlin.crypto.keyderivation.Argon2
import kotlin.test.Test

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

        val digest = Argon2(
            password,
            salt,
            parallelism,
            tagLength,
            memory,
            iterations,
            0x13U,
            secret,
            associatedData,
            type = Argon2.ArgonType.Argon2d
        )
        val result = digest.calculate()

    }
}