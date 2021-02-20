package com.ionspin.kotlin.crypto.jnitest

import co.libly.resourceloader.FileLoader
import co.libly.resourceloader.ResourceLoader
import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.NativeLibrary
import com.sun.jna.Platform
import java.io.File
import java.lang.RuntimeException
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Feb-2021
 */
object JniTest {


    init {
        val libraryFile = when {
            Platform.isMac() -> {
                FileLoader.get().load("dynamic-macos-x86-64.dylib", Any::class.java)
            }
            Platform.isLinux() -> {
                FileLoader.get().load("dynamic-linux-x86-64-libsodium.so", Any::class.java)
            }
            Platform.isWindows() -> {
                FileLoader.get().load("dynamic-mingw-x86-64-libsodium.dll.a", Any::class.java)
            }
            Platform.isAndroid() -> {
                File("/home/ionspin/Projects/Future/kotlin-multiplatform-libsodium/multiplatform-crypto-libsodium-bindings/src/jvmMain/resources/dynamic-linux-x86-64-libsodium.so")
            }
            else -> throw RuntimeException("Unknown platform")

        }



        println(libraryFile.absoluteFile)
        val loaded = Native.load(libraryFile.absolutePath, SodiumVersion::class.java) as SodiumVersion
        val version = loaded.sodium_version_string()

        println("Loaded ${loaded.sodium_version_string()}")
        assertEquals("1.0.18", version)

    }

    fun work() {

    }
}

interface SodiumVersion : Library {
    fun sodium_version_string() : String
}
