package com.ionspin.kotlin.crypto

import kotlinx.atomicfu.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import libsodium.sodium_init
import kotlin.native.concurrent.AtomicInt

actual object Initializer {

    private var isPlatformInitialized : AtomicInt = AtomicInt(0)

    actual suspend fun initialize() {
        GlobalScope.launch() {
            if (isPlatformInitialized.compareAndSet(0, 1)) {
                sodium_init()
            }
        }

    }

    actual fun initializeWithCallback(done: () -> Unit) {
        if (isPlatformInitialized.compareAndSet(0, 1)) {
            sodium_init()
        }
        done()
    }

    actual fun isInitialized(): Boolean {
        return isPlatformInitialized.value != 0
    }
}