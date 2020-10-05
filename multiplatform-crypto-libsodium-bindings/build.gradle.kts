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

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest

plugins {
    kotlin(PluginsDeps.multiplatform)
    id(PluginsDeps.mavenPublish)
    id(PluginsDeps.signing)
    id(PluginsDeps.node) version Versions.nodePlugin
    id(PluginsDeps.dokka)
    id(PluginsDeps.taskTree) version Versions.taskTreePlugin
    id(PluginsDeps.androidLibrary)
    id(PluginsDeps.kotlinAndroidExtensions)

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
    maven("https://dl.bintray.com/terl/lazysodium-maven")
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

}
group = ReleaseInfo.group
version = ReleaseInfo.version

val ideaActive = isInIdea()
println("Idea active: $ideaActive")

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}


kotlin {
    val hostOsName = getHostOsName()
    runningOnLinuxx86_64 {
        println("Configuring Linux X86-64 targets")
        jvm()
        android()
        js {
            browser {
                testTask {
                    isRunningInGitlabCi {
                        enabled = false //Until I sort out testing on travis
                    }
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
        linuxX64() {
            compilations.getByName("main") {
                val libsodiumCinterop by cinterops.creating {
                    defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                    compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-linux-x86-64/include/")
                }
                kotlinOptions.freeCompilerArgs = listOf(
                    "-include-binary", "${project.rootDir}/sodiumWrapper/static-linux-x86-64/lib/libsodium.a"
                )
            }
            binaries {
                staticLib {
                }
            }
        }


        linuxArm64() {
            binaries {
                staticLib {
                }
            }
        }
        // Linux 32 is using target-sysroot-2-raspberrypi which is missing getrandom and explicit_bzero in stdlib
        // so konanc can't build klib because getrandom missing will cause sodium_misuse()
        //     ld.lld: error: undefined symbol: explicit_bzero
        //     >>> referenced by utils.c
        //     >>>               libsodium_la-utils.o:(sodium_memzero) in archive /tmp/included11051337748775083797/libsodium.a
        //
        //     ld.lld: error: undefined symbol: getrandom
        //     >>> referenced by randombytes_sysrandom.c
        //     >>>               libsodium_la-randombytes_sysrandom.o:(_randombytes_linux_getrandom) in archive /tmp/included11051337748775083797/libsodium.a

    }

    runningOnLinuxArm64 {
        println("Configuring Linux Arm 64 targets")

    }

    runningOnLinuxArm32 {
        println("Configuring Linux Arm 32 targets")

    }

    runningOnMacos {
        println("Configuring macos targets")
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
            compilations.getByName("main") {
                val libsodiumCinterop by cinterops.creating {
                    defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                    compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-macos-x86-64/include")
                }
                kotlinOptions.freeCompilerArgs = listOf(
                    "-include-binary", "${project.rootDir}/sodiumWrapper/static-macos-x86-64/lib/libsodium.a"
                )
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
        println("Configuring Mingw targets")
        mingwX64() {
            binaries {
                staticLib {
                    optimized = true
                }
            }
            compilations.getByName("main") {
                val libsodiumCinterop by cinterops.creating {
                    defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                    compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-mingw-x86-64/include")
                }
                kotlinOptions.freeCompilerArgs = listOf(
                    "-include-binary", "${project.rootDir}/sodiumWrapper/static-mingw-x86-64/lib/libsodium.a"
                )
            }
        }
    }

    println(targets.names)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
                implementation(kotlin(Deps.Common.test))
                implementation(Deps.Common.kotlinBigNum)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
            }
        }

        val nativeDependencies = independentDependencyBlock {
        }

        val nativeMain by creating {
            dependsOn(commonMain)
            isRunningInIdea {
                kotlin.setSrcDirs(emptySet<String>())
            }
            dependencies {
                nativeDependencies(this)
            }
        }

        val nativeTest by creating {
            dependsOn(commonTest)
            isRunningInIdea {
                kotlin.setSrcDirs(emptySet<String>())
            }
            dependencies {
            }
        }

        //Set up shared source sets
        //linux, linuxArm32Hfp, linuxArm64
        val linux64Bit = setOf(
            "linuxX64"
        )
        val linuxArm64Bit = setOf(
            "linuxArm64"
        )
        val linux32Bit = setOf(
            "" // "linuxArm32Hfp"
        )

        //iosArm32, iosArm64, iosX64, macosX64, metadata, tvosArm64, tvosX64, watchosArm32, watchosArm64, watchosX86
        val macos64Bit = setOf(
            "macosX64"
        )
        val ios64Bit = setOf(
            "iosArm64", "iosX64"
        )
        val ios32Bit = setOf(
            "iosArm32"
        )
        val mingw64Bit = setOf(
            "mingwX64"
        )

        val tvos64Bit = setOf(
            "tvosArm64", "tvosX64"
        )

        val watchos32Bit = setOf(
            "watchosX86", "watchosArm32", "watchosArm64"
        )

        targets.withType<KotlinNativeTarget> {
            println("Target $name")

            compilations.getByName("main") {
                if (linux64Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(nativeMain)
                }
                if (linuxArm64Bit.contains(this@withType.name)) {
                        defaultSourceSet.dependsOn(
                            createWorkaroundNativeMainSourceSet(
                                this@withType.name,
                                nativeDependencies
                            )
                        )

                    compilations.getByName("main") {
                        val libsodiumCinterop by cinterops.creating {
                            defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                            compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-arm64/include/")
                        }
                        kotlinOptions.freeCompilerArgs = listOf(
                            "-include-binary", "${project.rootDir}/sodiumWrapper/static-arm64/lib/libsodium.a"
                        )
                    }
                }
                if (linux32Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(createWorkaroundNativeMainSourceSet(this@withType.name, nativeDependencies))
                }
                if (macos64Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(createWorkaroundNativeMainSourceSet(this@withType.name, nativeDependencies))
                }
                //All ioses share the same static library
                if (ios64Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(createWorkaroundNativeMainSourceSet(this@withType.name, nativeDependencies))
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-ios/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${project.rootDir}/sodiumWrapper/static-ios/lib/libsodium.a"
                    )
                }

                if (ios32Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(createWorkaroundNativeMainSourceSet(this@withType.name, nativeDependencies))
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-ios/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${project.rootDir}/sodiumWrapper/static-ios/lib/libsodium.a"
                    )
                }

                if (tvos64Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(createWorkaroundNativeMainSourceSet(this@withType.name, nativeDependencies))
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-tvos/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${project.rootDir}/sodiumWrapper/static-tvos/lib/libsodium.a"
                    )
                }

                if (watchos32Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(createWorkaroundNativeMainSourceSet(this@withType.name, nativeDependencies))
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(project.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${project.rootDir}/sodiumWrapper/static-watchos/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${project.rootDir}/sodiumWrapper/static-watchos/lib/libsodium.a"
                    )
                }



            }
            compilations.getByName("test") {
                println("Setting native test dep for $this@withType.name")
                defaultSourceSet.dependsOn(nativeTest)


            }
        }




        runningOnLinuxx86_64 {
            println("Configuring Linux 64 Bit source sets")
            val jvmMain by getting {
                kotlin.srcDirs("src/jvmSpecific", "src/jvmMain/kotlin")
                dependencies {
                    implementation(kotlin(Deps.Jvm.stdLib))
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))

                    //lazysodium
                    implementation(Deps.Jvm.Delegated.lazysodium)
                    implementation(Deps.Jvm.Delegated.jna)
                }
            }
            val jvmTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation(kotlin(Deps.Jvm.reflection))
                }
            }
            val androidMain by getting {
                isNotRunningInIdea {
                    kotlin.srcDirs("src/androidSpecific", "src/jvmMain/kotlin")
                }
                isRunningInIdea {
                    kotlin.srcDirs("src/androidSpecific")
                }
                dependencies {
                    implementation("com.goterl.lazycode:lazysodium-android:4.2.0@aar")
                    implementation("net.java.dev.jna:jna:5.5.0@aar")
                }
            }

            val androidTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Jvm.test))
                    implementation(kotlin(Deps.Jvm.testJUnit))
                    implementation("androidx.test:runner:1.2.0")
                    implementation("androidx.test:rules:1.2.0")
                }
            }

            val jsMain by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.stdLib))
                    implementation(npm(Deps.Js.Npm.libsodiumWrappers.first, Deps.Js.Npm.libsodiumWrappers.second))
                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.test))
                    implementation(npm(Deps.Js.Npm.libsodiumWrappers.first, Deps.Js.Npm.libsodiumWrappers.second))
                }
            }
            val linuxX64Main by getting {
                isRunningInIdea {
                    kotlin.srcDir("src/nativeMain/kotlin")
                }
            }
            val linuxX64Test by getting {
                dependsOn(nativeTest)
                isRunningInIdea {
                    kotlin.srcDir("src/nativeTest/kotlin")
                }
            }

        }

        runningOnMacos {
            println("Configuring Macos source sets")
            val macosX64Main by getting {
                dependsOn(nativeMain)
                if (ideaActive) {
                    kotlin.srcDir("src/nativeMain/kotlin")
                }

            }
            val macosX64Test by getting {
                dependsOn(nativeTest)
                if (ideaActive) {
                    kotlin.srcDir("src/nativeTest/kotlin")
                }

            }

            val tvosX64Main by getting {
                dependsOn(commonMain)
            }

            val tvosArm64Main by getting {
                dependsOn(commonMain)
            }

            val watchosX86Main by getting {
                dependsOn(commonMain)
            }

            val watchosArm64Main by getting {
                dependsOn(commonMain)
            }

            val watchosArm32Main by getting {
                dependsOn(commonMain)
            }

        }


        if (hostOsName == "windows") {
            val mingwX64Main by getting {
                dependsOn(nativeMain)
                if (ideaActive) {
                    kotlin.srcDir("src/nativeMain/kotlin")
                }
            }

            val mingwX64Test by getting {
                dependsOn(nativeTest)
                if (ideaActive) {
                    kotlin.srcDir("src/nativeTest/kotlin")
                }
            }
        }


        all {
            languageSettings.enableLanguageFeature("InlineClasses")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalUnsignedTypes")
            languageSettings.useExperimentalAnnotation("kotlin.ExperimentalStdlibApi")
        }
    }


}

