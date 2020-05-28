package com.ionspin.kotlin.crypto

actual object Initializer {
    actual suspend fun initialize() {
//        sodi
    }

    actual fun initializeWithCallback(done: () -> Unit) {
    }
}