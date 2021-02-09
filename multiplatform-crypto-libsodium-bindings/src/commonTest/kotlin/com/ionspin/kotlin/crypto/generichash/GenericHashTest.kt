package com.ionspin.kotlin.crypto.generichash

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.testBlocking
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.test.BeforeTest
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 26-Aug-2020
 */
class GenericHashTest {


    @Test
    fun testGenericHash() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val inputString = "1234567890"
            val inputStringBuilder = StringBuilder()
            for (i in 0 until 14) {
                inputStringBuilder.append(inputString)
            }
            val input = inputStringBuilder.toString().encodeToUByteArray()
            val expectedResult = ubyteArrayOf(
                //@formatter:off
                0x2fU, 0x49U, 0xaeU, 0xb6U, 0x13U, 0xe3U, 0x4eU, 0x92U, 0x4eU, 0x17U, 0x5aU, 0x6aU, 0xf2U, 0xfaU, 0xadU,
                0x7bU, 0xc7U, 0x82U, 0x35U, 0xf9U, 0xc5U, 0xe4U, 0x61U, 0xc6U, 0x8fU, 0xd5U, 0xb4U, 0x07U, 0xeeU, 0x8eU,
                0x2fU, 0x0dU, 0x2fU, 0xb4U, 0xc0U, 0x7dU, 0x7eU, 0x4aU, 0x72U, 0x40U, 0x46U, 0x12U, 0xd9U, 0x28U, 0x99U,
                0xafU, 0x8aU, 0x32U, 0x8fU, 0x3bU, 0x61U, 0x4eU, 0xd7U, 0x72U, 0x44U, 0xb4U, 0x81U, 0x15U, 0x1dU, 0x40U,
                0xb1U, 0x1eU, 0x32U, 0xa4U
                //@formatter:on
            )


            val result = GenericHash.genericHash(input, 64)
            println("GenericHash result: ${result.toHexString()}")
            assertTrue {
                result.contentEquals(expectedResult)
            }
        }

    }

    @Test
    fun testGenericHashMultipart() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val updates = 14
            val input = "1234567890"
            val expectedResult = ubyteArrayOf(
                //@formatter:off
                0x2fU, 0x49U, 0xaeU, 0xb6U, 0x13U, 0xe3U, 0x4eU, 0x92U, 0x4eU, 0x17U, 0x5aU, 0x6aU, 0xf2U, 0xfaU, 0xadU,
                0x7bU, 0xc7U, 0x82U, 0x35U, 0xf9U, 0xc5U, 0xe4U, 0x61U, 0xc6U, 0x8fU, 0xd5U, 0xb4U, 0x07U, 0xeeU, 0x8eU,
                0x2fU, 0x0dU, 0x2fU, 0xb4U, 0xc0U, 0x7dU, 0x7eU, 0x4aU, 0x72U, 0x40U, 0x46U, 0x12U, 0xd9U, 0x28U, 0x99U,
                0xafU, 0x8aU, 0x32U, 0x8fU, 0x3bU, 0x61U, 0x4eU, 0xd7U, 0x72U, 0x44U, 0xb4U, 0x81U, 0x15U, 0x1dU, 0x40U,
                0xb1U, 0x1eU, 0x32U, 0xa4U
                //@formatter:on
            )

            val genericHashState = GenericHash.genericHashInit(64)
            for (i in 0 until updates) {
                GenericHash.genericHashUpdate(genericHashState, input.encodeToUByteArray())
            }
            val result = GenericHash.genericHashFinal(genericHashState)
            println("GenericHash result: ${result.toHexString()}")
            assertTrue {
                result.contentEquals(expectedResult)
            }
        }
    }
}
