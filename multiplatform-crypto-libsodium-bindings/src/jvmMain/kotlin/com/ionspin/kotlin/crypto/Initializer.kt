package com.ionspin.kotlin.crypto

import com.goterl.lazycode.lazysodium.SodiumJava

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 02/Aug/2020
 */
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