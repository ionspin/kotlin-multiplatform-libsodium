package com.ionspin.kotlin.crypto

import ext.libsodium.com.ionspin.kotlin.crypto.JsSodiumInterface
import ext.libsodium.com.ionspin.kotlin.crypto.JsSodiumLoader
/* 1.4-M1 has some weirdness with static/objects, or I'm misusing something, not sure */
lateinit var sodiumPointer : JsSodiumInterface
var sodiumLoaded: Boolean = false

fun getSodium() : JsSodiumInterface = sodiumPointer

//fun getSodiumAdvanced() : JsSodiumAdvancedInterface = js("sodiumPointer.libsodium")

fun setSodiumPointer(jsSodiumInterface: JsSodiumInterface) {
    js("sodiumPointer = jsSodiumInterface")
}

fun getSodiumLoaded() : Boolean = sodiumLoaded

fun setSodiumLoaded(loaded: Boolean) {
    js("sodiumLoaded = loaded")
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
