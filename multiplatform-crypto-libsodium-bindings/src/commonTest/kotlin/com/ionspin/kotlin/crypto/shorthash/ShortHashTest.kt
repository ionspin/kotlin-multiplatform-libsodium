package com.ionspin.kotlin.crypto.shorthash

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.shortinputhash.ShortHash
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 28-Aug-2020
 */
class ShortHashTest {

    @Test
    fun testShortHash() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val expected = "00e5d509c14e81bb".hexStringToUByteArray()
            val input = "Libsodium test"
            val key = "key1key1key1key1"
            val hash = ShortHash.shortHash(input.encodeToUByteArray(), key.encodeToUByteArray())
            println(hash.toHexString())
            assertTrue {
                expected.contentEquals(hash)
            }
        }
    }

    @Test
    fun testKeygen() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            assertTrue {
                val key = ShortHash.shortHashKeygen()
                key.size == 16
            }
        }
    }
}
