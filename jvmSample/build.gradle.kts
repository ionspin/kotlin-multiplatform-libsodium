plugins {
    kotlin("jvm")
}

group = "com.ionspin.kotlin"
version = "unspecified"

repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(project(path = ":multiplatform-crypto"))
    testImplementation(kotlin(Deps.Jvm.test))
    testImplementation(kotlin(Deps.Jvm.testJUnit))
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}