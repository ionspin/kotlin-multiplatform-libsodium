plugins {
    kotlin
}

group = "com.ionspin.kotlin.crypto"
version = "0.0.1"

repositories {
    mavenCentral()
    google()
    maven ("https://kotlin.bintray.com/kotlinx")
    maven ("https://dl.bintray.com/kotlin/kotlin-eap")
    maven ("https://dl.bintray.com/kotlin/kotlin-dev")
    jcenter()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation (kotlin(Deps.Jvm.stdLib))
    implementation("com.squareup:kotlinpoet:1.6.0")
    testImplementation(kotlin(Deps.Jvm.test))
    testImplementation(kotlin(Deps.Jvm.testJUnit))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes",
        ""
    )
}





