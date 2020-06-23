package com.ionspin.kotlin.crypto

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
interface CryptoInitializer {
    suspend fun initialize()

    fun isInitialized() : Boolean


}
