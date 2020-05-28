package com.ionspin.kotlin.crypto.hash.blake2b

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */

import com.ionspin.kotlin.crypto.util.testBlocking
import interop.*
import kotlinx.cinterop.*
import libsodium.*

import kotlin.test.Test

class Blake2bLinuxTest {

    @Test
    fun testCinterop() {
        val sodiumInitResult = sodium_init()
        println("Sodium init $sodiumInitResult")
        println("1")
    }

    @Test
    fun testBlake2BSodiumInterop() = testBlocking {
        Blake2bStateless.digest("test")
    }
}