package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.GeneralLibsodiumException.Companion.ensureLibsodiumSuccess
import com.sun.jna.Native
import com.sun.jna.Platform
import java.io.File
import java.io.IOException
import java.lang.RuntimeException
import java.nio.file.Files
import java.util.UUID

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 02/Aug/2020
 */
actual object LibsodiumInitializer {
    private var isPlatformInitialized = false

    private fun loadLibrary(): JnaLibsodiumInterface {
        try {
            val libraryName = when {
                Platform.isMac() -> "libdynamic-macos.dylib"
                Platform.isLinux() -> {
                    if (Platform.isARM()) {
                        "libdynamic-linux-arm64-libsodium.so"
                    } else {
                        "libdynamic-linux-x86-64-libsodium.so"
                    }
                }

                Platform.isWindows() -> "libdynamic-msvc-x86-64-libsodium.dll"
                Platform.isAndroid() -> ""
                else -> throw RuntimeException("Unsupported platform")
            }

            return if (Platform.isAndroid()) {
                Native.load("sodium", JnaLibsodiumInterface::class.java) as JnaLibsodiumInterface
            } else {
                val libraryPath = extractLibraryPath(libraryName)
                Native.load(libraryPath, JnaLibsodiumInterface::class.java) as JnaLibsodiumInterface
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to load native library: ${e.message}", e)
        }
    }

    private fun extractLibraryPath(libraryName: String): String {
        try {
            val inputStream = LibsodiumInitializer::class.java.classLoader.getResourceAsStream(libraryName)
                ?: throw IOException("Library $libraryName not found in resources")

            val tempDir = System.getProperty("java.io.tmpdir")
            val tempFile = File(tempDir, "libsodium-${UUID.randomUUID()}-$libraryName")
            tempFile.deleteOnExit()

            Files.copy(inputStream, tempFile.toPath())
            return tempFile.absolutePath
        } catch (e: IOException) {
            throw RuntimeException("Failed to extract native library: $libraryName", e)
        }
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
