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

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 05-Jan-2020
 */
@ExperimentalUnsignedTypes
class SRNGJsTest {

    @Test
    fun testJsSrng() {
        val bytes1 = SRNG.getRandomBytes(10)
        val bytes2 = SRNG.getRandomBytes(10)
        assertTrue {
            !bytes1.contentEquals(bytes2) &&
                    bytes1.size == 10 &&
                    bytes2.size == 10
        }

    }
}