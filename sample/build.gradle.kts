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
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.FatFrameworkTask

plugins {
    kotlin(PluginsDeps.multiplatform)
    id(PluginsDeps.kapt)
    id(PluginsDeps.androidApplication)
    id(PluginsDeps.kotlinAndroidExtensions)
    id(PluginsDeps.mavenPublish)
    id(PluginsDeps.signing)
    kotlin(PluginsDeps.kotlinSerializationPlugin) version Versions.kotlinSerializationPlugin

}
org.jetbrains.kotlin.gradle.targets.js.npm.NpmResolverPlugin.apply(project)

val sonatypeStaging = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
val sonatypeSnapshots = "https://oss.sonatype.org/content/repositories/snapshots/"



repositories {
    mavenCentral()
    jcenter()
    maven("https://dl.bintray.com/terl/lazysodium-maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")

}
group = "com.ionspin.kotlin"
version = "0.1.0-SNAPSHOT"

val ideaActive = System.getProperty("idea.active") == "true"

kotlin {
    val hostOsName = getHostOsName()
    if (ideaActive) {
        when (hostOsName) {
            "linux" -> linuxX64("native")
            "macos" -> macosX64("native")
            "windows" -> mingwX64("native")
        }
    }
    android()
    runningOnLinuxx86_64 {
        jvm()
        js {
            browser {
                webpackTask {

                }
                testTask {
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

//            binaries.executable()

        }


        linuxX64("linux") {
            binaries {
                executable {
                }
            }
        }

        //Disable for now as libui doesnt support arm64
//        linuxArm64() {
//            binaries {
//                executable {
//                }
//            }
//        }

    }

    runningOnMacos {
        val iosX64Target = iosX64() {
            binaries {
                framework {
                    baseName = "LibsodiumBindings"
                    export(Deps.Common.libsodiumBindings)
                }
            }

        }
        val iosArm64Target = iosArm64() {
            binaries {
                framework {
                    baseName = "LibsodiumBindings"
                    export(Deps.Common.libsodiumBindings)
                }
            }
        }
        val iosArm32Target = iosArm32() {
            binaries {
                framework {
                    baseName = "LibsodiumBindings"
                    export(Deps.Common.libsodiumBindings)
                }
            }
        }
        val macosX64Target = macosX64()
        val tvosX64Target = tvosX64()
        val tvosArm64Target = tvosArm64()
        val watchosArm64Target = watchosArm64()
        val watchosArm32Target = watchosArm32()
        val watchosX86Target = watchosX86()

        configure(listOf(macosX64Target)) {
            binaries.executable {}
        }

//        configure(listOf(
//            iosX64Target, iosArm64Target, iosArm32Target,
//            tvosX64Target, tvosArm64Target, watchosArm64Target,
//            watchosArm32Target, watchosX86Target)
//        ) {
//            binaries.framework {
//                baseName = "LibsodiumBindings"
//                export(Deps.Common.libsodiumBindings)
//            }
//        }
        val mode = System.getenv("CONFIGURATION") ?: "DEBUG"
        println("Mode $mode")
        // Create a task to build a fat framework.
        tasks.create("packForXcode", FatFrameworkTask::class) {
            // The fat framework must have the same base name as the initial frameworks.
            baseName = "LibsodiumBindings"
            // The default destination directory is '<build directory>/fat-framework'.
            destinationDir = File(buildDir, "xcode-frameworks")
            // Specify the frameworks to be merged.
            from(
                iosX64Target.binaries.getFramework(mode),
                iosArm64Target.binaries.getFramework(mode),
                iosArm32Target.binaries.getFramework(mode)
            )
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
                implementation(Deps.Common.kotlinBigNum)
                implementation(Deps.Common.serialization)
                api(Deps.Common.libsodiumBindings)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin(Deps.Common.test))
                implementation(kotlin(Deps.Common.testAnnotation))
            }
        }

        val androidMain by getting {

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}")
                implementation("androidx.appcompat:appcompat:1.2.0")
                implementation("androidx.core:core-ktx:1.3.2")
                implementation("androidx.constraintlayout:constraintlayout:2.0.2")
                implementation("com.google.android.material:material:1.3.0-alpha03")
//                implementation("androidx.ui:ui-tooling:$composeDevVersion")
//                implementation("androidx.ui:ui-layout:$composeDevVersion")
//                implementation("androidx.ui:ui-material:$composeDevVersion")
//                implementation("androidx.ui:ui-foundation:$composeDevVersion")
//                implementation("androidx.ui:ui-framework:$composeDevVersion")
                implementation(Deps.Android.coroutines)
                implementation(Deps.Android.timber)
//                implementation("androidx.compose:compose-runtime:$composeDevVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin(Deps.Jvm.test))
                implementation(kotlin(Deps.Jvm.testJUnit))
                implementation(Deps.Jvm.coroutinesTest)
                implementation(kotlin(Deps.Jvm.reflection))
                implementation(Deps.Jvm.coroutinesCore)
            }
        }



