package com.ionspin.kotlin.crypto

import com.goterl.lazycode.lazysodium.SodiumJava

actual object Initializer {
    private var isPlatformInitialized = false

    lateinit var sodium : SodiumJava
    actual suspend fun initialize() {
        sodium = SodiumJava()
        isPlatformInitialized = true
    }

    actual fun initializeWithCallback(done: () -> Unit) {
        sodium = SodiumJava()
        isPlatformInitialized = true
        done()
    }

    actual fun isInitialized(): Boolean {
        return isPlatformInitialized
    }


}