/*
 * Copyright (c) 2019. Ugljesa Jovanovic
 */

package com.ionspin.crypto.blake2b

import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jul-2019
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Blake2BTest {

    @Test
    fun testMultipleBlocks() {
        val test = "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890"
        print("|$test|")
        val blake2b = Blake2b()

        val result = blake2b.digest(test)
        //Generated with b2sum 8.31
        val expectedResult = arrayOf<UByte>(
            //@formatter:off
            0x2fU, 0x49U, 0xaeU, 0xb6U, 0x13U, 0xe3U, 0x4eU, 0x92U, 0x4eU, 0x17U, 0x5aU, 0x6aU, 0xf2U, 0xfaU, 0xadU, 
            0x7bU, 0xc7U, 0x82U, 0x35U, 0xf9U, 0xc5U, 0xe4U, 0x61U, 0xc6U, 0x8fU, 0xd5U, 0xb4U, 0x07U, 0xeeU, 0x8eU, 
            0x2fU, 0x0dU, 0x2fU, 0xb4U, 0xc0U, 0x7dU, 0x7eU, 0x4aU, 0x72U, 0x40U, 0x46U, 0x12U, 0xd9U, 0x28U, 0x99U, 
            0xafU, 0x8aU, 0x32U, 0x8fU, 0x3bU, 0x61U, 0x4eU, 0xd7U, 0x72U, 0x44U, 0xb4U, 0x81U, 0x15U, 0x1dU, 0x40U, 
            0xb1U, 0x1eU, 0x32U, 0xa4U
            //@formatter:on
        )

        val printout = result.map { it.toString(16) }.chunked(16)
        printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }

        assertTrue {
            result.contentEquals(expectedResult)
        }
    }

    @Test
    fun singleBlockTest() {
        val test = "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890" +
                "1234567890"

        print("|$test|")
        val blake2b = Blake2b()

        val result = blake2b.digest(test)
        val expectedResultString = "800bb78cd4da18995c8074713bb674" +
                "3cd94b2b6490a693fe4000ed00833b88b7b474d94af9cfed246b1b" +
                "4ce1935a76154d7ea7c410493557741d18ec3a08da75"
        val expectedResult = expectedResultString.chunked(2).map { it.toUByte(16) }.toTypedArray()

        val printout = result.map { it.toString(16) }.chunked(16)
        printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }

        assertTrue {
            result.contentEquals(expectedResult)
        }
    }


    @Test
    fun testDigest() {
        val blake2b = Blake2b()
        val test = "111111111122222222223333333333333"

        val result = blake2b.digest(test)
        //Generated with b2sum 8.31
        val expectedResult = arrayOf<UByte>(
            //@formatter:off
            0xe0U, 0xabU, 0xb7U, 0x5dU, 0xb2U, 0xc8U, 0xe1U, 0x3cU, 0x5fU, 0x1dU, 0x9fU, 0x55U, 0xc8U, 0x4eU, 0xacU, 0xd7U,
            0xa8U, 0x44U, 0x57U, 0x9bU, 0xc6U, 0x9cU, 0x47U, 0x26U, 0xebU, 0xeaU, 0x2bU, 0xafU, 0x9eU, 0x44U, 0x16U, 0xebU,
            0xb8U, 0x0aU, 0xc5U, 0xfbU, 0xb0U, 0xe8U, 0xe5U, 0x6eU, 0xc5U, 0x49U, 0x0dU, 0x75U, 0x59U, 0x32U, 0x13U, 0xb4U,
            0x76U, 0x50U, 0x5eU, 0x6aU, 0xd8U, 0x74U, 0x67U, 0x14U, 0x64U, 0xb0U, 0xf8U, 0xb5U, 0x50U, 0x60U, 0x62U, 0xfbU
            //@formatter:on
        )

        val printout = result.map { it.toString(16) }.chunked(16)
        printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }

        assertTrue {
            result.contentEquals(expectedResult)
        }

    }

    @Test
    fun testFigestWithKey() {
        val blake2b = Blake2b()
        val test = "abc"
        val key = "key"

        val result = blake2b.digest(test, key)
        val printout = result.map { it.toString(16) }.chunked(16)
        printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }



        assertTrue {
            result.isNotEmpty()
        }
        val expectedResult = ("5c6a9a4ae911c02fb7e71a991eb9aea371ae993d4842d206e" +
                "6020d46f5e41358c6d5c277c110ef86c959ed63e6ecaaaceaaff38019a43264ae06acf73b9550b1")
            .chunked(2).map { it.toUByte(16) }.toTypedArray()

        assertTrue {
            result.contentEquals(expectedResult)
        }
    }


    @Test
    fun testDigestFromRfc() {
        val blake2b = Blake2b()
        val test = "abc"

        val result = blake2b.digest(test)
        //@formatter:off
        val expectedResult = arrayOf<UByte>(

            0xBAU,0x80U,0xA5U,0x3FU,0x98U,0x1CU,0x4DU,0x0DU,0x6AU,0x27U,0x97U,0xB6U,0x9FU,0x12U,0xF6U,0xE9U,
            0x4CU,0x21U,0x2FU,0x14U,0x68U,0x5AU,0xC4U,0xB7U,0x4BU,0x12U,0xBBU,0x6FU,0xDBU,0xFFU,0xA2U,0xD1U,
            0x7DU,0x87U,0xC5U,0x39U,0x2AU,0xABU,0x79U,0x2DU,0xC2U,0x52U,0xD5U,0xDEU,0x45U,0x33U,0xCCU,0x95U,
            0x18U,0xD3U,0x8AU,0xA8U,0xDBU,0xF1U,0x92U,0x5AU,0xB9U,0x23U,0x86U,0xEDU,0xD4U,0x00U,0x99U,0x23U

        )
        //@formatter:on
        val printout = result.map { it.toString(16) }.chunked(16)
        printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }

        assertTrue {
            result.contentEquals(expectedResult)
        }

    }

    val message = arrayOf(
        0x0000000000636261UL, 0x0000000000000000UL, 0x0000000000000000UL,
        0x0000000000000000UL, 0x0000000000000000UL, 0x0000000000000000UL,
        0x0000000000000000UL, 0x0000000000000000UL, 0x0000000000000000UL,
        0x0000000000000000UL, 0x0000000000000000UL, 0x0000000000000000UL,
        0x0000000000000000UL, 0x0000000000000000UL, 0x0000000000000000UL,
        0x0000000000000000UL
    )

    val mixChain = arrayOf(
        arrayOf(
            0x6A09E667F2BDC948UL, 0xBB67AE8584CAA73BUL, 0x3C6EF372FE94F82BUL,
            0xA54FF53A5F1D36F1UL, 0x510E527FADE682D1UL, 0x9B05688C2B3E6C1FUL,
            0x1F83D9ABFB41BD6BUL, 0x5BE0CD19137E2179UL, 0x6A09E667F3BCC908UL,
            0xBB67AE8584CAA73BUL, 0x3C6EF372FE94F82BUL, 0xA54FF53A5F1D36F1UL,
            0x510E527FADE682D2UL, 0x9B05688C2B3E6C1FUL, 0xE07C265404BE4294UL,
            0x5BE0CD19137E2179UL
        ),
        arrayOf(
            0x86B7C1568029BB79UL, 0xC12CBCC809FF59F3UL, 0xC6A5214CC0EACA8EUL,
            0x0C87CD524C14CC5DUL, 0x44EE6039BD86A9F7UL, 0xA447C850AA694A7EUL,
            0xDE080F1BB1C0F84BUL, 0x595CB8A9A1ACA66CUL, 0xBEC3AE837EAC4887UL,
            0x6267FC79DF9D6AD1UL, 0xFA87B01273FA6DBEUL, 0x521A715C63E08D8AUL,
            0xE02D0975B8D37A83UL, 0x1C7B754F08B7D193UL, 0x8F885A76B6E578FEUL,
            0x2318A24E2140FC64UL
        ),
        arrayOf(
            0x53281E83806010F2UL, 0x3594B403F81B4393UL, 0x8CD63C7462DE0DFFUL,
            0x85F693F3DA53F974UL, 0xBAABDBB2F386D9AEUL, 0xCA5425AEC65A10A8UL,
            0xC6A22E2FF0F7AA48UL, 0xC6A56A51CB89C595UL, 0x224E6A3369224F96UL,
            0x500E125E58A92923UL, 0xE9E4AD0D0E1A0D48UL, 0x85DF9DC143C59A74UL,
            0x92A3AAAA6D952B7FUL, 0xC5FDF71090FAE853UL, 0x2A8A40F15A462DD0UL,
            0x572D17EFFDD37358UL
        ),
        arrayOf(
            0x60ED96AA7AD41725UL, 0xE46A743C71800B9DUL, 0x1A04B543A01F156BUL,
            0xA2F8716E775C4877UL, 0xDA0A61BCDE4267EAUL, 0xB1DD230754D7BDEEUL,
            0x25A1422779E06D14UL, 0xE6823AE4C3FF58A5UL, 0xA1677E19F37FD5DAUL,
            0x22BDCE6976B08C51UL, 0xF1DE8696BEC11BF1UL, 0xA0EBD586A4A1D2C8UL,
            0xC804EBAB11C99FA9UL, 0x8E0CEC959C715793UL, 0x7C45557FAE0D4D89UL,
            0x716343F52FDD265EUL
        ),
        arrayOf(
            0xBB2A77D3A8382351UL, 0x45EB47971F23B103UL, 0x98BE297F6E45C684UL,
            0xA36077DEE3370B89UL, 0x8A03C4CB7E97590AUL, 0x24192E49EBF54EA0UL,
            0x4F82C9401CB32D7AUL, 0x8CCD013726420DC4UL, 0xA9C9A8F17B1FC614UL,
            0x55908187977514A0UL, 0x5B44273E66B19D27UL, 0xB6D5C9FCA2579327UL,
            0x086092CFB858437EUL, 0x5C4BE2156DBEECF9UL, 0x2EFEDE99ED4EFF16UL,
            0x3E7B5F234CD1F804UL
        ),
        arrayOf(
            0xC79C15B3D423B099UL, 0x2DA2224E8DA97556UL, 0x77D2B26DF1C45C55UL,
            0x8934EB09A3456052UL, 0x0F6D9EEED157DA2AUL, 0x6FE66467AF88C0A9UL,
            0x4EB0B76284C7AAFBUL, 0x299C8E725D954697UL, 0xB2240B59E6D567D3UL,
            0x2643C2370E49EBFDUL, 0x79E02EEF20CDB1AEUL, 0x64B3EED7BB602F39UL,
            0xB97D2D439E4DF63DUL, 0xC718E755294C9111UL, 0x1F0893F2772BB373UL,
            0x1205EA4A7859807DUL
        ),
        arrayOf(
            0xE58F97D6385BAEE4UL, 0x7640AA9764DA137AUL, 0xDEB4C7C23EFE287EUL,
            0x70F6F41C8783C9F6UL, 0x7127CD48C76A7708UL, 0x9E472AF0BE3DB3F6UL,
            0x0F244C62DDF71788UL, 0x219828AA83880842UL, 0x41CCA9073C8C4D0DUL,
            0x5C7912BC10DF3B4BUL, 0xA2C3ABBD37510EE2UL, 0xCB5668CC2A9F7859UL,
            0x8733794F07AC1500UL, 0xC67A6BE42335AA6FUL, 0xACB22B28681E4C82UL,
            0xDB2161604CBC9828UL
        ),
        arrayOf(
            0x6E2D286EEADEDC81UL, 0xBCF02C0787E86358UL, 0x57D56A56DD015EDFUL,
            0x55D899D40A5D0D0AUL, 0x819415B56220C459UL, 0xB63C479A6A769F02UL,
            0x258E55E0EC1F362AUL, 0x3A3B4EC60E19DFDCUL, 0x04D769B3FCB048DBUL,
            0xB78A9A33E9BFF4DDUL, 0x5777272AE1E930C0UL, 0x5A387849E578DBF6UL,
            0x92AAC307CF2C0AFCUL, 0x30AACCC4F06DAFAAUL, 0x483893CC094F8863UL,
            0xE03C6CC89C26BF92UL
        ),
        arrayOf(
            0xFFC83ECE76024D01UL, 0x1BE7BFFB8C5CC5F9UL, 0xA35A18CBAC4C65B7UL,
            0xB7C2C7E6D88C285FUL, 0x81937DA314A50838UL, 0xE1179523A2541963UL,
            0x3A1FAD7106232B8FUL, 0x1C7EDE92AB8B9C46UL, 0xA3C2D35E4F685C10UL,
            0xA53D3F73AA619624UL, 0x30BBCC0285A22F65UL, 0xBCEFBB6A81539E5DUL,
            0x3841DEF6F4C9848AUL, 0x98662C85FBA726D4UL, 0x7762439BD5A851BDUL,
            0xB0B9F0D443D1A889UL
        ),
        arrayOf(
            0x753A70A1E8FAEADDUL, 0x6B0D43CA2C25D629UL, 0xF8343BA8B94F8C0BUL,
            0xBC7D062B0DB5CF35UL, 0x58540EE1B1AEBC47UL, 0x63C5B9B80D294CB9UL,
            0x490870ECAD27DEBDUL, 0xB2A90DDF667287FEUL, 0x316CC9EBEEFAD8FCUL,
            0x4A466BCD021526A4UL, 0x5DA7F7638CEC5669UL, 0xD9C8826727D306FCUL,
            0x88ED6C4F3BD7A537UL, 0x19AE688DDF67F026UL, 0x4D8707AAB40F7E6DUL,
            0xFD3F572687FEA4F1UL
        ),
        arrayOf(
            0xE630C747CCD59C4FUL, 0xBC713D41127571CAUL, 0x46DB183025025078UL,
            0x6727E81260610140UL, 0x2D04185EAC2A8CBAUL, 0x5F311B88904056ECUL,
            0x40BD313009201AABUL, 0x0099D4F82A2A1EABUL, 0x6DD4FBC1DE60165DUL,
            0xB3B0B51DE3C86270UL, 0x900AEE2F233B08E5UL, 0xA07199D87AD058D8UL,
            0x2C6B25593D717852UL, 0x37E8CA471BEAA5F8UL, 0x2CFC1BAC10EF4457UL,
            0x01369EC18746E775UL
        ),
        arrayOf(
            0xE801F73B9768C760UL, 0x35C6D22320BE511DUL, 0x306F27584F65495EUL,
            0xB51776ADF569A77BUL, 0xF4F1BE86690B3C34UL, 0x3CC88735D1475E4BUL,
            0x5DAC67921FF76949UL, 0x1CDB9D31AD70CC4EUL, 0x35BA354A9C7DF448UL,
            0x4929CBE45679D73EUL, 0x733D1A17248F39DBUL, 0x92D57B736F5F170AUL,
            0x61B5C0A41D491399UL, 0xB5C333457E12844AUL, 0xBD696BE010D0D889UL,
            0x02231E1A917FE0BDUL
        ),
        arrayOf(
            0x12EF8A641EC4F6D6UL, 0xBCED5DE977C9FAF5UL, 0x733CA476C5148639UL,
            0x97DF596B0610F6FCUL, 0xF42C16519AD5AFA7UL, 0xAA5AC1888E10467EUL,
            0x217D930AA51787F3UL, 0x906A6FF19E573942UL, 0x75AB709BD3DCBF24UL,
            0xEE7CE1F345947AA4UL, 0xF8960D6C2FAF5F5EUL, 0xE332538A36B6D246UL,
            0x885BEF040EF6AA0BUL, 0xA4939A417BFB78A3UL, 0x646CBB7AF6DCE980UL,
            0xE813A23C60AF3B82UL
        )
    )

    @Test
    fun testMixRound() {
        val blake2b = Blake2b()

        for (i in 0 until mixChain.size - 1) {
            val inputRound = mixChain[i]
            val round = i
            val result = blake2b.mixRound(inputRound, message, round)
            println("Result: ")
            val printout = result.map { it.toString(16) }.chunked(3)
            printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
            val expectedResult = mixChain[i + 1]
            assertTrue {
                result.contentEquals(expectedResult)
            }
        }


    }
}