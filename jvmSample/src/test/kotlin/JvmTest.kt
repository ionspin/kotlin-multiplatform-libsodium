@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS", "EXPERIMENTAL_API_USAGE")

import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2
import com.ionspin.kotlin.crypto.keyderivation.argon2.ArgonType
import org.jetbrains.annotations.TestOnly
import org.junit.Test
import kotlin.test.assertTrue

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

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 19-May-2020
 */
@ExperimentalStdlibApi
class JvmTest {

    @Test
    fun testNothing() {
        JvmSample.nothing()
        assertTrue { true }

    }

    @Test
    fun argon2StringExample() {
        val argon2Instance = Argon2(
            password = "Password",
            salt = "RandomSalt",
            parallelism = 4,
            tagLength = 64U,
            requestedMemorySize = 25U * 1024U, //Travis build on mac fails with higher values
            numberOfIterations = 4,
            key = "",
            associatedData = "",
            argonType = ArgonType.Argon2id
        )
        val tag = argon2Instance.derive()
        val tagString = tag.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "")
        val expectedTagString = "ca134003c9f9f76ca8869359c1d9065603ec54ac30f5158f06af647cacaef2c1c3e" +
                "c71e81960278c0596febc64125acbbe5959146db1c128199a1b7cb38982a9"
        println("Tag: ${tagString}")
//        assertEquals(tagString, expectedTagString)

    }


}