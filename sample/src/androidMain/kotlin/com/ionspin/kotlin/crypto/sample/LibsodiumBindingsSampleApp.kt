/*
 * Copyright (c) 2020 Ugljesa Jovanovic (ugljesa.jovanovic@ionspin.com)
 */

package com.ionspin.kotlin.crypto.sample

import android.app.Application
import com.ionspin.kotlin.crypto.LibsodiumInitializer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/**
 * @author [Ugljesa Jovanovic](ugi@mobilabsolutions.com)
 */
class LibsodiumBindingsSampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val initialization = GlobalScope.async {
            LibsodiumInitializer.initialize()
        }
        runBlocking {
            initialization.await()
        }

    }
}
