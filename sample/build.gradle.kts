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
 *
 */

@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest

plugins {
    kotlin(PluginsDeps.multiplatform)
    id (PluginsDeps.mavenPublish)
    id (PluginsDeps.signing)
    id (PluginsDeps.node) version Versions.nodePlugin
    id (PluginsDeps.dokka) version Versions.dokkaPlugin
}

val sonatypeStaging = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
val sonatypeSnapshots = "https://oss.sonatype.org/content/repositories/snapshots/"

val sonatypePassword : String? by project

val sonatypeUsername : String? by project

val sonatypePasswordEnv : String? = System.getenv()["SONATYPE_PASSWORD"]
val sonatypeUsernameEnv : String? = System.getenv()["SONATYPE_USERNAME"]

repositories {
    mavenCentral()
    jcenter()

}
group = "com.ionspin.kotlin"
version = "0.0.4-SNAPSHOT"

val ideaActive = System.getProperty("idea.active") == "true"

kotlin {
    val hostOsName = getHostOsName()
    runningOnLinuxx86_64 {
        jvm()
        js {
            browser {
                testTask {
                    enabled = false //Until I sort out testing on travis
                    useKarma {
                        useChrome()
                    }
                }
            }
            nodejs {
                testTask {
                    useMocha() {
                        timeout = "10s"
                    }
                }
            }

        }
        linuxX64("linux") {

            binaries {

                executable {
                }
            }
        }
        // Linux 32 is using target-sysroot-2-raspberrypi which is missing getrandom and explicit_bzero in stdlib
        // so konanc can't build klib because getrandom missing will cause sodium_misuse()
        // so 32bit will be only available from non-delegated flavor
//        linuxArm32Hfp() {
//            binaries {
//                executable {
//                }
//            }
//        }
        linuxArm64() {
            binaries {
                executable {
                }
            }
        }

    }

    runningOnMacos {
        iosX64("ios") {
            binaries {
                framework {

                }
            }
        }
        iosArm64("ios64Arm") {
            binaries {
                framework {

                }
            }
        }

        iosArm32("ios32Arm") {
            binaries {
                framework {

                }
            }
        }
        macosX64() {
            binaries {
                executable {

                }
            }
        }
    }
    runningOnWindows {

        mingwX64() {
            binaries {
                executable {
                }
            }
        }
    }

    println(targets.names)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
                implementation(kotlin(Deps.Common.test))
                implementation(Deps.Common.coroutines)
                implementation(Deps.Common.kotlinBigNum)
                implementation(project(":multiplatform-crypto-delegated"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
            }
        }


        val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(Deps.Native.coroutines)
            }
        }

        val nativeTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation(Deps.Native.coroutines)
            }
        }

        runningOnLinuxx86_64 {
            val jvmMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.stdLib))
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation(Deps.Jvm.coroutinesCore)
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation(Deps.Jvm.coroutinesTest)
                    implementation(kotlin(Deps.Jvm.reflection))
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.stdLib))
                    implementation(Deps.Js.coroutines)
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(Deps.Js.coroutines)
                    implementation(kotlin(Deps.Js.test))
                }
            }
            val linuxMain by getting {
                dependsOn(nativeMain)
            }
            val linuxTest by getting {
                dependsOn(nativeTest)
            }
//            val linuxArm32HfpMain by getting {
//                dependsOn(nativeMain)
//            }
//            val linuxArm32HfpTest by getting {
//                dependsOn(nativeTest)
//            }
            val linuxArm64Main by getting {
                dependsOn(nativeMain)
            }
            val linuxArm64Test by getting {
                dependsOn(nativeTest)
            }
        }

        runningOnMacos {

            val iosMain by getting {
                dependsOn(nativeMain)
            }
            val iosTest by getting {
                dependsOn(nativeTest)
            }

            val ios64ArmMain by getting {
                dependsOn(nativeMain)
            }
            val ios64ArmTest by getting {
                dependsOn(nativeTest)
            }

            val ios32ArmMain by getting {
                dependsOn(nativeMain)
            }
            val ios32ArmTest by getting {
                dependsOn(nativeTest)
            }

            val macosX64Main by getting {
                dependsOn(nativeMain)
            }
            val macosX64Test by getting {
                dependsOn(nativeTest)
            }
        }


        runningOnWindows {
            val mingwX64Main by getting {
                dependsOn(commonMain)
                dependencies {
                    implementation(Deps.Native.coroutines)
                }
            }

            val mingwX64Test by getting {
                dependsOn(commonTest)
            }
        }


        all {
            languageSettings.enableLanguageFeature("InlineClasses")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
        }
    }


}

tasks {


    create<Jar>("javadocJar") {
        dependsOn(dokka)
        archiveClassifier.set("javadoc")
        from(dokka.get().outputDirectory)
    }

    if (getHostOsName() == "linux") {


        val jvmTest by getting(Test::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
            }
        }

        val linuxTest by getting(KotlinNativeTest::class) {

            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                // showStandardStreams = true
            }
        }

        val jsNodeTest by getting(KotlinJsTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                showStandardStreams = true
            }
        }

//        val legacyjsNodeTest by getting(KotlinJsTest::class) {
//
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//                showStandardStreams = true
//            }
//        }

//        val jsIrBrowserTest by getting(KotlinJsTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//                 showStandardStreams = true
//            }
//        }
    }

    if (getHostOsName() == "windows") {
        val mingwX64Test by getting(KotlinNativeTest::class) {

            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                showStandardStreams = true
            }
        }
    }

}








