package com.ionspin.kotlin.crypto.hash.blake2b

import kotlin.test.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-May-2020
 */
@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
class Blake2bJsTest {

    @Test
    fun testBlake2BSodiumInterop() {
        Blake2bStateless.digest("test")
    }
}