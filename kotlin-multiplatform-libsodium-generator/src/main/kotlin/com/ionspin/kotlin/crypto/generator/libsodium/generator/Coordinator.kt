package com.ionspin.kotlin.crypto.generator.libsodium.generator

import com.ionspin.kotlin.crypto.generator.libsodium.definitions.LibSodiumDefinitions

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 31-Jul-2020
 */
object Coordinator {

    fun run(packageName: String) {
        CommonLibsodiumGenerator.createCommonFile(packageName, LibSodiumDefinitions.testKotlinFile)
        JvmLibsodiumGenerator.createJvmFile(packageName, LibSodiumDefinitions.testKotlinFile)
    }
}
