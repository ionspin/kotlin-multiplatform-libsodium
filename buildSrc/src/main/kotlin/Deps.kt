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
    val kotlinCoroutines = "1.3.9"
    val kotlin = "1.4.10"
    val kotlinSerialization = "1.0.0-RC"
    val atomicfu = "0.14.3-M2-2-SNAPSHOT" //NOTE: my linux arm32 and arm64 build
    val nodePlugin = "1.3.0"
    val dokkaPlugin = "1.4.0-rc"
    val taskTreePlugin = "1.5"

    val kotlinBigNumVersion = "0.1.6-1.4.0-rc-SNAPSHOT"

    val lazySodium = "4.3.1-SNAPSHOT"
    val jna = "5.5.0"

    val kotlinPoet = "1.6.0"


}

object ReleaseInfo {
    val group = "com.ionspin.kotlin"
    val version = "0.1.0-SNAPSHOT"
}

object Deps {

    object Common {
        val stdLib = "stdlib-common"
        val test = "test-common"
        val testAnnotation = "test-annotations-common"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-common:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-common:${Versions.kotlinSerialization}"
        val atomicfu = "com.ionspin.kotlin.atomicfu:atomicfu:${Versions.atomicfu}"


        val kotlinBigNum = "com.ionspin.kotlin:bignum:${Versions.kotlinBigNumVersion}"

        val apiProject = ":multiplatform-crypto-api"
    }

    object Js {
        val stdLib = "stdlib-js"
        val test = "test-js"
        val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${Versions.kotlinCoroutines}"
        val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime-js:${Versions.kotlinSerialization}"

        object Npm {
            val libsodium = Pair("libsodium-wrappers-sumo", "0.7.8")
            //val libsodiumWrappers = Pair("libsodium-wrappers-sumo", "file:${getProjectPath()}/multiplatform-crypto-delegated/libsodium-wrappers-sumo-0.7.6.tgz")
            val libsodiumWrappers = Pair("libsodium-wrappers-sumo", "0.7.8")
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

}


object PluginsDeps {
    val kotlinSerializationPlugin = "kotlinx-serialization"
    val multiplatform = "multiplatform"
    val node = "com.github.node-gradle.node"
    val mavenPublish = "maven-publish"
    val signing = "signing"
    val dokka = "org.jetbrains.dokka"
    val taskTree = "com.dorongold.task-tree"
    val androidLibrary = "com.android.library"
    val kotlinAndroidExtensions = "kotlin-android-extensions"
}

