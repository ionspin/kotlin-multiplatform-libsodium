/*
 * Copyright (c) 2019. Ugljesa Jovanovic
 */

package com.ionspin.crypto.sha

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
        val sha = Sha512()

        val result = sha.digest(message = "abc".encodeToByteArray().map { it.toUByte() }.toTypedArray())
        println(result.map {it.toString(16)})
        val expectedResult = "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a" +
            "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }


    }

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownDoubleBlock() {
        val sha = Sha512()

        val resultDoubleBlock = sha.digest(message = ("abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmn" +
            "hijklmnoijklmnopjklmnopqklmnopqrlmnopqrsmnopqrstnopqrstu").encodeToByteArray().map { it.toUByte() }.toTypedArray())
        println(resultDoubleBlock.map {it.toString(16)})
        val expectedResultForDoubleBlock = "8e959b75dae313da8cf4f72814fc143f8f7779c6eb9f7fa17299aeadb6889018" +
            "501d289e4900f7e4331b99dec4b5433ac7d329eeb6dd26545e96e55b874be909"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownLong() {
        val sha = Sha512()
        val inputBuilder = StringBuilder()
        for (i in 0 until 1000000) {
            inputBuilder.append("a")
            if (i % 100000 == 0) {
                println("$i / 1000000")
            }
        }
        val resultDoubleBlock = sha.digest(message = (inputBuilder.toString()).encodeToByteArray().map { it.toUByte() }.toTypedArray())
        println(resultDoubleBlock.map {it.toString(16)})
        val expectedResultForDoubleBlock = "e718483d0ce769644e2e42c7bc15b4638e1f98b13b2044285632a803afa973ebde0ff244877ea60a4cb0432ce577c31beb009c5c2c49aa2e4eadb217ad8cc09b"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }
}