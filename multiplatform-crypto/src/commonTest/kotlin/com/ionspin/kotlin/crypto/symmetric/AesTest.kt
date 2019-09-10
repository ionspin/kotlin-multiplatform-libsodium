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
}