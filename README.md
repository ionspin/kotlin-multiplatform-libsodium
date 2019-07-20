# Kotlin Multiplatform Crypto Library

Kotlin Multiplatform Crypto is a library for various cryptographic applications. 

This is an extremely early release, currently only consisting of Blake2b and SHA256 and 512.

## Notes & Roadmap

**The API will move fast and break often until v1.0**

Tenative plan is to add 25519 curve based signing and key exchange next.

## Should I use this in production?

No, it's untested and unproven. 

## Integration

#### Gradle
```kotlin
implementation("com.ionspin.kotlin:crypto:0.0.1")
```

#### Snapshot builds
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
implementation("com.ionspin.kotlin:crypto:0.0.1-SNAPSHOT")

```

## Usage

To be continued...


 