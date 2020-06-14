package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.util.fromLittleEndianToUInt
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
        val expected = 0x150f0fd8U
        assertEquals(b, expected)
    }

    @Test
    fun testQuarterRound() {
        assertTrue {
            val input = uintArrayOf(0U, 0U, 0U, 0U)
            val expected = uintArrayOf(0U, 0U, 0U, 0U)
            Salsa20.quarterRound(input, 0, 1, 2, 3)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")

            expected.contentEquals(input)
        }

        assertTrue {
            val input = uintArrayOf(1U, 0U, 0U, 0U)
            val expected = uintArrayOf(0x08008145U, 0x00000080U, 0x00010200U, 0x20500000U)
            Salsa20.quarterRound(input, 0, 1, 2, 3)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")

            expected.contentEquals(input)
        }

        assertTrue {
            val input = uintArrayOf(0U, 1U, 0U, 0U)
            val expected = uintArrayOf(0x88000100U, 0x00000001U, 0x00000200U, 0x00402000U)
            Salsa20.quarterRound(input, 0, 1, 2, 3)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")

            expected.contentEquals(input)
        }

        assertTrue {
            val input = uintArrayOf(0U, 0U, 1U, 0U)
            val expected = uintArrayOf(0x80040000U, 0x00000000U, 0x00000001U, 0x00002000U)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            Salsa20.quarterRound(input, 0, 1, 2, 3)
            expected.contentEquals(input)
        }

        assertTrue {
            val input = uintArrayOf(0U, 0U, 0U, 1U)
            val expected = uintArrayOf(0x00048044U, 0x00000080U, 0x00010000U, 0x20100001U)
            Salsa20.quarterRound(input, 0, 1, 2, 3)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")

            expected.contentEquals(input)
        }


        assertTrue {
            val input = uintArrayOf(0xd3917c5bU, 0x55f1c407U, 0x52a58a7aU, 0x8f887a3bU)
            val expected = uintArrayOf(0x3e2f308cU, 0xd90a8f36U, 0x6ab2a923U, 0x2883524cU)
            Salsa20.quarterRound(input, 0, 1, 2, 3)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            expected.contentEquals(input)
        }
    }

    @Test
    fun testRowRound() {
        assertTrue {
            val input = uintArrayOf(
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U
            )
            val expected = uintArrayOf(
                0x08008145U, 0x00000080U, 0x00010200U, 0x20500000U,
                0x20100001U, 0x00048044U, 0x00000080U, 0x00010000U,
                0x00000001U, 0x00002000U, 0x80040000U, 0x00000000U,
                0x00000001U, 0x00000200U, 0x00402000U, 0x88000100U
            )

            Salsa20.rowRound(input)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            expected.contentEquals(input)
        }
        assertTrue {

            val input = uintArrayOf(
                0x08521bd6U, 0x1fe88837U, 0xbb2aa576U, 0x3aa26365U,
                0xc54c6a5bU, 0x2fc74c2fU, 0x6dd39cc3U, 0xda0a64f6U,
                0x90a2f23dU, 0x067f95a6U, 0x06b35f61U, 0x41e4732eU,
                0xe859c100U, 0xea4d84b7U, 0x0f619bffU, 0xbc6e965aU
            )
            val expected = uintArrayOf(
                0xa890d39dU, 0x65d71596U, 0xe9487daaU, 0xc8ca6a86U,
                0x949d2192U, 0x764b7754U, 0xe408d9b9U, 0x7a41b4d1U,
                0x3402e183U, 0x3c3af432U, 0x50669f96U, 0xd89ef0a8U,
                0x0040ede5U, 0xb545fbceU, 0xd257ed4fU, 0x1818882dU
            )
            Salsa20.rowRound(input)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            expected.contentEquals(input)
        }
    }

    @Test
    fun testColumnRound() {
        assertTrue {
            val input = uintArrayOf(
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U
            )
            val expected = uintArrayOf(
                0x10090288U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000101U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00020401U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x40a04001U, 0x00000000U, 0x00000000U, 0x00000000U
            )

            Salsa20.columnRound(input)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            expected.contentEquals(input)
        }
        assertTrue {

            val input = uintArrayOf(
                0x08521bd6U, 0x1fe88837U, 0xbb2aa576U, 0x3aa26365U,
                0xc54c6a5bU, 0x2fc74c2fU, 0x6dd39cc3U, 0xda0a64f6U,
                0x90a2f23dU, 0x067f95a6U, 0x06b35f61U, 0x41e4732eU,
                0xe859c100U, 0xea4d84b7U, 0x0f619bffU, 0xbc6e965aU
            )
            val expected = uintArrayOf(
                0x8c9d190aU, 0xce8e4c90U, 0x1ef8e9d3U, 0x1326a71aU,
                0x90a20123U, 0xead3c4f3U, 0x63a091a0U, 0xf0708d69U,
                0x789b010cU, 0xd195a681U, 0xeb7d5504U, 0xa774135cU,
                0x481c2027U, 0x53a8e4b5U, 0x4c1f89c5U, 0x3f78c9c8U
            )
            Salsa20.columnRound(input)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            expected.contentEquals(input)
        }
    }

    @Test
    fun testDoubleRound() {
        assertTrue {
            val input = uintArrayOf(
                0x00000001U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000000U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000000U, 0x00000000U, 0x00000000U, 0x00000000U,
                0x00000000U, 0x00000000U, 0x00000000U, 0x00000000U
            )
            val expected = uintArrayOf(
                0x8186a22dU, 0x0040a284U, 0x82479210U, 0x06929051U,
                0x08000090U, 0x02402200U, 0x00004000U, 0x00800000U,
                0x00010200U, 0x20400000U, 0x08008104U, 0x00000000U,
                0x20500000U, 0xa0000040U, 0x0008180aU, 0x612a8020U
            )

            Salsa20.doubleRound(input)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            expected.contentEquals(input)
        }
        assertTrue {

            val input = uintArrayOf(
                0xde501066U, 0x6f9eb8f7U, 0xe4fbbd9bU, 0x454e3f57U,
                0xb75540d3U, 0x43e93a4cU, 0x3a6f2aa0U, 0x726d6b36U,
                0x9243f484U, 0x9145d1e8U, 0x4fa9d247U, 0xdc8dee11U,
                0x054bf545U, 0x254dd653U, 0xd9421b6dU, 0x67b276c1U
            )
            val expected = uintArrayOf(
                0xccaaf672U, 0x23d960f7U, 0x9153e63aU, 0xcd9a60d0U,
                0x50440492U, 0xf07cad19U, 0xae344aa0U, 0xdf4cfdfcU,
                0xca531c29U, 0x8e7943dbU, 0xac1680cdU, 0xd503ca00U,
                0xa74b2ad6U, 0xbc331c5cU, 0x1dda24c7U, 0xee928277U
            )
            Salsa20.doubleRound(input)
            println("Result ${input.joinToString { it.toString(16).padStart(2, '0') }}")
            expected.contentEquals(input)
        }
    }

    @Test
    fun littleEndianTest() {
        assertTrue {
            val input = ubyteArrayOf(86U, 75U, 30U, 9U)
            val expected = 0x091e4b56U
            val result = Salsa20.littleEndian(input, 0, 1, 2, 3)
            result == expected
        }

        assertTrue {
            val input = ubyteArrayOf(255U, 255U, 255U, 250U)
            val expected = 0xFAFFFFFFU
            val result = Salsa20.littleEndian(input, 0, 1, 2, 3)
            result == expected
        }
    }

    @Test
    fun littleEndianInvertedArrayTest() {
        assertTrue {
            val expected = ubyteArrayOf(86U, 75U, 30U, 9U)
            val input = uintArrayOf(0x091e4b56U)
            val result = UByteArray(4)
            Salsa20.littleEndianInverted(input, 0, result, 0)
            result.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(255U, 255U, 255U, 250U)
            val input = uintArrayOf(0xFAFFFFFFU)
            val result = UByteArray(4)
            Salsa20.littleEndianInverted(input, 0, result, 0)
            result.contentEquals(expected)
        }
    }

    @Test
    fun littleEndianInvertedTest() {
        assertTrue {
            val expected = ubyteArrayOf(86U, 75U, 30U, 9U)
            val input = 0x091e4b56U
            val result = UByteArray(4)
            Salsa20.littleEndianInverted(input, result, 0)
            result.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(255U, 255U, 255U, 250U)
            val input = 0xFAFFFFFFU
            val result = UByteArray(4)
            Salsa20.littleEndianInverted(input, result, 0)
            result.contentEquals(expected)
        }
    }

    @Test
    fun salsa20HashTest() {
        assertTrue {
            val input = ubyteArrayOf(
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U,
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U,
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U,
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U
            )
            val expected = ubyteArrayOf(
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U,
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U,
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U,
                0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U, 0U
            )
            val result = Salsa20.hash(input.fromLittleEndianToUInt())
            input.contentEquals(expected)
        }

        assertTrue {
            val input = ubyteArrayOf(
                211U, 159U, 13U, 115U, 76U, 55U, 82U, 183U, 3U, 117U, 222U, 37U, 191U, 187U, 234U, 136U,
                49U, 237U, 179U, 48U, 1U, 106U, 178U, 219U, 175U, 199U, 166U, 48U, 86U, 16U, 179U, 207U,
                31U, 240U, 32U, 63U, 15U, 83U, 93U, 161U, 116U, 147U, 48U, 113U, 238U, 55U, 204U, 36U,
                79U, 201U, 235U, 79U, 3U, 81U, 156U, 47U, 203U, 26U, 244U, 243U, 88U, 118U, 104U, 54U
            )
            val expected = ubyteArrayOf(
                109U, 42U, 178U, 168U, 156U, 240U, 248U, 238U, 168U, 196U, 190U, 203U, 26U, 110U, 170U, 154U,
                29U, 29U, 150U, 26U, 150U, 30U, 235U, 249U, 190U, 163U, 251U, 48U, 69U, 144U, 51U, 57U,
                118U, 40U, 152U, 157U, 180U, 57U, 27U, 94U, 107U, 42U, 236U, 35U, 27U, 111U, 114U, 114U,
                219U, 236U, 232U, 135U, 111U, 155U, 110U, 18U, 24U, 232U, 95U, 158U, 179U, 19U, 48U, 202U
            )
            val result = Salsa20.hash(input.fromLittleEndianToUInt())
            result.contentEquals(expected)
        }

        assertTrue {
            val input = ubyteArrayOf(
                6U, 124U, 83U, 146U, 38U, 191U, 9U, 50U, 4U, 161U, 47U, 222U, 122U, 182U, 223U, 185U,
                75U, 27U, 0U, 216U, 16U, 122U, 7U, 89U, 162U, 104U, 101U, 147U, 213U, 21U, 54U, 95U,
                225U, 253U, 139U, 176U, 105U, 132U, 23U, 116U, 76U, 41U, 176U, 207U, 221U, 34U, 157U, 108U,
                94U, 94U, 99U, 52U, 90U, 117U, 91U, 220U, 146U, 190U, 239U, 143U, 196U, 176U, 130U, 186U
            )
            val expected = ubyteArrayOf(
                8U, 18U, 38U, 199U, 119U, 76U, 215U, 67U, 173U, 127U, 144U, 162U, 103U, 212U, 176U, 217U,
                192U, 19U, 233U, 33U, 159U, 197U, 154U, 160U, 128U, 243U, 219U, 65U, 171U, 136U, 135U, 225U,
                123U, 11U, 68U, 86U, 237U, 82U, 20U, 155U, 133U, 189U, 9U, 83U, 167U, 116U, 194U, 78U,
                122U, 127U, 195U, 185U, 185U, 204U, 188U, 90U, 245U, 9U, 183U, 248U, 226U, 85U, 245U, 104U
            )
            val result = (0 until 1_000_000).fold(input) { acc, _ ->
                Salsa20.hash(acc.fromLittleEndianToUInt())
            }
            result.contentEquals(expected)
        }

    }

    @Test
    fun testExpansion() {
        val k0 = ubyteArrayOf(1U, 2U, 3U, 4U, 5U, 6U, 7U, 8U, 9U, 10U, 11U, 12U, 13U, 14U, 15U, 16U)
        val k1 =
            ubyteArrayOf(201U, 202U, 203U, 204U, 205U, 206U, 207U, 208U, 209U, 210U, 211U, 212U, 213U, 214U, 215U, 216U)
        val n =
            ubyteArrayOf(101U, 102U, 103U, 104U, 105U, 106U, 107U, 108U, 109U, 110U, 111U, 112U, 113U, 114U, 115U, 116U)

        assertTrue {
            val expected = ubyteArrayOf(
                69U, 37U, 68U, 39U, 41U, 15U, 107U, 193U, 255U, 139U, 122U, 6U, 170U, 233U, 217U, 98U,
                89U, 144U, 182U, 106U, 21U, 51U, 200U, 65U, 239U, 49U, 222U, 34U, 215U, 114U, 40U, 126U,
                104U, 197U, 7U, 225U, 197U, 153U, 31U, 2U, 102U, 78U, 76U, 176U, 84U, 245U, 246U, 184U,
                177U, 160U, 133U, 130U, 6U, 72U, 149U, 119U, 192U, 195U, 132U, 236U, 234U, 103U, 246U, 74U
            )
            val result = Salsa20.expansion32(k0 + k1, n)
            result.contentEquals(expected)
        }

        assertTrue {
            val expected = ubyteArrayOf(
                39U, 173U, 46U, 248U, 30U, 200U, 82U, 17U, 48U, 67U, 254U, 239U, 37U, 18U, 13U, 247U,
                241U, 200U, 61U, 144U, 10U, 55U, 50U, 185U, 6U, 47U, 246U, 253U, 143U, 86U, 187U, 225U,
                134U, 85U, 110U, 246U, 161U, 163U, 43U, 235U, 231U, 94U, 171U, 51U, 145U, 214U, 112U, 29U,
                14U, 232U, 5U, 16U, 151U, 140U, 183U, 141U, 171U, 9U, 122U, 181U, 104U, 182U, 177U, 193U
            )
            val result = Salsa20.expansion16(k0, n)
            result.contentEquals(expected)
        }
    }

    @Test
    fun testSalsa20Encryption() {
        assertTrue {
            val key = "8000000000000000000000000000000000000000000000000000000000000000".hexStringToUByteArray()
            val nonce = "0000000000000000".hexStringToUByteArray()
            val expectedStartsWith = (
                    "E3BE8FDD8BECA2E3EA8EF9475B29A6E7" +
                            "003951E1097A5C38D23B7A5FAD9F6844" +
                            "B22C97559E2723C7CBBD3FE4FC8D9A07" +
                            "44652A83E72A9C461876AF4D7EF1A117").toLowerCase()
            val endsWith = (
                    "696AFCFD0CDDCC83C7E77F11A649D79A" +
                            "CDC3354E9635FF137E929933A0BD6F53" +
                            "77EFA105A3A4266B7C0D089D08F1E855" +
                            "CC32B15B93784A36E56A76CC64BC8477"
                    ).toLowerCase()

            val ciphertext = Salsa20.encrypt(key, nonce, UByteArray(512) { 0U })
            ciphertext.toHexString().toLowerCase().startsWith(expectedStartsWith) &&
                    ciphertext.toHexString().toLowerCase().endsWith(endsWith)
        }

        assertTrue {
            val key = "0A5DB00356A9FC4FA2F5489BEE4194E73A8DE03386D92C7FD22578CB1E71C417".hexStringToUByteArray()
            val nonce = "1F86ED54BB2289F0".hexStringToUByteArray()
            val expectedStartsWith = (
                    "3FE85D5BB1960A82480B5E6F4E965A44" +
                            "60D7A54501664F7D60B54B06100A37FF" +
                            "DCF6BDE5CE3F4886BA77DD5B44E95644" +
                            "E40A8AC65801155DB90F02522B644023").toLowerCase()
            val endsWith = (
                    "7998204FED70CE8E0D027B206635C08C" +
                            "8BC443622608970E40E3AEDF3CE790AE" +
                            "EDF89F922671B45378E2CD03F6F62356" +
                            "529C4158B7FF41EE854B1235373988C8"
                    ).toLowerCase()

            val ciphertext = Salsa20.encrypt(key, nonce, UByteArray(131072) { 0U })
            println(ciphertext.slice(0 until 64).toTypedArray().toHexString())
                    println(ciphertext.slice(131008 until 131072).toTypedArray().toHexString())
            ciphertext.slice(0 until 64).toTypedArray().toHexString().toLowerCase().startsWith(expectedStartsWith) &&
                    ciphertext.slice(131008 until 131072).toTypedArray().toHexString().toLowerCase().contains(endsWith)
        }


    }


}