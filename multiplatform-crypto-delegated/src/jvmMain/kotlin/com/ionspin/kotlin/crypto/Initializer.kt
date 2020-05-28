package com.ionspin.kotlin.crypto

actual object Initializer {
    actual suspend fun initialize() {
    }

    actual fun initializeWithCallback(done: () -> Unit) {
    }
}