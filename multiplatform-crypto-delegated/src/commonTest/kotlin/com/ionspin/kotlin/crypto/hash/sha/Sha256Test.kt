package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.Initializer
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.testBlocking
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 07-Jun-2020
 */
class Sha256Test {
    @BeforeTest
    fun beforeTest() = testBlocking {
        Initializer.initialize()
    }

    @Test
    fun statelessSimpleTest() {
        val expected = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"
        val result = Crypto.Sha256.stateless("test".encodeToUByteArray()).toHexString()
//        println("Result: $result")
        assertTrue { result == expected }
    }

    //This is a bad test since it's not larger than one block
    //but for now I'm testing that the platform library is being correctly called
    @Test
    fun updateableSimpleTest() {
        val expected = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"
        val sha256 = Crypto.Sha256.updateable()
        sha256.update("t".encodeToUByteArray())
        sha256.update(("est".encodeToUByteArray()))
        val result = sha256.digest().toHexString()
//        println("Result: $result")
        assertTrue { result == expected }
    }
}