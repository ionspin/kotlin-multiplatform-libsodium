package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
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
    @Test
    fun testIfLibraryIsNotOnFire() = testBlocking {
        Initializer.initialize()
        val crypto = Crypto()
        val state256 = crypto.crypto_hash_sha256_init_spec() //TODO seems to be a bug in JS compiler, if we have the same method name in crypto an in JsSodiumInterface, method tries to call wrong method name (unneeded suffix _0)
        crypto.crypto_hash_sha256_update(state256, "Hello".encodeToUByteArray())
        val result = crypto.crypto_hash_sha256_final(state256).toHexString()
        println("Result: $result")
        assertTrue {
            "185f8db32271fe25f561a6fc938b2e264306ec304eda518007d1764826381969" == result
        }


    }
}
