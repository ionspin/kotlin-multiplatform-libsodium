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
class Sha512Test {
    @Test
    fun statelessSimpleTest() {
        val expected = "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67" +
                "b143732c304cc5fa9ad8e6f57f50028a8ff"
        val result = Crypto.Sha512.stateless("test".encodeToUByteArray()).toHexString()
        println("Result: $result")
        assertTrue { result == expected }
    }

    //This is a bad test since it's not larger than one block
    //but for now I'm testing that the platform library is being correctly called
    @Test
    fun updateableSimpleTest() {
        val expected = "ee26b0dd4af7e749aa1a8ee3c10ae9923f618980772e473f8819a5d4940e0db27ac185f8a0e1d5f84f88bc887fd67" +
                "b143732c304cc5fa9ad8e6f57f50028a8ff"
        val sha512 = Crypto.Sha512.updateable()
        sha512.update("t".encodeToUByteArray())
        sha512.update(("est".encodeToUByteArray()))
        val result = sha512.digest().toHexString()
        println("Result: $result")
        assertTrue { result == expected }
    }
}