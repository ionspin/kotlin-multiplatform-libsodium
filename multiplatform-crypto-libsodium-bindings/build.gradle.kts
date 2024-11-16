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
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    kotlin(PluginsDeps.multiplatform)
    id(PluginsDeps.mavenPublish)
    id(PluginsDeps.signing)
    id(PluginsDeps.taskTree) version Versions.taskTreePlugin
    id(PluginsDeps.androidLibrary)
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
    mavenLocal()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }

}
group = ReleaseInfo.group
version = ReleaseInfo.bindingsVersion

val ideaActive = isInIdea()
println("Idea active: $ideaActive")
android {
    compileSdk = AndroidPluginConfiguration.sdkVersion
    defaultConfig {
        minSdk = AndroidPluginConfiguration.minVersion
        targetSdk = AndroidPluginConfiguration.sdkVersion
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    sourceSets.getByName("main") {
//        jniLibs.srcDir("src/androidMain/libs")
    }

    lint {
        abortOnError = false
    }
}



kotlin {
    val hostOsName = getHostOsName()
    androidTarget() {
        publishLibraryVariants("release", "debug")
    }

    jvm()
    val projectRef = project
    runningOnLinuxx86_64 {
        println("Configuring Linux X86-64 targets")


        js {
            browser {
                testTask {
                    useKarma {
                        useChromeHeadless()
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
                    defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                    compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-linux-x86-64/include/")
                }
                kotlinOptions.freeCompilerArgs = listOf(
                    "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-linux-x86-64/lib/libsodium.a"
                )
            }
            binaries {
                staticLib {
                }
            }
        }

        if (ideaActive.not()) {
            linuxArm64() {
                binaries {
                    staticLib {
                    }
                }
            }
        }

    }

    runningOnLinuxArm64 {
        println("Configuring Linux Arm 64 targets")

    }

    runningOnLinuxArm32 {
        println("Configuring Linux Arm 32 targets")

    }
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

    iosSimulatorArm64() {
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
    macosArm64() {
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
    tvosSimulatorArm64() {
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

    watchosSimulatorArm64() {
        binaries {
            framework {
                optimized = true
            }
        }
    }


    println("Configuring Mingw targets")
    mingwX64() {
        if (hostOsName == "windows") {
            binaries {
                staticLib {
                    optimized = true
                }
            }
            compilations.getByName("main") {
                val libsodiumCinterop by cinterops.creating {
                    defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                    compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-mingw-x86-64/include")
                }
                kotlinOptions.freeCompilerArgs = listOf(
                    "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-mingw-x86-64/lib/libsodium.a"
                )
            }
        } else {
            // Disable cross compilation until https://youtrack.jetbrains.com/issue/KT-30498 is supported
            compilations.all {
                cinterops.all {
                    project.tasks[interopProcessingTaskName].enabled = false
                }
                compileKotlinTask.enabled = false
            }
            binaries.all {
                linkTask.enabled = false
            }
            mavenPublication(
                Action {
                    tasks.withType<AbstractPublishToMaven> {
                        onlyIf {
                            publication != this@Action
                        }
                    }
                    tasks.withType<GenerateModuleMetadata>() {
                        onlyIf {
                            publication.get() != this@Action
                        }
                    }
                }
            )
        }

    }



    println(targets.names)

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin(Deps.Common.stdLib))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
                implementation(Deps.Common.coroutines)
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
            if (ideaActive.not()) {
                "linuxArm64"
            } else {
                ""
            }
        )

        //iosArm64, iosX64, macosX64, metadata, tvosArm64, tvosX64, watchosArm32, watchosArm64
        val macos64Bit = setOf(
            "macosX64", "macosArm64"
        )
        val iosArm = setOf(
            "iosArm64",
        )
        val iosSimulator = setOf(
            "iosX64", "iosSimulatorArm64"
        )
        val mingw64Bit = setOf(
            "mingwX64"
        )

        val tvosArm = setOf(
            "tvosArm64"
        )
        val tvosSimulator = setOf(
            "tvosX64", "tvosSimulatorArm64"
        )

        val watchosArm = setOf(
            "watchosArm64", "watchosArm32"
        )
        val watchosSimulator = setOf(
            "watchosSimulatorArm64"
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
                            defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                            compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-arm64/include/")
                        }
                        kotlinOptions.freeCompilerArgs = listOf(
                            "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-arm64/lib/libsodium.a"
                        )
                    }
                }

                if (macos64Bit.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(
                        createWorkaroundNativeMainSourceSet(
                            this@withType.name,
                            nativeDependencies
                        )
                    )
                    println("Setting macos cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-macos/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-macos/lib/libsodium.a"
                    )
                }
                //All ioses share the same static library
                if (iosArm.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(
                        createWorkaroundNativeMainSourceSet(
                            this@withType.name,
                            nativeDependencies
                        )
                    )
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-ios/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-ios/lib/libsodium.a"
                    )
                }

                if (iosSimulator.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(
                        createWorkaroundNativeMainSourceSet(
                            this@withType.name,
                            nativeDependencies
                        )
                    )
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-ios-simulators/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-ios-simulators/lib/libsodium.a"
                    )
                }

                if (tvosArm.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(
                        createWorkaroundNativeMainSourceSet(
                            this@withType.name,
                            nativeDependencies
                        )
                    )
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-tvos/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-tvos/lib/libsodium.a"
                    )
                }

                if (tvosSimulator.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(
                        createWorkaroundNativeMainSourceSet(
                            this@withType.name,
                            nativeDependencies
                        )
                    )
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-tvos-simulators/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-tvos-simulators/lib/libsodium.a"
                    )
                }

                if (watchosArm.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(
                        createWorkaroundNativeMainSourceSet(
                            this@withType.name,
                            nativeDependencies
                        )
                    )
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-watchos/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary", "${projectRef.rootDir}/sodiumWrapper/static-watchos/lib/libsodium.a"
                    )
                }

                if (watchosSimulator.contains(this@withType.name)) {
                    defaultSourceSet.dependsOn(
                        createWorkaroundNativeMainSourceSet(
                            this@withType.name,
                            nativeDependencies
                        )
                    )
                    println("Setting ios cinterop for $this")
                    val libsodiumCinterop by cinterops.creating {
                        defFile(projectRef.file("src/nativeInterop/cinterop/libsodium.def"))
                        compilerOpts.add("-I${projectRef.rootDir}/sodiumWrapper/static-watchos-simulators/include")
                    }
                    kotlinOptions.freeCompilerArgs = listOf(
                        "-include-binary",
                        "${projectRef.rootDir}/sodiumWrapper/static-watchos-simulators/lib/libsodium.a"
                    )
                }


            }
            compilations.getByName("test") {
                println("Setting native test dep for $this@withType.name")
                defaultSourceSet.dependsOn(nativeTest)


            }
        }

        val androidMain by getting {
            isNotRunningInIdea {
                kotlin.srcDirs("src/androidMain", "src/androidSpecific", "src/jvmMain/kotlin")
            }
            isRunningInIdea {
                kotlin.srcDirs("src/androidSpecific", "src/jvmMain/kotlin")
            }
            dependencies {
                implementation("net.java.dev.jna:jna:5.12.1@aar")
                implementation(Deps.Jvm.resourceLoader) {
                    exclude("net.java.dev.jna", "jna")
                }
            }
        }

        val androidUnitTest by getting {
            dependencies {
            }
        }

        val jvmMain by getting {
            kotlin.srcDirs("src/jvmSpecific", "src/jvmMain/kotlin")
            dependencies {
                implementation(kotlin(Deps.Jvm.stdLib))

                implementation(Deps.Jvm.resourceLoader)

                implementation(Deps.Jvm.Delegated.jna)

                implementation("org.slf4j:slf4j-api:1.7.30")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin(Deps.Jvm.test))
                implementation(kotlin(Deps.Jvm.testJUnit))
                implementation(kotlin(Deps.Jvm.reflection))
            }
        }
        runningOnLinuxx86_64 {
            println("Configuring Linux 64 Bit source sets")


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
            languageSettings.optIn("kotlin.ExperimentalUnsignedTypes")
            languageSettings.optIn("kotlin.ExperimentalStdlibApi")
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }
    }


}

