package com.ionspin.kotlin.crypto.generator

import com.ionspin.kotlin.crypto.generator.libsodium.generator.CommonLibsodiumGenerator
import com.ionspin.kotlin.crypto.generator.libsodium.definitions.LibSodiumDefinitions
import com.ionspin.kotlin.crypto.generator.libsodium.generator.Coordinator
import org.junit.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
class DebugTest {
    val packageName = "debug.test"
    @Test
    fun debugTest() {
        Coordinator.run(packageName)
    }
}
