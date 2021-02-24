package com.ionspin.kotlin.crypto

import co.libly.resourceloader.FileLoader
import com.sun.jna.Native
import com.sun.jna.Platform
import java.io.File
import java.lang.RuntimeException

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 02/Aug/2020
 */
actual object LibsodiumInitializer {
    private var isPlatformInitialized = false

    private fun loadLibrary() : JnaLibsodiumInterface {
        val libraryFile = when {
            Platform.isMac() -> {
                FileLoader.get().load("dynamic-macos-x86-64.dylib", Any::class.java)
            }
            Platform.isLinux() -> {
                FileLoader.get().load("dynamic-linux-x86-64-libsodium.so", Any::class.java)
            }
            Platform.isWindows() -> {
                FileLoader.get().load("dynamic-msvc-x86-64-libsodium.dll", Any::class.java)
            }
            Platform.isAndroid() -> {
                when {
                    Platform.is64Bit() -> {
                        File("irrelevant")
                    }
                    else -> throw RuntimeException("Unsupported platform")
                }
            }
            else -> throw RuntimeException("Unknown platform")

        }


        val library = if (Platform.isAndroid()) {
            Native.load("sodium", JnaLibsodiumInterface::class.java) as JnaLibsodiumInterface
        } else {
            Native.load(libraryFile.absolutePath, JnaLibsodiumInterface::class.java) as JnaLibsodiumInterface
        }

        return library
    }


    lateinit var sodiumJna : JnaLibsodiumInterface
    actual suspend fun initialize() {
        sodiumJna = loadLibrary()
        isPlatformInitialized = true
    }

    actual fun initializeWithCallback(done: () -> Unit) {
        sodiumJna = loadLibrary()
        isPlatformInitialized = true
        done()
    }

    actual fun isInitialized(): Boolean {
        return isPlatformInitialized
    }

}
