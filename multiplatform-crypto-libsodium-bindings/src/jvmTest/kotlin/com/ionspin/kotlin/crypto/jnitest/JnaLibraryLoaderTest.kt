package com.ionspin.kotlin.crypto.jnitest

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna
import com.ionspin.kotlin.crypto.util.runTest
import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Feb-2021
 */
class JnaLibraryLoaderTest {

    @Test
    fun testIfLibraryIsLoaded() = runTest {
        LibsodiumInitializer.initialize()
        val version = sodiumJna.sodium_version_string()
        println("Got loaded sodium version: $version")
        assertEquals("1.0.18", version)
    }
}