tasks.whenTaskAdded {
    if("DebugUnitTest" in name || "ReleaseUnitTest" in name) {
        enabled = false // https://youtrack.jetbrains.com/issue/KT-34662 otherwise common tests fail, because we require native android libs to be loaded
    }
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
            create("commonMain") {
                displayName = "common"
                platform = "common"
            }
        }


    }
    if (getHostOsName() == "linux" && getHostArchitecture() == "x86-64") {
        val jvmTest by getting(Test::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
            }
        }

        val linuxX64Test by getting(KotlinNativeTest::class) {

            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                showStandardStreams = true
            }
        }

        val jsNodeTest by getting(KotlinJsTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
//                showStandardStreams = true
            }
        }



//        val legacyjsNodeTest by getting(KotlinJsTest::class) {
//
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//                showStandardStreams = true
//            }
//        }

        val jsBrowserTest by getting(KotlinJsTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                 showStandardStreams = true
            }
        }
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

//configurations.forEach {
//
//    if (it.name == "linuxCompileKlibraries") {
//        println("Configuration name: ${it.name}")
//        it.attributes {
//            this.keySet().forEach { key ->
//                val attribute = getAttribute(key)
//                println(" |-- Attribute $key ${attribute}")
//                attribute(org.jetbrains.kotlin.gradle.plugin.ProjectLocalConfigurations.ATTRIBUTE, "publicZ")
//            }
//        }
//    }
//}


