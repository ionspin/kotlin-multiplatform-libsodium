package com.ionspin.kotlin.crypto.hash.blake2b

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.testBlocking
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.coroutines.runBlocking

import kotlin.test.Test
import kotlin.test.assertTrue

class Sha512DelegatedLinuxTest {

    @Test
    fun testCinterop() {
        runBlocking {
            Crypto.initialize()
        }
    }

    @Test
    fun testBlake2bUpdateable() = testBlocking {
        val blake2b = Crypto.Blake2b.updateable()
        blake2b.update("test".encodeToUByteArray())
        val result = blake2b.digest().toHexString()
        println(result)
        assertTrue { result.length > 2 }
    }

    @Test
    fun testBlake2BStateless() = testBlocking {
        Blake2bDelegatedStateless.digest("test".encodeToUByteArray())
    }
}