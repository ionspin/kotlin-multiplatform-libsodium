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

import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin(PluginsDeps.multiplatform)
    id (PluginsDeps.mavenPublish)
    id (PluginsDeps.signing)
    id (PluginsDeps.dokka) version Versions.dokkaPlugin
}

repositories {
    mavenCentral()
    jcenter()

}
group = Published.group
version = Published.version

val ideaActive = System.getProperty("idea.active") == "true"

fun getHostOsName(): String {
    val target = System.getProperty("os.name")
    if (target == "Linux") return "linux"
    if (target.startsWith("Windows")) return "windows"
    if (target.startsWith("Mac")) return "macos"
    return "unknown"
}

kotlin {
    val hostOsName = getHostOsName()
    if (hostOsName == "linux") {
        jvm()
        js {
            compilations {
                this.forEach {
                    it.compileKotlinTask.kotlinOptions.sourceMap = true
                    it.compileKotlinTask.kotlinOptions.moduleKind = "commonjs"
                    it.compileKotlinTask.kotlinOptions.metaInfo = true

                    if (it.name == "main") {
                        it.compileKotlinTask.kotlinOptions.main = "call"
                    }
                    println("Compilation name ${it.name} set")
                    println("Destination dir ${it.compileKotlinTask.destinationDir}")
                }
            }
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
                staticLib {
                    optimized = true
                }
            }
        }
        //Not supported in coroutines at the moment
        linuxArm32Hfp() {
            binaries {
                staticLib {
                }
            }
        }
        //Not supported in coroutines at the moment
        linuxArm64() {
            binaries {
                staticLib {
                }
            }
        }

    }

    if (hostOsName == "macos") {
        iosX64("ios") {
            binaries {
                framework {
                    optimized = true
                }
            }
        }
        iosArm64("ios64Arm") {
            binaries {
                framework {
                    optimized = true
                }
            }
        }

        iosArm32("ios32Arm") {
            binaries {
                framework {
                    optimized = true
                }
            }
        }
        macosX64() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }
    }
    if (hostOsName == "windows") {

        mingwX64() {
            binaries {
                staticLib {
                    optimized = true
                }
            }
        }

        mingwX86() {
            binaries {
                staticLib {

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
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
            }
        }

        if (hostOsName == "linux") {
            val jvmMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.stdLib))
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation(kotlin(Deps.Jvm.reflection))
                }
            }
            val jsMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.stdLib))
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.test))
                }
            }
        }

        all {
            languageSettings.enableLanguageFeature("InlineClasses")
        }
    }


}

tasks {
    create<Jar>("javadocJar") {
        dependsOn(dokka)
        archiveClassifier.set("javadoc")
        from(dokka.get().outputDirectory)
    }

    dokka {
        println ("Dokka !")
        impliedPlatforms = mutableListOf("Common")
        kotlinTasks {
            listOf()
        }
        sourceRoot {
            println ("Common !")
            path = "/home/ionspin/Projects/Future/kotlin-multiplatform-crypto/crypto/src/commonMain" //TODO remove static path!
            platforms = listOf("Common")
        }
    }
    if (getHostOsName() == "linux") {
        val compileKotlinJs by getting(AbstractCompile::class)
        val compileTestKotlinJs by getting(Kotlin2JsCompile::class)

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






