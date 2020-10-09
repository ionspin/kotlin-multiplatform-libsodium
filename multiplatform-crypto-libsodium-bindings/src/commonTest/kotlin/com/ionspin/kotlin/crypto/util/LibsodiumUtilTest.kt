package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.bignum.integer.util.hexColumsPrint
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-Sep-2020
 */

class LibsodiumUtilTest {
    @Test
    fun testMemzero() {
        LibsodiumInitializer.initializeWithCallback {
            val input = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 32U, 64U, 128U, 255U)
            LibsodiumUtil.memzero(input)
            assertTrue {
                input.contentEquals(UByteArray(9) { 0U })
            }
        }
    }

    @Test
    fun testMemcmp() {
        LibsodiumInitializer.initializeWithCallback {
            val input = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 32U, 64U, 128U, 255U)
            val input2 = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 32U, 64U, 128U, 255U)
            val input3 = ubyteArrayOf(2U, 2U, 2U, 2U, 2U, 2U, 21U, 2U, 2U)
            assertTrue {
                LibsodiumUtil.memcmp(input, input2)
            }
            assertFalse {
                LibsodiumUtil.memcmp(input, input3)
            }
        }
    }

    @Test
    fun testPadding() {
        LibsodiumInitializer.initializeWithCallback {
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
    }

    @Test
    fun testPaddingChars() {
        LibsodiumInitializer.initializeWithCallback {
            val input = charArrayOf('a', 'b', 'c', 'd').map { it.toByte().toUByte() }.toUByteArray()
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

    @Test
    fun testPaddingAligned() {
        LibsodiumInitializer.initializeWithCallback {
            val input = ubyteArrayOf(1U, 2U)
            val blocksize = 2
            val padded = LibsodiumUtil.pad(input, blocksize)
            val expected = ubyteArrayOf(1U, 2U, 0x80U, 0x00U)
            println(padded.hexColumsPrint())
            assertTrue { padded.contentEquals(expected) }
            val unpadded = LibsodiumUtil.unpad(padded, blocksize)
            println(unpadded.hexColumsPrint())

            assertTrue {
                input.contentEquals(unpadded)
            }
        }
    }

    @Test
    fun testPaddingMultiblock() {
        LibsodiumInitializer.initializeWithCallback {
            val input = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 6U)
            val blocksize = 4
            val padded = LibsodiumUtil.pad(input, blocksize)
            val expected = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 6U, 0x80U, 0x00U)
            println(padded.hexColumsPrint())
            assertTrue { padded.contentEquals(expected) }
            val unpadded = LibsodiumUtil.unpad(padded, blocksize)
            println(unpadded.hexColumsPrint())

            assertTrue {
                input.contentEquals(unpadded)
            }
        }
    }

    @Test
    fun testToBase64() {
        LibsodiumInitializer.initializeWithCallback {
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
    }

    @Test
    fun testToBase64Unaligned() {
        LibsodiumInitializer.initializeWithCallback {
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
    }

    @Test
    fun toHex() {
        LibsodiumInitializer.initializeWithCallback {
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
}
