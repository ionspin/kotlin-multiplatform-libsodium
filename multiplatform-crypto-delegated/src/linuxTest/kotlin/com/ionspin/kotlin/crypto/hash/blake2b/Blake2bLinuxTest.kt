package com.ionspin.kotlin.crypto.hash.blake2b

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */

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
}