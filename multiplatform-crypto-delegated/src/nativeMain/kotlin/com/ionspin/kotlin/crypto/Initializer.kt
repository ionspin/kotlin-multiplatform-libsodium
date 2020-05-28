package com.ionspin.kotlin.crypto

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import libsodium.sodium_init

actual object Initializer {
    actual suspend fun initialize() {
        GlobalScope.launch {
            sodium_init()
        }

    }

    actual fun initializeWithCallback(done: () -> Unit) {
        sodium_init()
        done()
    }
}