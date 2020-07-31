package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.LibSodiumDefinitions
import java.io.File

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
object Coordinator {

    fun run(packageName: String) {
        val commonFileSpec = CommonLibsodiumGenerator.createCommonFile(packageName, LibSodiumDefinitions.testKotlinFile)
        val jvmFileSpec = JvmLibsodiumGenerator.createJvmFile(packageName, LibSodiumDefinitions.testKotlinFile)
        val commonFile = File("multiplatform-crypto-libsodium-bindings/src/commonMain/kotlin/")
        commonFileSpec.writeTo(commonFile)
        val jvmFile = File("multiplatform-crypto-libsodium-bindings/src/jvmMain/kotlin/")
        jvmFileSpec.writeTo(jvmFile)
    }
}
