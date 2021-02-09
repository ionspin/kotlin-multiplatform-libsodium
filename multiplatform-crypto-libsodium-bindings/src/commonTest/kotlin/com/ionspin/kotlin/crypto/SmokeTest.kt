package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.generichash.GenericHash
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.testBlocking
import com.ionspin.kotlin.crypto.util.toHexString
import com.ionspin.kotlin.crypto.util.runTest
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
            LibsodiumInitializer.initialize()
            val hashResult = GenericHash.genericHash("Hello".encodeToUByteArray(), 64)
            println("Smoke test: ${hashResult.toHexString()}")
            assertTrue {
                "EF15EAF92D5E335345A3E1D977BC7D8797C3D275717CC1B10AF79C93CDA01AEB2A0C59BC02E2BDF9380FD1B54EB9E1669026930CCC24BD49748E65F9A6B2EE68".toLowerCase() == hashResult.toHexString()
            }


        }
    }

}
