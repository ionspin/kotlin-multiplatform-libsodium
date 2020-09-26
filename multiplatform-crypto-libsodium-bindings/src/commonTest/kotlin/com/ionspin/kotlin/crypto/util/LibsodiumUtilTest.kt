package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.bignum.integer.util.hexColumsPrint
import kotlin.math.exp
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

    @Test
    fun testToBase64() {
        val input = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 32U, 64U, 128U, 255U)
        val expected = "AQIDBAUgQID_"
        val output = LibsodiumUtil.toBase64(input)
        println("Output: |$output|")
        println("Expected|$expected| ")
        assertTrue {
            output == expected
        }
        val reconstructed = LibsodiumUtil.fromBase64(output)
        println("Reconstructed: ${reconstructed.toHexString()}")
        assertTrue {
            reconstructed.contentEquals(input)
        }
    }

    @Test
    fun testToBase64Unaligned() {
        val input = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 32U, 64U, 128U, 255U, 128U)
        val expected = "AQIDBAUgQID_gA"
        val output = LibsodiumUtil.toBase64(input)
        println("Output: |$output|")
        println("Expected|$expected| ")
        assertTrue {
            output == expected
        }
        val reconstructed = LibsodiumUtil.fromBase64(output)
        println("Reconstructed: ${reconstructed.toHexString()}")
        assertTrue {
            reconstructed.contentEquals(input)
        }
    }

    @Test
    fun toHex() {
        val input = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 32U, 64U, 128U, 255U, 128U)
        val hex = LibsodiumUtil.toHex(input)
        assertTrue {
            hex == input.toHexString()
        }
        val reconstructed = LibsodiumUtil.fromHex(hex)
        assertTrue {
            reconstructed.contentEquals(input)
        }
    }
}
