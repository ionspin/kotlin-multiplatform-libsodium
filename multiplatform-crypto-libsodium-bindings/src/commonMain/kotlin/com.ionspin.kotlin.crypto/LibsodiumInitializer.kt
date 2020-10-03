package com.ionspin.kotlin.crypto

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 02/Aug/2020
 */
expect object LibsodiumInitializer {
    fun isInitialized() : Boolean

    suspend fun initialize()

    fun initializeWithCallback(done: () -> (Unit))
}
