package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.rotateLeft
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
class Salsa20Test {

    @Test
    fun testRotateLeft() {
        val a = 0xc0a8787eU
        val b = a rotateLeft 5
        val expected  = 0x150f0fd8U
        assertEquals(b, expected)
    }

    @Test
    fun testCoreHash() {
        assertTrue {
            val input =    "00000000000000000000000000000000".hexStringToUByteArray()
            val expected = "00000000000000000000000000000000".hexStringToUByteArray()
            val result = Salsa20.coreHash(input)
            println("Result ${result.toHexString()}")

            expected.contentEquals(result)
        }

        assertTrue {
            val input =    "00000001000000000000000000000000".hexStringToUByteArray()
            val expected = "08008145000000800001020020500000".hexStringToUByteArray()
            val result = Salsa20.coreHash(input)
            println("Result ${result.toHexString()}")

            expected.contentEquals(result)
        }

        assertTrue {
            val input =    "00000000000000010000000000000000".hexStringToUByteArray()
            val expected = "88000100000000010000020000402000".hexStringToUByteArray()
            val result = Salsa20.coreHash(input)
            println("Result ${result.toHexString()}")

            expected.contentEquals(result)
        }

        assertTrue {
            val input =    "00000000000000000000000100000000".hexStringToUByteArray()
            val expected = "80040000000000000000000100002000".hexStringToUByteArray()
            val result = Salsa20.coreHash(input)
            println("Result ${result.toHexString()}")

            expected.contentEquals(result)
        }

        assertTrue {
            val input =    "00000000000000000000000000000001".hexStringToUByteArray()
            val expected = "00048044000000800001000020100001".hexStringToUByteArray()
            val result = Salsa20.coreHash(input)
            println("Result ${result.toHexString()}")

            expected.contentEquals(result)
        }


        assertTrue {
            val input =    "d3917c5b55f1c40752a58a7a8f887a3b".hexStringToUByteArray()
            val expected = "3e2f308cd90a8f366ab2a9232883524c".hexStringToUByteArray()
            val result = Salsa20.coreHash(input)
            println("Result ${result.toHexString()}")

            expected.contentEquals(result)
        }
    }
}