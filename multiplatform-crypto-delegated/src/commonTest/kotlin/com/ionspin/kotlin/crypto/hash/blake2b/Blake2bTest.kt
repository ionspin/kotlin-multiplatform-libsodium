package com.ionspin.kotlin.crypto.hash.blake2b

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
 * on 09-Jun-2020
 */
class Blake2bTest {



    @Test
    fun statelessSimpleTest() = testBlocking {
        Initializer.initialize()
        val expected = "a71079d42853dea26e453004338670a53814b78137ffbed07603a41d76a483aa9bc33b582f77d30a65e6f29a89" +
                "6c0411f38312e1d66e0bf16386c86a89bea572"
        val result = Crypto.Blake2b.stateless("test".encodeToUByteArray()).toHexString()
//        println("Result: $result")
        assertTrue { result == expected }
    }

    //This is a bad test since it's not larger than one block
    //but for now I'm testing that the platform library is being correctly called
    @Test
    fun updateableSimpleTest() = testBlocking {
        Initializer.initialize()
        val expected = "a71079d42853dea26e453004338670a53814b78137ffbed07603a41d76a483aa9bc33b582f77d30a65e6f29a89" +
                "6c0411f38312e1d66e0bf16386c86a89bea572"
        val blake2b = Crypto.Blake2b.updateable()
        blake2b.update("t".encodeToUByteArray())
        blake2b.update(("est".encodeToUByteArray()))
        val result = blake2b.digest().toHexString()
//        println("Result: $result")
        assertTrue { result == expected }
    }
}