[![Build Status](https://travis-ci.com/ionspin/kotlin-multiplatform-crypto.svg?branch=master)](https://travis-ci.com/ionspin/kotlin-multiplatform-crypto)
![Maven Central](https://img.shields.io/maven-central/v/com.ionspin.kotlin/multiplatform-crypto.svg)

# Kotlin Multiplatform Crypto Library

Kotlin Multiplatform Crypto is a library for various cryptographic applications. 

This is an extremely early release, currently only consisting of Blake2b and SHA256 and 512.

## Notes & Roadmap

**The API will move fast and break often until v1.0**

Make SHA hashes "updatable" like Blake2b

After that tenative plan is to add 25519 curve based signing and key exchange next.

## Should I use this in production?

No, it's untested and unproven. 

## Supported

## Hashing functions
* Blake2b
* SHA512
* SHA256

More to come.

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

### Hashes

Hashes are provided in two versions, "stateless", usually the companion object of the hash, 
which takes the data to be hashed in one go, and "updatable" which can be fed data in chunks.


#### Blake2b

You can use Blake 2b in two modes

##### Stateless version
You need to deliver the complete data that is to be hashed in one go

```kotlin
val input = "abc"
val result = Blake2b.digest(input)
```

Result is returned as a `Array<Byte>`

##### Updatable instance version
You can create an instance and feed the data by using `update(input : Array<Byte>)` call. Once all data is supplied,
you should call `digest()` or `digestString()` convenience method that converts the `Array<Byte>` into hexadecimal string.

If you want to use Blake2b with a key, you should supply it when creating the `Blake2b` instance.

```kotlin
val test = "abc"
val key = "key"
val blake2b = Blake2b(key)
blake2b.update(test)
val result = blake2b.digest()
```

After digest is called, the instance is reset and can be reused (Keep in mind key stays the same for the particular instance).
#### SHA2 (SHA256 and SHA512)

##### Stateless version

You need to deliver the complete data that is to be hashed in one go. You can either provide the `Array<Byte>` as input
or `String`. Result is always returned as `Array<Byte>` (At least in verision 0.0.1)

```kotlin
val input = "abc"
val result = Sha256.digest(input)
```

```kotlin
val input ="abc"
val result = Sha512.digest(message = input.encodeToByteArray().map { it.toUByte() }.toTypedArray())
```

Result is returned as a `Array<Byte>`

##### Updateable version

Or you can use the updatable instance version

```kotlin
val sha256 = Sha256()
sha256.update("abc")
val result = sha256.digest()
```

```kotlin
val sha512 = Sha512()
sha512.update("abc")
val result = sha512.digest()
```









 