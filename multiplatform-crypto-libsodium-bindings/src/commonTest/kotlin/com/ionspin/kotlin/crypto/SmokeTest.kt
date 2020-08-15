package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.testBlocking
import com.ionspin.kotlin.crypto.util.toHexString
import debug.test.Crypto
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 08-Aug-2020
 */
class SmokeTest {
    //TODO Browser ignores our testBlocking, node works fine though
    @Test
    fun testIfLibraryIsNotOnFire() {
        testBlocking {
            Initializer.initialize()
            val crypto = Crypto()
            //TODO seems to be a bug in JS compiler, if we have the same method name in crypto an in JsSodiumInterface, method tries to call wrong method name (unneeded suffix _0)
            //I've worked around this by making state functions with 1 parameter execute call with js("") wrap, but still might sail somewhere else
            val state256 = crypto.crypto_hash_sha256_init()
            crypto.crypto_hash_sha256_update(state256, "Hello".encodeToUByteArray())
            val result = crypto.crypto_hash_sha256_final(state256)
            val resultString = result.toHexString()
            println("Result: $resultString")
            assertTrue {
                "185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969" == resultString
            }

        }
    }
}
