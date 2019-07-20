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
@ExperimentalUnsignedTypes
class Sha256Test {

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownValue() {
        val sha = Sha256()

        val result = sha.digest(message = "abc".encodeToByteArray().map { it.toUByte() }.toTypedArray())
        println(result.map {it.toString(16)})
        val expectedResult = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
        assertTrue {
            result.contentEquals(expectedResult.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }


    }

    @ExperimentalStdlibApi
    @Test
    fun testWellKnownDoubleBlock() {
        val sha = Sha256()

        val resultDoubleBlock = sha.digest(message = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq".encodeToByteArray().map { it.toUByte() }.toTypedArray())
        val expectedResultForDoubleBlock = "248d6a61d20638b8e5c026930c3e6039a33ce45964ff2167f6ecedd419db06c1"
        assertTrue {
            resultDoubleBlock.contentEquals(expectedResultForDoubleBlock.chunked(2).map { it.toUByte(16) }.toTypedArray())
        }
    }
}