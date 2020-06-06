package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.testBlocking
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-May-2020
 */


class Blake2bJsTest {

    @Test
    fun testBlake2BSodiumInterop() = testBlocking {
        Crypto.initialize()
        val hash = Blake2bDelegatedStateless.digest("test".encodeToUByteArray())
        assertEquals(hash.toHexString(), "a71079d42853dea26e453004338670a53814b78137ffbed07603a41d76a4" +
                "83aa9bc33b582f77d30a65e6f29a896c0411f38312e1d66e0bf16386c86a89bea572")
    }

}