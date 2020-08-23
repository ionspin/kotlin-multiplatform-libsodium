@file:Suppress("VARIABLE_IN_SINGLETON_WITHOUT_THREAD_LOCAL")

package com.ionspin.kotlin.crypto

import libsodium.sodium_init
import kotlin.native.concurrent.AtomicInt

actual object LibsodiumInitializer {

    private var isPlatformInitialized : AtomicInt = AtomicInt(0)

    actual suspend fun initialize() {
        if (isPlatformInitialized.compareAndSet(0, 1)) {
            sodium_init()
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
