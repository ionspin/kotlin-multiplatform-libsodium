package com.ionspin.kotlin.crypto.sample.di

import kotlinx.serialization.json.Json

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Oct-2020
 */
object ServiceLocator : ServiceLocatorInterface {
    override val Storage: StorageModule = StorageModule.StorageServiceLocator()


}

interface ServiceLocatorInterface {
    val Storage: StorageModule
}

interface StorageModule {
    val json: Json

    class StorageServiceLocator : StorageModule {
        override val json = Json {
            prettyPrint = true
        }
    }
}
