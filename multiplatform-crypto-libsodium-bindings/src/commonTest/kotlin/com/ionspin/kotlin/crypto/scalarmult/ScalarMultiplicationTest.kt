package com.ionspin.kotlin.crypto.scalarmult

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.toHexString
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 15-Oct-2020
 */
class ScalarMultiplicationTest {
    val aliceSecretKey = ubyteArrayOf(
        0x77U, 0x07U, 0x6dU, 0x0aU, 0x73U, 0x18U, 0xa5U, 0x7dU, 0x3cU, 0x16U, 0xc1U,
        0x72U, 0x51U, 0xb2U, 0x66U, 0x45U, 0xdfU, 0x4cU, 0x2fU, 0x87U, 0xebU, 0xc0U,
        0x99U, 0x2aU, 0xb1U, 0x77U, 0xfbU, 0xa5U, 0x1dU, 0xb9U, 0x2cU, 0x2aU
    )

    val bobSecretKey = ubyteArrayOf(
        0x5dU, 0xabU, 0x08U, 0x7eU, 0x62U, 0x4aU, 0x8aU, 0x4bU, 0x79U, 0xe1U, 0x7fU,
        0x8bU, 0x83U, 0x80U, 0x0eU, 0xe6U, 0x6fU, 0x3bU, 0xb1U, 0x29U, 0x26U, 0x18U,
        0xb6U, 0xfdU, 0x1cU, 0x2fU, 0x8bU, 0x27U, 0xffU, 0x88U, 0xe0U, 0xebU
    )


    val expectedAlicePublicKeyString = "8520f0098930a754748b7ddcb43ef75a0dbf3a0d26381af4eba4a98eaa9b4e6a"
    val expectedBobPublickKeyString = "de9edb7d7b7dc1b4d35b61c2ece435373f8343c85b78674dadfc7e146f882b4f"
    val expectedSharedSecretString = "4a5d9d5ba4ce2de1728e3bf480350f25e07e21c947d19e3376f09b3c1e161742"

    @Test
    fun testScalarMultiplication() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val alicePublicKey = ScalarMultiplication.scalarMultiplicationBase(aliceSecretKey)
            assertTrue {
                alicePublicKey.toHexString().equals(expectedAlicePublicKeyString)
            }
            val bobPublickKey = ScalarMultiplication.scalarMultiplicationBase(bobSecretKey)
            assertTrue {
                bobPublickKey.toHexString().equals(expectedBobPublickKeyString)
            }
            val aliceToBobSecret = ScalarMultiplication.scalarMultiplication(aliceSecretKey, bobPublickKey)
            val bobToAliceSecret = ScalarMultiplication.scalarMultiplication(bobSecretKey, alicePublicKey)
            assertTrue {
                aliceToBobSecret.toHexString().equals(expectedSharedSecretString)
            }
            assertTrue {
                bobToAliceSecret.toHexString().equals(expectedSharedSecretString)
            }
            println(aliceToBobSecret.toHexString())
        }
    }
}
