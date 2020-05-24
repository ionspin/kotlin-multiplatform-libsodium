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

import com.moowork.gradle.node.task.NodeTask
import org.gradle.api.tasks.testing.logging.TestLogging
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

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

fun getHostOsName(): String {
    val target = System.getProperty("os.name")
    if (target == "Linux") return "linux"
    if (target.startsWith("Windows")) return "windows"
    if (target.startsWith("Mac")) return "macos"
    return "unknown"
}

kotlin {
    val hostOsName = getHostOsName()
    if (ideaActive) {
        when(hostOsName) {
            "linux" -> linuxX64("native")
            "macos" -> macosX64("native")
            "windows" -> mingwX64("native")
        }
    }
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
            //Until I figure out how to run headless chrome on travis
//        browser {
//
//            testTask {
//                useKarma {
//                    useChrome()
//                }
//            }
//        }
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
//        linuxArm32Hfp() {
//            binaries {
//                staticLib {
//                }
//            }
//        }
        //Not supported in coroutines at the moment
//        linuxArm64() {
//            binaries {
//                staticLib {
//                }
//            }
//        }

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
    }
// No coroutines support for mingwX86
//    mingwX86() {
//        binaries {
//            staticLib {
//
//            }
//        }
//    }


    println(targets.names)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
                implementation(kotlin(Deps.Common.test))
                implementation(Deps.Common.coroutines)
                implementation(Deps.Common.kotlinBigNum)
                implementation(project(Deps.Common.apiProject))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
            }
        }

        val nativeMain = if (ideaActive) {
            val nativeMain by getting {
                dependsOn(commonMain)
                dependencies {
                    implementation(Deps.Native.coroutines)
                }
            }
            nativeMain
        } else {
            val nativeMain by creating {
                dependsOn(commonMain)
                dependencies {
                    implementation(Deps.Native.coroutines)
                }
            }
            nativeMain
        }
        val nativeTest = if (ideaActive) {
            val nativeTest by getting {
                dependsOn(commonTest)
                dependencies {
                    implementation(Deps.Native.coroutines)
                }
            }
            nativeTest
        } else {
            val nativeTest by creating {
                dependsOn(commonTest)
                dependencies {
                    implementation(Deps.Native.coroutines)
                }
            }
            nativeTest
        }

        if (hostOsName == "linux") {
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
            //Not supported in coroutines at the moment
//            val linuxArm32HfpMain by getting {
//                dependsOn(nativeMain)
//            }
//
//            val linuxArm32HfpTest by getting {
//                dependsOn(nativeTest)
//            }

//            val linuxArm64Main by getting {
//                dependsOn(nativeMain)
//            }
//
//            val linuxArm64Test by getting {
//                dependsOn(nativeTest)
//            }

        }

        if (hostOsName == "macos") {

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

//      Coroutines don't support mingwx86 yet
//        val mingwX86Main by getting {
//            dependsOn(commonMain)
//            dependencies {
//                implementation(Deps.Native.coroutines)
//            }
//        }

//        val mingwX86Test by getting {
//            dependsOn(commonTest)
//        }
//
        if (hostOsName == "windows") {
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
        }
    }


}



task<Copy>("copyPackageJson") {
    dependsOn("compileKotlinJs")
    println("Copying package.json from $projectDir/core/src/jsMain/npm")
    from ("$projectDir/src/jsMain/npm")
    println("Node modules dir ${node.nodeModulesDir}")
    into ("${node.nodeModulesDir}")
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

        val npmInstall by getting
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



signing {
    isRequired = false
    sign(publishing.publications)
}

publishing {
    publications.withType(MavenPublication::class) {
        artifact(tasks["javadocJar"])
        pom {
            name.set("Kotlin Multiplatform Crypto")
            description.set("Kotlin Multiplatform Crypto library")
            url.set("https://github.com/ionspin/kotlin-multiplatform-crypto")
            licenses {
                license {
                    name.set("The Apache License, Version 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }
            developers {
                developer {
                    id.set("ionspin")
                    name.set("Ugljesa Jovanovic")
                    email.set("opensource@ionspin.com")
                }
            }
            scm {
                url.set("https://github.com/ionspin/kotlin-multiplatform-crypto")
                connection.set("scm:git:git://git@github.com:ionspin/kotlin-multiplatform-crypto.git")
                developerConnection.set("scm:git:ssh://git@github.com:ionspin/kotlin-multiplatform-crypto.git")

            }

        }
    }
    repositories {
        maven {

            url = uri(sonatypeStaging)
            credentials {
                username = sonatypeUsername ?: sonatypeUsernameEnv ?: ""
                password = sonatypePassword ?: sonatypePasswordEnv ?: ""
            }
        }

        maven {
            name = "snapshot"
            url = uri(sonatypeSnapshots)
            credentials {
                username = sonatypeUsername ?: sonatypeUsernameEnv ?: ""
                password = sonatypePassword ?: sonatypePasswordEnv ?: ""
            }
        }
    }
}