tasks.whenTaskAdded {
    if ("DebugUnitTest" in name || "ReleaseUnitTest" in name) {
        enabled =
            false // https://youtrack.jetbrains.com/issue/KT-34662 otherwise common tests fail, because we require native android libs to be loaded
    }
}

tasks {


    create<Jar>("javadocJar") {
        dependsOn(dokkaHtml)
        archiveClassifier.set("javadoc")
        from(dokkaHtml.get().outputDirectory)
    }

    dokkaHtml {
        println("Dokka !")
        dokkaSourceSets {
        }

    }

    val jvmTest by getting(Test::class) {
        testLogging {
            events("PASSED", "FAILED", "SKIPPED")
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = true
            showStackTraces = true
        }
    }

    if (getHostOsName() == "linux" && getHostArchitecture() == "x86-64") {

        val linuxX64Test by getting(KotlinNativeTest::class) {

            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                exceptionFormat = TestExceptionFormat.FULL
                showStandardStreams = true
                showStackTraces = true
            }
        }
        val jsNodeTest by getting(KotlinJsTest::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
                exceptionFormat = TestExceptionFormat.FULL
                showStandardStreams = true
                showStackTraces = true
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

//        val jsLegacyBrowserTest by getting(KotlinJsTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//                showStandardStreams = true
//            }
//        }
//
//        val jsIrBrowserTest by getting(KotlinJsTest::class) {
//            testLogging {
//                events("PASSED", "FAILED", "SKIPPED")
//                showStandardStreams = true
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

allprojects {
    tasks.withType(JavaCompile::class) {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
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
            description.set("Kotlin Multiplatform Libsodium Wrapper")
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
                url.set("https://github.com/ionspin/kotlin-multiplatform-libsodium")
                connection.set("scm:git:git://git@github.com:ionspin/kotlin-multiplatform-libsodium.git")
                developerConnection.set("scm:git:ssh://git@github.com:ionspin/kotlin-multiplatform-libsodium.git")

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



