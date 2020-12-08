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
    val kotlinCoroutines = "1.4.1"
    val kotlin = "1.4.20"
    val kotlinSerialization = "1.0.1"
    val kotlinSerializationPlugin = "1.4.10"
    val atomicfu = "0.14.3-M2-2-SNAPSHOT" //NOTE: my linux arm32 and arm64 build
    val nodePlugin = "1.3.0"
    val dokkaPlugin = "1.4.0"
    val taskTreePlugin = "1.5"
    val kotlinBigNumVersion = "0.2.2"
    val lazySodium = "4.3.1-SNAPSHOT"
    val jna = "5.5.0"
    val kotlinPoet = "1.6.0"
    val libsodiumBindings = "0.1.1-SNAPSHOT"
    val ktor = "1.3.2"
    val timber = "4.7.1"
    val kodeinVersion = "7.1.0"



}

object ReleaseInfo {
    val group = "com.ionspin.kotlin"
    val version = "0.1.0-SNAPSHOT"
    val bindingsVersion = "0.1.1-SNAPSHOT"
}

object Deps {

    object Common {
        val stdLib = "stdlib-common"
        val test = "test-common"
        val testAnnotation = "test-annotations-common"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}"
        val atomicfu = "com.ionspin.kotlin.atomicfu:atomicfu:${Versions.atomicfu}"


        val kotlinBigNum = "com.ionspin.kotlin:bignum:${Versions.kotlinBigNumVersion}"

        val apiProject = ":multiplatform-crypto-api"

        val libsodiumBindings = "com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:${Versions.libsodiumBindings}"

        val kodein = "org.kodein.di:kodein-di:${Versions.kodeinVersion}"
    }

    object Js {

        object JsVersions {
            val react = "16.13.1-pre.124-kotlin-1.4.10"
            val reactNpm = "16.13.1"
            val styled = "5.2.0-pre.124-kotlin-1.4.10"
            val styledNpm = "1.0.0"

        }

        val stdLib = "stdlib-js"
        val test = "test-js"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinSerialization}"

        val ktorClient = "io.ktor:ktor-client-js:${Versions.ktor}"
        val ktorClientSerialization = "io.ktor:ktor-client-serialization-js:${Versions.ktor}"
        val ktorClientWebSockets = "io.ktor:ktor-client-websockets-js:${Versions.ktor}"

        object React {
            val react = "org.jetbrains:kotlin-react:${JsVersions.react}"
            val reactDom = "org.jetbrains:kotlin-react-dom:${JsVersions.react}"
            val styled = "org.jetbrains:kotlin-styled:${JsVersions.styled}"

        }

        object Npm {
            val libsodium = Pair("libsodium-wrappers-sumo", "0.7.8")
            //val libsodiumWrappers = Pair("libsodium-wrappers-sumo", "file:${getProjectPath()}/multiplatform-crypto-delegated/libsodium-wrappers-sumo-0.7.6.tgz")
            val libsodiumWrappers = Pair("libsodium-wrappers-sumo", "0.7.8")
            val reactPair = Pair("react", JsVersions.reactNpm)
            val reactDomPair = Pair("react-dom", JsVersions.reactNpm)
            val styledComponentsPair = Pair("styled-components", "5.2.0")
            val inlineStylePrefixesPair = Pair("inline-style-prefixer", "6.0.0")
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

        object Delegated {
            // Temporary until reported lazysodium issues are fixed. My snapshot build with
            // And cause I registered com.ionspin.kotlin as maven central package root now I have to use
            // that even though this is pure java library. :)
            val lazysodium = "com.ionspin.kotlin:lazysodium-java:${Versions.lazySodium}"
            val jna = "net.java.dev.jna:jna:${Versions.jna}"
        }
    }

    object iOs {
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"
    }

    object Native {
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-native:${Versions.kotlinSerialization}"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-native:${Versions.kotlinCoroutines}"

    }

    object Android {
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
        val ktorClientOkHttp = "io.ktor:ktor-client-okhttp:${Versions.ktor}"
        val ktorClient = "io.ktor:ktor-client-android:${Versions.ktor}"
        val ktorClientSerialization = "io.ktor:ktor-client-serialization-jvm:${Versions.ktor}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.kotlinSerialization}"
        val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    }

    object Desktop {
        val libui = "com.github.msink:libui:0.1.8"
    }

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

