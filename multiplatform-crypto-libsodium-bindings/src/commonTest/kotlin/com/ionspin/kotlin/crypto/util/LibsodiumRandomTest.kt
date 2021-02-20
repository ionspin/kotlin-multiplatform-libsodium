package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 03-Oct-2020
 */
class LibsodiumRandomTest {

    @Test
    fun testRandom()= runTest {
        //This is just a sanity test, it should fail on occasion though, with probability of 1/2^32
        LibsodiumInitializer.initializeWithCallback {
            val random = LibsodiumRandom.random()
            assertTrue { random != 0U }
        }

    }

    @Test
    fun testRandomUniform() = runTest {
        //This is just a sanity test, it should fail on occasion though, with probability of 1/2^31
        LibsodiumInitializer.initializeWithCallback {
            val random = LibsodiumRandom.uniform(UInt.MAX_VALUE / 2U)
            assertTrue { random != 0U  && random < (UInt.MAX_VALUE / 2U)}
        }
    }

    @Test
    fun testRandomBuffer() = runTest {
        //This is just a sanity test, it should fail on occasion though, with probability of 1/2^52
        LibsodiumInitializer.initializeWithCallback {
            val result = LibsodiumRandom.buf(20)
            val lowProbability = UByteArray(20) { 0U }
            assertFalse { result.contentEquals(lowProbability) }
        }
    }

    @Test
    fun testRandomBufferDeterministic() = runTest {
        //This is just a sanity test, it should fail on occasion though, with probability of 1/2^52
        LibsodiumInitializer.initializeWithCallback {
            val seed = UByteArray(randombytes_SEEDBYTES) { 1U }
            val result = LibsodiumRandom.bufDeterministic(20, seed)
            val lowProbability = UByteArray(20) { 0U }
            assertFalse { result.contentEquals(lowProbability) }
            val secondResult = LibsodiumRandom.bufDeterministic(20, seed)
            assertTrue { result.contentEquals(secondResult) }
        }
    }
}
