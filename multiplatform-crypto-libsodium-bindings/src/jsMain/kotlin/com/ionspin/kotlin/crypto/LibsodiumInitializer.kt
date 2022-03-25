package com.ionspin.kotlin.crypto

import ext.libsodium.com.ionspin.kotlin.crypto.JsSodiumInterface
import ext.libsodium.com.ionspin.kotlin.crypto.JsSodiumLoader

var sodiumLoaded: Boolean = false

fun getSodium() : JsSodiumInterface = JsSodiumInterface

fun getSodiumLoaded() : Boolean = sodiumLoaded

fun setSodiumLoaded(loaded: Boolean) {
    sodiumLoaded = loaded
}

actual object LibsodiumInitializer {
    private var isPlatformInitialized = false

    actual suspend fun initialize() {
        JsSodiumLoader.load()
        isPlatformInitialized = true
    }

    actual fun initializeWithCallback(done: () -> Unit) {
        JsSodiumLoader.loadWithCallback {
            isPlatformInitialized = true
            done()
        }
    }

    actual fun isInitialized(): Boolean {
        return isPlatformInitialized
    }


}
