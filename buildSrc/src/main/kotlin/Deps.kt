/*
 *    Copyright 2019 Ugljesa Jovanovic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

object Versions {
    val kotlinCoroutines = "1.7.0-Beta"
    val kotlin = "1.8.10"
    val kotlinSerialization = "1.5.0"
    val kotlinSerializationPlugin = kotlin
    val nodePlugin = "1.3.0"
    val dokkaPlugin = kotlin
    val taskTreePlugin = "1.5"
    val kotlinBigNumVersion = "0.3.7"
    val jna = "5.13.0"
    val kotlinPoet = "1.6.0"
    val sampleLibsodiumBindings = "0.8.8-SNAPSHOT"
    val ktor = "1.3.2"
    val timber = "4.7.1"
    val kodeinVersion = "7.1.0"
    val resourceLoader = "2.0.2"




}

object ReleaseInfo {
    val group = "com.ionspin.kotlin"
    val bindingsVersion = "0.8.9-SNAPSHOT"
}

object Deps {

    object Common {
        val stdLib = "stdlib-common"
        val test = "test-common"
        val testAnnotation = "test-annotations-common"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}"

        val kotlinBigNum = "com.ionspin.kotlin:bignum:${Versions.kotlinBigNumVersion}"

        val apiProject = ":multiplatform-crypto-api"

        val libsodiumBindings = "com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:${Versions.sampleLibsodiumBindings}"

        val kodein = "org.kodein.di:kodein-di:${Versions.kodeinVersion}"
    }

    object Js {

        object JsVersions {
            val react = "17.0.2-pre.218-kotlin-1.5.21"
            val reactNpm = "17.0.2"
            val styled = "5.3.0-pre.218-kotlin-1.5.21"
            val styledNpm = "5.3.0"

        }

        val stdLib = "stdlib-js"
        val test = "test-js"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinSerialization}"

        val ktorClient = "io.ktor:ktor-client-js:${Versions.ktor}"
        val ktorClientSerialization = "io.ktor:ktor-client-serialization-js:${Versions.ktor}"
        val ktorClientWebSockets = "io.ktor:ktor-client-websockets-js:${Versions.ktor}"

        object Npm {
            val libsodium = Pair("libsodium-wrappers-sumo", "0.7.9")
            //val libsodiumWrappers = Pair("libsodium-wrappers-sumo", "file:${getProjectPath()}/multiplatform-crypto-delegated/libsodium-wrappers-sumo-0.7.6.tgz")
            val libsodiumWrappers = Pair("libsodium-wrappers-sumo", "0.7.9")

        }

    }

    object Jvm {
        val stdLib = "stdlib-jdk8"
        val test = "test"
        val testJUnit = "test-junit"
        val reflection = "reflect"
        val coroutinesCore = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        val coroutinesjdk8 = "org.jetbrains.kotlinx:kotlinx-coroutines-jdk8:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerialization}"
        val coroutinesTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.kotlinCoroutines}"

        val kotlinPoet = "com.squareup:kotlinpoet:${Versions.kotlinPoet}"

        val resourceLoader = "com.goterl:resource-loader:${Versions.resourceLoader}"

        object Delegated {
            val jna = "net.java.dev.jna:jna:${Versions.jna}"
        }
    }

    object iOs {
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"
    }

    object Native {
        val serialization = "org.jetbrains.kotli    nx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"

    }

    object Android {
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
        val ktorClientOkHttp = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        val ktorClient = "io.ktor:ktor-client-android:${Versions.ktor}"
        val ktorClientSerialization = "io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerialization}"
        val timber = "com.jakewharton.timber:timber:${Versions.timber}"
        val jna = "net.java.dev.jna:jna:${Versions.jna}@aar"
    }

    object Desktop {
        val libui = "com.github.msink:libui:0.1.8"
    }

}

object AndroidPluginConfiguration {
    val sdkVersion = 31
    val targetVersion = 31
    val minVersion = 24
}


object PluginsDeps {
    val kotlinSerializationPlugin = "plugin.serialization"
    val multiplatform = "multiplatform"
    val node = "com.github.node-gradle.node"
    val mavenPublish = "maven-publish"
    val signing = "signing"
    val dokka = "org.jetbrains.dokka"
    val taskTree = "com.dorongold.task-tree"
    val androidLibrary = "com.android.library"
    val kotlinAndroidExtensions = "kotlin-android-extensions"
    val androidApplication = "com.android.application"
    val kotlinAndroid = "kotlin-android"
    val kapt = "kotlin-kapt"
}

