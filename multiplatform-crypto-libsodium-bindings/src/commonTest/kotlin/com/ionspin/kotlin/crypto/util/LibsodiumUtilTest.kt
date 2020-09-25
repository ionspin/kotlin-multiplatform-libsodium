package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.bignum.integer.util.hexColumsPrint
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-Sep-2020
 */

class LibsodiumUtilTest {
    @Test
    fun testPadding() {
        val input = ubyteArrayOf(1U, 2U)
        val blocksize = 16
        val padded = LibsodiumUtil.pad(input, blocksize)
        println(padded.hexColumsPrint())
        val unpadded = LibsodiumUtil.unpad(padded, blocksize)
        println(unpadded.hexColumsPrint())

        assertTrue {
            input.contentEquals(unpadded)
        }
    }

    @Test
    fun testPaddingAligned() {
        val input = ubyteArrayOf(1U, 2U)
        val blocksize = 2
        val padded = LibsodiumUtil.pad(input, blocksize)
        println(padded.hexColumsPrint())
        val unpadded = LibsodiumUtil.unpad(padded, blocksize)
        println(unpadded.hexColumsPrint())

        assertTrue {
            input.contentEquals(unpadded)
        }
    }

    @Test
    fun testPaddingMultiblock() {
        val input = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 6U)
        val blocksize = 4
        val padded = LibsodiumUtil.pad(input, blocksize)
        println(padded.hexColumsPrint())
        val unpadded = LibsodiumUtil.unpad(padded, blocksize)
        println(unpadded.hexColumsPrint())

        assertTrue {
            input.contentEquals(unpadded)
        }
    }
}