//        val nativeMain by creating {
//            dependsOn(commonMain)
//            dependencies {
//                implementation(Deps.Desktop.libui)
//            }
//        }
//
//        val nativeTest by creating {
//            dependsOn(commonTest)
//            dependencies {
//            }
//        }

        val nativeMain = if (ideaActive) {
            val nativeMain by getting {
                dependsOn(commonMain)
                dependencies {
                    implementation(Deps.Desktop.libui)
                }
            }
            nativeMain
        } else {
            val nativeMain by creating {
                dependsOn(commonMain)
                dependencies {
                    implementation(Deps.Desktop.libui)
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
                    implementation(Deps.Js.coroutines)

                    // React
                    implementation(Deps.Js.React.react)
                    implementation(Deps.Js.React.reactDom)
                    implementation(npm(Deps.Js.Npm.reactPair.first, Deps.Js.Npm.reactPair.second))
                    implementation(npm(Deps.Js.Npm.reactDomPair.first, Deps.Js.Npm.reactDomPair.second))

                    // Styled
                    implementation(Deps.Js.React.styled)
                    implementation(npm(Deps.Js.Npm.styledComponentsPair.first, Deps.Js.Npm.styledComponentsPair.second))
                    implementation(npm(Deps.Js.Npm.inlineStylePrefixesPair.first, Deps.Js.Npm.inlineStylePrefixesPair.second))

                }
            }
            val jsTest by getting {
                dependencies {
                    implementation(kotlin(Deps.Js.test))
                }
            }

            val linuxMain by getting {
                dependsOn(nativeMain)
            }
            val linuxTest by getting {
                dependsOn(nativeTest)
            }

//            val linuxArm64Main by getting {
//                dependsOn(nativeMain)
//            }
//            val linuxArm64Test by getting {
//                dependsOn(nativeTest)
//            }
        }

        runningOnMacos {

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
                dependsOn(nativeMain)
            }
            val macosX64Test by getting {
                dependsOn(nativeTest)
            }
        }


        runningOnWindows {
            val mingwX64Main by getting {
                dependsOn(commonMain)
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

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.ionspin.kotlin.crypto.sample"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    sourceSets {
        val main by getting
        main.manifest.srcFile("src/androidMain/AndroidManifest.xml")
        main.java.srcDirs("src/androidMain/kotlin")
        main.res.srcDirs("src/androidMain/res")
    }
    packagingOptions {
        exclude("META-INF/library_release.kotlin_module")
        exclude("META-INF/kotlinx-serialization-runtime.kotlin_module")
        exclude("META-INF/ktor-http.kotlin_module")
        exclude("META-INF/ktor-utils.kotlin_module")
        exclude("META-INF/ktor-io.kotlin_module")
        exclude("META-INF/ktor-*")
    }
    compileOptions {
        setSourceCompatibility(JavaVersion.VERSION_1_8)
        setTargetCompatibility(JavaVersion.VERSION_1_8)
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

//    buildFeatures {
//        // Enables Jetpack Compose for this module
//        this.compose = true
//    }

//    composeOptions {
//        kotlinCompilerExtensionVersion = "0.1.0-dev05"
//    }

    // Magic for compose dev08, but it doesn't work with serialization plugin because of IR. Leave here for future reference.
//    project.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java)
//        .configureEach {
//            println("Task: $this")
//            if (this.name.contains("Android")) {
//                println("Setting plugins: $this")
//                this.kotlinOptions.freeCompilerArgs += listOf(
//                    "-P",
//                    "plugin:androidx.compose.plugins.idea:enabled=true"
//                )
//                this.kotlinOptions.freeCompilerArgs += "-Xplugin=${project.rootDir}/compose-compiler-0.1.0-dev08.jar"
//                this.kotlinOptions.freeCompilerArgs += "-Xuse-ir"
//            }
//        }
//    project.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java)
//        .forEach { compile ->
//            compile.kotlinOptions.freeCompilerArgs += listOf(
//                "-P",
//                "plugin:androidx.compose.plugins.idea:enabled=true"
//            )
//            compile.kotlinOptions.freeCompilerArgs += "-Xplugin=${project.rootDir}/compose-compiler-0.1.0-dev08.jar"
//            compile.kotlinOptions.freeCompilerArgs += "-Xuse-ir"
//            println("Compile: $compile")
//            println("Compiler free args ${compile.kotlinOptions.freeCompilerArgs}")
//        }
}

tasks {


    if (getHostOsName() == "linux") {


        val jvmTest by getting(Test::class) {
            testLogging {
                events("PASSED", "FAILED", "SKIPPED")
            }
        }

        val linuxTest by getting(KotlinNativeTest::class) {

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

fun org.jetbrains.kotlin.gradle.plugin.mpp.Executable.windowsResources(rcFileName: String) {
    val taskName = linkTaskName.replaceFirst("link", "windres")
    val inFile = compilation.defaultSourceSet.resources.sourceDirectories.singleFile.resolve(rcFileName)
    val outFile = buildDir.resolve("processedResources/$taskName.res")

    val windresTask = tasks.create<Exec>(taskName) {
        val konanUserDir = System.getenv("KONAN_DATA_DIR") ?: "${System.getProperty("user.home")}/.konan"
        val konanLlvmDir = "$konanUserDir/dependencies/msys2-mingw-w64-x86_64-clang-llvm-lld-compiler_rt-8.0.1/bin"

        inputs.file(inFile)
        outputs.file(outFile)
        commandLine("$konanLlvmDir/windres", inFile, "-D_${buildType.name}", "-O", "coff", "-o", outFile)
        environment("PATH", "$konanLlvmDir;${System.getenv("PATH")}")

        dependsOn(compilation.compileKotlinTask)
    }

    linkTask.dependsOn(windresTask)
    linkerOpts(outFile.toString())
}








