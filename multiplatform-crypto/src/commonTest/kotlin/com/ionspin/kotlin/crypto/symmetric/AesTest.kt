package com.ionspin.kotlin.crypto.symmetric

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 10/Sep/2019
 */
@ExperimentalUnsignedTypes
class AesTest {

    @Test
    fun testSubBytes() {
        val fakeState = arrayOf(
                ubyteArrayOf(0x53U, 0U, 0U, 0U).toTypedArray(),
                ubyteArrayOf(0U, 0U, 0U, 0U).toTypedArray(),
                ubyteArrayOf(0U, 0U, 0U, 0U).toTypedArray(),
                ubyteArrayOf(0U, 0U, 0U, 0U).toTypedArray()
                )
        val aes = Aes()
        fakeState.copyInto(aes.stateMatrix)
        aes.subBytes()
        aes.stateMatrix.forEach{
            println(it.joinToString { it.toString(16) })
        }
        assertTrue {
            aes.stateMatrix[0][0] == 0xEDU.toUByte()
        }
    }

    @Test
    fun testShiftRows() {
        val fakeState = arrayOf(
                ubyteArrayOf(0U, 1U, 2U, 3U).toTypedArray(),
                ubyteArrayOf(0U, 1U, 2U, 3U).toTypedArray(),
                ubyteArrayOf(0U, 1U, 2U, 3U).toTypedArray(),
                ubyteArrayOf(0U, 1U, 2U, 3U).toTypedArray()
        )
        val expectedState = arrayOf(
                ubyteArrayOf(0U, 1U, 2U, 3U).toTypedArray(),
                ubyteArrayOf(1U, 2U, 3U, 0U).toTypedArray(),
                ubyteArrayOf(2U, 3U, 0U, 1U).toTypedArray(),
                ubyteArrayOf(3U, 0U, 1U, 2U).toTypedArray()
        )
        val aes = Aes()
        fakeState.copyInto(aes.stateMatrix)
        aes.shiftRows()
        aes.stateMatrix.forEach{
            println(it.joinToString { it.toString(16) })
        }
        assertTrue {
            aes.stateMatrix.contentDeepEquals(expectedState)
        }
    }

    @Test
    fun testGaloisMultiply() {
        val a = 0x57U
        val b = 0x83U
        val aes = Aes()
        val c = aes.galoisFieldMultiply(a.toUByte(), b.toUByte())
        assertTrue {
            c == 0xC1U.toUByte()
        }
    }
}