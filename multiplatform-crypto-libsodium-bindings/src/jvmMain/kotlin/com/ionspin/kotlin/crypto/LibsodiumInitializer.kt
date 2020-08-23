package com.ionspin.kotlin.crypto

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 02/Aug/2020
 */
actual object LibsodiumInitializer {
    private var isPlatformInitialized = false

    lateinit var sodium : SodiumWrapper
    actual suspend fun initialize() {
        sodium = SodiumWrapper()
        isPlatformInitialized = true
    }

    actual fun initializeWithCallback(done: () -> Unit) {
        sodium = SodiumWrapper()
        isPlatformInitialized = true
        done()
    }

    actual fun isInitialized(): Boolean {
        return isPlatformInitialized
    }

}
