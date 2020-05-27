package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.util.testBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-May-2020
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Blake2bJsTest {

    @BeforeTest
    fun setup() = testBlocking {
        Crypto.initialize()
    }

    @Test
    fun testBlake2BSodiumInterop() = testBlocking {
        Blake2bStateless.digest("test")
    }
}