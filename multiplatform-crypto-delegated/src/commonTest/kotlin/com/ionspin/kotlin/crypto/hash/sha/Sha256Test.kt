package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 07-Jun-2020
 */
class Sha256Test {
    @Test
    fun statelessSimpleTest() {
        val expected = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"
        val result = Crypto.Sha256.stateless("test".encodeToUByteArray()).toHexString()
        println("Result: $result")
        assertTrue { result == expected }
    }
}