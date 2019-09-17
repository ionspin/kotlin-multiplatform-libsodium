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
        val aes = Aes(AesKey.Aes128Key("2b7e151628aed2a6abf7158809cf4f3c"))
        fakeState.copyInto(aes.stateMatrix)
        aes.subBytes()
        aes.stateMatrix.forEach {
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
        val aes = Aes(AesKey.Aes128Key("2b7e151628aed2a6abf7158809cf4f3c"))
        fakeState.copyInto(aes.stateMatrix)
        aes.shiftRows()
        aes.stateMatrix.forEach {
            println(it.joinToString { it.toString(16) })
        }
        assertTrue {
            aes.stateMatrix.contentDeepEquals(expectedState)
        }
    }

    @Test
    fun testGaloisMultiply() {
        //Samples from FIPS-197
        assertTrue {
            val a = 0x57U
            val b = 0x83U
            val aes = Aes(AesKey.Aes128Key("2b7e151628aed2a6abf7158809cf4f3c"))
            val c = aes.galoisFieldMultiply(a.toUByte(), b.toUByte())
            c == 0xC1U.toUByte()
        }

        assertTrue {
            val a = 0x57U
            val b = 0x13U
            val aes = Aes(AesKey.Aes128Key("2b7e151628aed2a6abf7158809cf4f3c"))
            val c = aes.galoisFieldMultiply(a.toUByte(), b.toUByte())
            c == 0xFEU.toUByte()
        }


    }

    @Test
    fun testMixColumns() {
        //Test vectors from wikipedia
        val fakeState = arrayOf(
            ubyteArrayOf(0xdbU, 0xf2U, 0x01U, 0xc6U).toTypedArray(),
            ubyteArrayOf(0x13U, 0x0aU, 0x01U, 0xc6U).toTypedArray(),
            ubyteArrayOf(0x53U, 0x22U, 0x01U, 0xc6U).toTypedArray(),
            ubyteArrayOf(0x45U, 0x5cU, 0x01U, 0xc6U).toTypedArray()
        )

        val expectedState = arrayOf(
            ubyteArrayOf(0x8eU, 0x9fU, 0x01U, 0xc6U).toTypedArray(),
            ubyteArrayOf(0x4dU, 0xdcU, 0x01U, 0xc6U).toTypedArray(),
            ubyteArrayOf(0xa1U, 0x58U, 0x01U, 0xc6U).toTypedArray(),
            ubyteArrayOf(0xbcU, 0x9dU, 0x01U, 0xc6U).toTypedArray()
        )

        val aes = Aes(AesKey.Aes128Key("2b7e151628aed2a6abf7158809cf4f3c"))
        fakeState.copyInto(aes.stateMatrix)
        aes.mixColumns()
        aes.stateMatrix.forEach {
            println(it.joinToString { it.toString(16) })
        }
        assertTrue {
            aes.stateMatrix.contentDeepEquals(expectedState)
        }

    }

    @Test
    fun testKeyExpansion() {
        assertTrue {
            val key = "2b7e151628aed2a6abf7158809cf4f3c"
            val expectedExpandedKey = uintArrayOf(
                // @formatter:off
                0x2b7e1516U, 0x28aed2a6U, 0xabf71588U, 0x09cf4f3cU, 0xa0fafe17U, 0x88542cb1U,
                0x23a33939U, 0x2a6c7605U, 0xf2c295f2U, 0x7a96b943U, 0x5935807aU, 0x7359f67fU,
                0x3d80477dU, 0x4716fe3eU, 0x1e237e44U, 0x6d7a883bU, 0xef44a541U, 0xa8525b7fU,
                0xb671253bU, 0xdb0bad00U, 0xd4d1c6f8U, 0x7c839d87U, 0xcaf2b8bcU, 0x11f915bcU,
                0x6d88a37aU, 0x110b3efdU, 0xdbf98641U, 0xca0093fdU, 0x4e54f70eU, 0x5f5fc9f3U,
                0x84a64fb2U, 0x4ea6dc4fU, 0xead27321U, 0xb58dbad2U, 0x312bf560U, 0x7f8d292fU,
                0xac7766f3U, 0x19fadc21U, 0x28d12941U, 0x575c006eU, 0xd014f9a8U, 0xc9ee2589U,
                0xe13f0cc8U, 0xb6630ca6U
            // @formatter:on
            ).toTypedArray()


            val aes = Aes(AesKey.Aes128Key(key))
            val result = aes.expandedKey.map {
                it.foldIndexed(0U) { index, acc, uByte ->
                    acc + (uByte.toUInt() shl (24 - index * 8))
                }
            }.toTypedArray()
            expectedExpandedKey.contentEquals(result)
        }

        assertTrue {
            val key = "8e73b0f7da0e6452c810f32b809079e562f8ead2522c6b7b"
            val expectedExpandedKey = uintArrayOf(
                // @formatter:off
                0x8e73b0f7U, 0xda0e6452U, 0xc810f32bU, 0x809079e5U, 0x62f8ead2U, 0x522c6b7bU,
                0xfe0c91f7U, 0x2402f5a5U, 0xec12068eU, 0x6c827f6bU, 0x0e7a95b9U, 0x5c56fec2U, 0x4db7b4bdU, 0x69b54118U,
                0x85a74796U, 0xe92538fdU, 0xe75fad44U, 0xbb095386U, 0x485af057U, 0x21efb14fU, 0xa448f6d9U, 0x4d6dce24U,
                0xaa326360U, 0x113b30e6U, 0xa25e7ed5U, 0x83b1cf9aU, 0x27f93943U, 0x6a94f767U, 0xc0a69407U, 0xd19da4e1U,
                0xec1786ebU, 0x6fa64971U, 0x485f7032U, 0x22cb8755U, 0xe26d1352U, 0x33f0b7b3U, 0x40beeb28U, 0x2f18a259U,
                0x6747d26bU, 0x458c553eU, 0xa7e1466cU, 0x9411f1dfU, 0x821f750aU, 0xad07d753U, 0xca400538U, 0x8fcc5006U,
                0x282d166aU, 0xbc3ce7b5U, 0xe98ba06fU, 0x448c773cU, 0x8ecc7204U, 0x01002202U
            // @formatter:on
            ).toTypedArray()


            val aes = Aes(AesKey.Aes192Key(key))
            val result = aes.expandedKey.map {
                it.foldIndexed(0U) { index, acc, uByte ->
                    acc + (uByte.toUInt() shl (24 - index * 8))
                }
            }.toTypedArray()
            expectedExpandedKey.contentEquals(result)
        }

        assertTrue {
            val key = "603deb1015ca71be2b73aef0857d77811f352c073b6108d72d9810a30914dff4"

            val expectedExpandedKey = uintArrayOf(
                // @formatter:off
                0x603deb10U, 0x15ca71beU, 0x2b73aef0U, 0x857d7781U, 0x1f352c07U, 0x3b6108d7U, 0x2d9810a3U, 0x0914dff4U,
                0x9ba35411U, 0x8e6925afU, 0xa51a8b5fU, 0x2067fcdeU, 0xa8b09c1aU, 0x93d194cdU, 0xbe49846eU, 0xb75d5b9aU,
                0xd59aecb8U, 0x5bf3c917U, 0xfee94248U, 0xde8ebe96U, 0xb5a9328aU, 0x2678a647U, 0x98312229U, 0x2f6c79b3U,
                0x812c81adU, 0xdadf48baU, 0x24360af2U, 0xfab8b464U, 0x98c5bfc9U, 0xbebd198eU, 0x268c3ba7U, 0x09e04214U,
                0x68007bacU, 0xb2df3316U, 0x96e939e4U, 0x6c518d80U, 0xc814e204U, 0x76a9fb8aU, 0x5025c02dU, 0x59c58239U,
                0xde136967U, 0x6ccc5a71U, 0xfa256395U, 0x9674ee15U, 0x5886ca5dU, 0x2e2f31d7U, 0x7e0af1faU, 0x27cf73c3U,
                0x749c47abU, 0x18501ddaU, 0xe2757e4fU, 0x7401905aU, 0xcafaaae3U, 0xe4d59b34U, 0x9adf6aceU, 0xbd10190dU,
                0xfe4890d1U, 0xe6188d0bU, 0x046df344U, 0x706c631eU
            // @formatter:on
            ).toTypedArray()


            val aes = Aes(AesKey.Aes256Key(key))
            val result = aes.expandedKey.map {
                it.foldIndexed(0U) { index, acc, uByte ->
                    acc + (uByte.toUInt() shl (24 - index * 8))
                }
            }.toTypedArray()
            expectedExpandedKey.contentEquals(result)
        }


    }
}