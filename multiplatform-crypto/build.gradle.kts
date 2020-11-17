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
import org.jetbrains.dokka.Platform

plugins {
    kotlin(PluginsDeps.multiplatform)
    id(PluginsDeps.mavenPublish)
    id(PluginsDeps.signing)
    id(PluginsDeps.node) version Versions.nodePlugin
    id(PluginsDeps.dokka)
}

val sonatypeStaging = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
val sonatypeSnapshots = "https://oss.sonatype.org/content/repositories/snapshots/"

val sonatypePassword: String? by project

val sonatypeUsername: String? by project

val sonatypePasswordEnv: String? = System.getenv()["SONATYPE_PASSWORD"]
val sonatypeUsernameEnv: String? = System.getenv()["SONATYPE_USERNAME"]

repositories {
    mavenCentral()
    jcenter()

}
group = ReleaseInfo.group
version = ReleaseInfo.version

val ideaActive = System.getProperty("idea.active") == "true"

kotlin {
    val hostOsName = getHostOsName()
    val bla =1
    runningOnLinuxx86_64 {
        jvm()
        js {

        browser {

            testTask {
//                isRunningInTravis {
                    enabled = false //Until I sort out testing on travis, and figure out how to increase karma timeout
//                }
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

        linuxArm64() {
            binaries {
                staticLib {
                }
            }
        }

        linuxArm32Hfp() {
            binaries {
                staticLib {
                }
            }
        }



    }

    runningOnMacos {
        iosX64() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }
        iosArm64() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }

        iosArm32() {
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
        tvosX64() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }

        tvosArm64() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }

        watchosArm64() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }

        watchosArm32() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }

        watchosX86() {
            binaries {
                framework {
                    optimized = true
                }
            }
        }
    }
    runningOnWindows {

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


        val nativeMain by creating {
            dependsOn(commonMain)
            dependencies {
            }
            isRunningInIdea {
                kotlin.setSrcDirs(emptySet<String>())
            }
        }


        val nativeTest by creating {
            dependsOn(commonTest)
            dependencies {
            }
        }

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
            compilations.getByName("main") {
                println("Setting native sourceset dependancy for $name")
                if ((this@withType.name.contains("ios") ||
                            this@withType.name.contains("mingw")).not()
                ) {
                    println("Setting native sourceset dependancy for $this@withType.name")
                    defaultSourceSet.dependsOn(nativeMain)
                }
            }
            compilations.getByName("test") {
                println("Setting native sourceset dependancy for $name")
                if ((this@withType.name.contains("ios") ||
                            this@withType.name.contains("mingw")).not()
                ) {
                    println("Setting native sourceset dependancy for $this@withType.name")
                    defaultSourceSet.dependsOn(nativeTest)
                }
            }
        }



        runningOnLinuxx86_64 {
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
            val linuxMain by getting {
                dependsOn(nativeMain)
                //Force idea to consider native sourceset
                if (ideaActive) {
                    kotlin.srcDir("src/nativeMain/kotlin")
                }
            }
            val linuxTest by getting {
                dependsOn(nativeTest)
//                Force idea to consider native sourceset
                if (ideaActive) {
                    kotlin.srcDir("src/nativeTest/kotlin")
                }
            }

            val linuxArm64Main by getting {
                dependsOn(nativeMain)
            }

            val linuxArm64Test by getting {
                dependsOn(nativeTest)
            }

            val linuxArm32HfpMain by getting {
                dependsOn(nativeMain)
            }

            val linuxArm32HfpTest by getting {
                dependsOn(nativeTest)
            }

        }


        runningOnMacos{

            val iosX64Main by getting {
                dependsOn(nativeMain)
            }
            val iosX64Test by getting {
                dependsOn(nativeTest)
            }

            val iosArm64Main by getting {
                dependsOn(nativeMain)
            }
            val iosArm64Test by getting {
                dependsOn(nativeTest)
            }

            val iosArm32Main by getting {
                dependsOn(nativeMain)
            }
            val iosArm32Test by getting {
                dependsOn(nativeTest)
            }

            val macosX64Main by getting {
                dependsOn(commonMain)
                if (ideaActive) {
                    kotlin.srcDir("src/nativeMain/kotlin")
                }
            }
            val macosX64Test by getting {
                dependsOn(commonTest)
                if (ideaActive) {
                    kotlin.srcDir("src/nativeTest/kotlin")
                }
            }
        }

//      Coroutines don't support mingwx86 yet
//        val mingwX86Main by getting {
//            dependsOn(commonMain)
//            dependencies {
//            }
//        }

//        val mingwX86Test by getting {
//            dependsOn(commonTest)
//        }
//
        runningOnWindows {
            val mingwX64Main by getting {
                dependsOn(commonMain)
                dependencies {
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



task<Copy>("copyPackageJson") {
    dependsOn("compileKotlinJs")
    println("Copying package.json from $projectDir/core/src/jsMain/npm")
    from("$projectDir/src/jsMain/npm")
    println("Node modules dir ${node.nodeModulesDir}")
    into("${node.nodeModulesDir}")
}

tasks {


    create<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        archiveClassifier.set("javadoc")
        from(dokkaJavadoc.get().outputDirectory)
    }

    dokkaJavadoc {
        println("Dokka !")
        dokkaSourceSets {
            named("commonMain") {
                displayName.set("common")
                platform.set(Platform.common)
            }
        }



    }
    if (getHostOsName() == "linux" && getHostArchitecture() == "x86-64") {

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


