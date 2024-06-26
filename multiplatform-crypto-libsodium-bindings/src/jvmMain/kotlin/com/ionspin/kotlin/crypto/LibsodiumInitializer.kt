package com.ionspin.kotlin.crypto

import com.goterl.resourceloader.SharedLibraryLoader
import com.ionspin.kotlin.crypto.GeneralLibsodiumException.Companion.ensureLibsodiumSuccess
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
                SharedLibraryLoader.get().load("libdynamic-macos.dylib", JnaLibsodiumInterface::class.java)
            }
            Platform.isLinux() -> {
                if (Platform.isARM()) {
                    SharedLibraryLoader.get().load("libdynamic-linux-arm64-libsodium.so", JnaLibsodiumInterface::class.java)
                } else {
                    SharedLibraryLoader.get()
                        .load("libdynamic-linux-x86-64-libsodium.so", JnaLibsodiumInterface::class.java)
                }
            }
            Platform.isWindows() -> {
                SharedLibraryLoader.get().load("libdynamic-msvc-x86-64-libsodium.dll", JnaLibsodiumInterface::class.java)
            }
            Platform.isAndroid() -> {
                File("irrelevant")
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
        sodiumJna.sodium_init().ensureLibsodiumSuccess()
        isPlatformInitialized = true
    }

    actual fun initializeWithCallback(done: () -> Unit) {
        sodiumJna = loadLibrary()
        sodiumJna.sodium_init().ensureLibsodiumSuccess()
        isPlatformInitialized = true
        done()
    }

    actual fun isInitialized(): Boolean {
        return isPlatformInitialized
    }

}
