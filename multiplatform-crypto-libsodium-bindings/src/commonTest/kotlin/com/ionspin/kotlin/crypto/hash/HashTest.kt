package com.ionspin.kotlin.crypto.hash

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.runTest
import com.ionspin.kotlin.crypto.util.toHexString
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Aug-2020
 */
class HashTest {
    //Not present in Lazy sodium
//    @Test
//    fun hashTest() {
//        = runTest {
//        LibsodiumInitializer.initializeWithCallback {
//            val input = ("Input for various hash functions").encodeToUByteArray()
//            val expected = ("34fcbcdcfe9e6aa3e6d5a64649afcfafb449c4b8435a65e5e7b7c2b6af3b04da350acee" +
//                    "838246d7c2637021def0c844fcb79ac42d6a50279f1078e535997b6e6").hexStringToUByteArray()
//
//            val result = Hash.hash(input)
//            assertTrue {
//                result.contentEquals(expected)
//            }
//        }
//
//    }


    @Test
    fun hashTestSha256() = runTest {
        LibsodiumInitializer.initialize()
            val input = ("Input for various hash functions").encodeToUByteArray()
            val expected = ("2bb078ec5993b5428355ba49bf030b1ac7" +
                    "1519e635aebc2f28124fac2aef9264").hexStringToUByteArray()

            val result = Hash.sha256(input)
            assertTrue {
                result.contentEquals(expected)
            }

            val sha256State = Hash.sha256Init()
            Hash.sha256Update(sha256State, input)
            val multipartResult = Hash.sha256Final(sha256State)
            assertTrue {
                multipartResult.contentEquals(expected)
            }


    }

    @Test
    fun hashTestSha512() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val input = ("Input for various hash functions").encodeToUByteArray()
            val expected = ("34fcbcdcfe9e6aa3e6d5a64649afcfafb449c4b8435a65e5e7b7c2b6af3b04da350acee" +
                    "838246d7c2637021def0c844fcb79ac42d6a50279f1078e535997b6e6").hexStringToUByteArray()

            val result = Hash.sha512(input)
            assertTrue {
                result.contentEquals(expected)
            }

            val sha512State = Hash.sha512Init()
            Hash.sha512Update(sha512State, input)
            val multipartResult = Hash.sha512Final(sha512State)
            assertTrue {
                multipartResult.contentEquals(expected)
            }
        }

    }


}
