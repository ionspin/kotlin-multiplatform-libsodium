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

import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */

class Sha256UpdatableTest {


    @Test
    fun testWellKnownValue() {
        val sha256 = Sha256Pure()
        sha256.update("abc")
        val result = sha256.digest()
        val expectedResult = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        assertTrue {
            result.contentEquals(expectedResult.hexStringToUByteArray())
        }
    }


    @Test
    fun testWellKnownDoubleBlock() {
        val sha256 = Sha256Pure()
        sha256.update(data = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq")
        val resultDoubleBlock = sha256.digest()
        println(resultDoubleBlock.map{ it.toString(16)}.joinToString(separator = ""))
        val expectedResultForDoubleBlock = "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.hexStringToUByteArray())
        }
    }


    @Test
    fun testWellKnown3() { //It's good that I'm consistent with names.
        val sha256 = Sha256Pure()
        sha256.update(data = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu")
        val resultDoubleBlock = sha256.digest()
        println(resultDoubleBlock.map{ it.toString(16)}.joinToString(separator = ""))
        val expectedResultForDoubleBlock = "cf5b16a778af8380036ce59e7b0492370b249b11e8f07a51afac45037afee9d1"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.hexStringToUByteArray())
        }
    }


    @Test
    fun testWellKnownLong() {
        val sha256 = Sha256Pure()
        for (i in 0 until 10000) {
            sha256.update("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa")
        }
        val result = sha256.digest()
        val expectedResult = "cdc76e5c9914fb9281a1c7e284d73e67f1809a48a497200e046d39ccc7112cd0"
        assertTrue {
            result.contentEquals(expectedResult.hexStringToUByteArray())
        }
    }

    @Ignore // Takes too long on native and js, but it now finishes correctly (and surprisingly quickly) on JVM
    @Test
    fun testWellKnownLonger() {

        //Obtained from libsodium runs
        val roundsExpectedMap = mapOf(
            0 to "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            1 to "2ff100b36c386c65a1afc462ad53e25479bec9498ed00aa5a04de584bc25301b",
            2 to "a9161e25f5e45eddf9a4cfa8b23456ba66be8098e474dc145bfceb2a99808b89",
            3 to "7bd37dc113762cb012b69105b6302638d414ebfcabde205669aab2b8bbe1f3c4",
            4 to "8fa352dc79b2482d1b55777b4932aff67f52e87bafdf9b9d45d57405a672de7a",
            5 to "a229ab9b1bce3f3281b9d06a592aadb7e765029472a6b9ebf5ecd6c45a31f2f6",
            6 to "09b49600c40293a902e52f89478d4886cf5771faabe38f53a63f3e9051b23e32",
            7 to "02372493fd93330a8371f1dea20f5c29e5411baa1b08ba6cb33c6ca745c364a1",
            8 to "e38b73d311ac89575d4e69a965763cdc618d0879142574231268f2b014fd0fec",
            9 to "37ed309f368eef8560c8cbfb1d8b619f3cc5460689984b8c687f46373ff62087",
            0 to "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            1000000 to "7d9d6bcd4ffc9eb37899bbd7b5810e1762c55e4104f1b26b061349dbc67b5500",
            2000000 to "6b324148d357b3bb6549d339cec1a2a9ce45543eca0efcece78eecb3337097c2",
            3000000 to "3a52d0fb3383bca84dab55b2f41f0827d08325dbc65e3db4f15264597a8ac828",
            4000000 to "afd4587cf84fe7b6ffd7146a702d9b5dd4f0b754971eef36b7e0a6c4c3e84d58",
            5000000 to "d2b74e1a88652cce4450c3040decf40f9496ec92ad5e3443045922952db7894d",
            6000000 to "6e27932f14dfbbce11c9c9523e8b5a11616fd7864504d607639a98f81d5c0701",
            7000000 to "06e320b651981e13fb192805ff12a848815671d15885b636f99148b0089efabe",
            8000000 to "acc52c42ee92c046430f6e5454670e4f82da684a157eddd393a74c6883c881a5",
            9000000 to "493a7d3a40f3575bd5e5b4a5fec005f37392f3ce44565da314d173a062f499be",
            10000000 to "b3c6a178986ae0f5c81875ed68da2c72c8be961e9abc05e02982f723bd52a489",
            11000000 to "48387a098f9e919ea15ae5d1a347857f1043116e24bf9224b718120e0bfdaa9f",
            12000000 to "3ec9b9e64073f1c89f6d1c817a75610294d32c33d089af45540a352634e4a74a",
            13000000 to "cfb7c272408acdabf35c5cf1e2c0e2e81092d4e86bf8d51575a8a3baea2e7f8c",
            14000000 to "aa678e14a7d9a160177d52f153f30edd20c7f8a25fe36a3500fc1e11bd99029f",
            15000000 to "77c0a6256b17d6fdfd0926312ab168c6ac7357b11e93933e31f2f5fd5aefd400",
            16000000 to "28598fc683ac23a324e732ae398e093b5b80b03f01bdcf2f2d1b8e6bc76d183e",
            16777216 to "50e72a0e26442fe2552dc3938ac58658228c0cbfb1d2ca872ae435266fcd055e",
        )
        val encoded = "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmno".encodeToUByteArray()
        for (roundsExpected in roundsExpectedMap) {
            val sha256 = Sha256Pure()
            for (i in 0 until roundsExpected.key) {
                sha256.update(encoded)
            }
            val result = sha256.digest()
            assertTrue("Failed on ${roundsExpected.key} rounds, expected ${roundsExpected.value}, but got result ${result.toHexString()}") {
                result.contentEquals(roundsExpected.value.hexStringToUByteArray())
            }
        }

    }
}
