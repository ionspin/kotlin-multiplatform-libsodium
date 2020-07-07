[![Build Status](https://travis-ci.com/ionspin/kotlin-multiplatform-crypto.svg?branch=master)](https://travis-ci.com/ionspin/kotlin-multiplatform-crypto)
![Maven Central](https://img.shields.io/maven-central/v/com.ionspin.kotlin/multiplatform-crypto.svg)

# Kotlin Multiplatform Crypto Library

#Note:
### Next stable release will be published after public release of Kotlin 1.4, until then API will change significantly

Kotlin Multiplatform Crypto is a library for various cryptographic applications. 

The library comes in two flavors `multiplatform-crypto` and `multiplatform-crypto-delegated`. This project also provides 
direct libsodium bindings under `multiplatform-crypto-libsodium-bindings`.

* `multiplatform-crypto` contains pure kotlin implementations, is not reviewed, should be considered unsafe and only 
for prototyping or experimentation purposes.

* `multiplatform-crypto-delegated` relies on platform specific implementations, mostly libsodium, but care should still be taken that the kotlin code is not reviewed or proven safe.

APIs of both variants are identical. 

### Table of contents
1. [Supported platforms](#supported-platforms-by-variant)
2. [API](#api)
3. TODO

## Supported platforms by variant
|Platform|Pure variant| Delegated variant|
|--------|------------|------------------|
|Linux X86 64|          :heavy_check_mark: | :heavy_check_mark: |
|Linux Arm 64|          :heavy_check_mark: | :heavy_check_mark: |
|Linux Arm 32|          :heavy_check_mark: | :x: |
|macOS X86 64|          :heavy_check_mark: | :heavy_check_mark: |
|iOS x86 64 |           :heavy_check_mark: | :heavy_check_mark: |
|iOS Arm 64 |           :heavy_check_mark: | :heavy_check_mark: |
|iOS Arm 32 |           :heavy_check_mark: | :heavy_check_mark: |
|watchOS X86 32 |       :heavy_check_mark: | :heavy_check_mark: |
|watchOS Arm 64(_32) |  :heavy_check_mark: | :heavy_check_mark: |
|watchos Arm 32 |       :heavy_check_mark: | :heavy_check_mark: |
|tvOS X86 64 |          :heavy_check_mark: | :heavy_check_mark: |
|tvOS Arm 64 |          :heavy_check_mark: | :heavy_check_mark: |
|minGW X86 64|          :heavy_check_mark: | :heavy_check_mark: |
|minGW X86 32|          :x:                | :x: | 

## Sample project
The library includes sample project that shows usage on different platforms
- NOTE: Currently only linux, macOs and windows are included. 

## Notes & Roadmap

**The API will move fast and break often until v1.0**

Next steps:
- Expand API (ECC, Signing ...)

## Should I use this in production?

No, until it is reviewed.

## Should I use this in code that is *critical* in any way, shape or form?

No, but even if after being warned you decide to, then use `multiplatform-crypto-delegated` as it relies on reputable libraries.

## Why?

This is an experimental implementation, mostly for expanding personal understanding of cryptography. 
It's not peer reviewed, not guaranteed to be bug free, and not guaranteed to be secure.

## API

### Hashing functions
* Blake2b
* SHA512
* SHA256
  
### Key Derivation 

* Argon2

### Authenticated symmetric encryption (AEAD)

* XChaCha20-Poly1305


### Delegated flavor dependancy table
The following table describes which library is used for particular cryptographic primitive

| Primitive | JVM | JS | Native |
| ----------|-----|----|--------|
| Blake2b | LazySodium | libsodium.js | libsodium |
| SHA256 | LazySodium | libsodium.js | libsodium |
| SHA512 | LazySodium | libsodium.js | libsodium |
| XChaCha20-Poly1305 | LazySodium | libsodium.js | libsodium |



## Integration

NOTE: Latest version of the library is built with Kotlin 1.4-M2 and therefore only SNAPSHOT variant is available. Next 
stable version will be released when Kotlin 1.4. is released

#### Gradle
Kotlin 
```kotlin
implementation("com.ionspin.kotlin:multiplatform-crypto:0.1.0")

or

implementation("com.ionspin.kotlin:multiplatform-crypto-delegated:0.1.0")
```

#### Snapshot builds
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
implementation("com.ionspin.kotlin:multiplatform-crypto:0.1.0-SNAPSHOT")

```

## Usage

### Helper functions

All API take `UByteArray` as message/key/nonce/etc parameter. For convenience when working with strings we provide
`String.enocdeToUbyteArray()` extensions function, and `UByteArray.toHexString` extension function. 

More convenience functions will be added.

### Hashes

Hashes are provided in two versions, "stateless", usually the companion object of the hash, 
which takes the data to be hashed in one go, and "updatable" which can be fed data in chunks.


#### Blake2b

You can use Blake 2b in two modes

##### Stateless version
You need to deliver the complete data that is to be hashed in one go

```kotlin
val input = "abc"
val result = Crypto.Blake2b.stateless(input.encodeToUByteArray())
```

Result is returned as a `UByteArray`

##### Updatable instance version
You can create an instance and feed the data by using `update(input : UByteArray)` call. Once all data is supplied,
you should call `digest()`.

If you want to use Blake2b with a key, you should supply it when creating the `Blake2b` instance.

```kotlin
val test = "abc"
val key = "key"
val blake2b = Crypto.Blake2b.updateable(key.encodeToUByteArray())
blake2b.update(test.encodeToUByteArray())
val result = blake2b.digest().toHexString()
```

After digest is called, the instance is reset and can be reused (Keep in mind key stays the same for the particular instance).
#### SHA2 (SHA256 and SHA512)

##### Stateless version

You need to deliver the complete data that is to be hashed in one go. You can either provide the `UByteArray` as input
or `String`. Result is always returned as `UByteArray` (At least in verision 0.0.1)

```kotlin
val input = "abc"
val result = Crypto.Sha256.stateless(input.encodeToUByteArray())
```

```kotlin
val input ="abc"
val result = Crypto.Sha512.stateless(input.encodeToUByteArray())
```

Result is returned as a `UByteArray`

##### Updateable version

Or you can use the updatable instance version

```kotlin
val sha256 = Crypto.Sha256.updateable()
sha256.update("abc".encodeToUByteArray())
val result = sha256.digest()
```

```kotlin
val sha512 = Crypto.Sha512.updateable()
sha512.update("abc".encodeToUByteArray())
val result = sha512.digest()
```

### Key derivation

#### Argon2

NOTE: This implementation is tested against KAT generated by reference Argon2 implementation, which does not follow
specification completely. See this issue https://github.com/P-H-C/phc-winner-argon2/issues/183

```kotlin
val argon2Instance = Argon2(
            password = "Password",
            salt = "RandomSalt",
            parallelism = 8,
            tagLength = 64U,
            requestedMemorySize = 256U, //4GB
            numberOfIterations = 4U,
            key = "",
            associatedData = "",
            argonType = ArgonType.Argon2id
        )
val tag = argon2Instance.derive()
val tagString = tag.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "")
val expectedTagString = "c255e3e94305817d5e09a7c771e574e3a81cc78fef5da4a9644b6df0" +
        "0ba1c9b424e3dd0ce7e600b1269b14c84430708186a8a60403e1bfbda935991592b9ff37"
println("Tag: ${tagString}")
assertEquals(tagString, expectedTagString)
```

### Symmetric encryption (OUTDATED, won't be exposed in next release, no counterpart in delegated flavor - 0.10.1)

#### AES 

Aes is available with CBC and CTR mode through `AesCbc` and `AesCtr` classes/objects. 
Similarly to hashes you can either use stateless or updateable version.

Initialization vector, or counter states are chosen by the SDK automaticaly, and returned alongside encrypted data

##### Stateless AesCbc and AesCtr 

AesCtr

```kotlin
val keyString = "4278b840fb44aaa757c1bf04acbe1a3e"
val key = AesKey.Aes128Key(keyString)
val plainText = "6bc1bee22e409f96e93d7e117393172aae2d8a571e03ac9c9eb76fac45af8e5130c81c46a35ce411e5fbc1191a0a52eff69f2445df4f9b17ad2b417be66c3710"

val encryptedDataAndInitializationVector = AesCtr.encrypt(key, plainText.hexStringToUByteArray())
val decrypted = AesCtr.decrypt(
    key,
    encryptedDataAndInitializationVector.encryptedData,
    encryptedDataAndInitializationVector.initialCounter
)
plainText == decrypted.toHexString()
```

AesCbc

```kotlin

val keyString = "4278b840fb44aaa757c1bf04acbe1a3e"
val key = AesKey.Aes128Key(keyString)

val plainText = "3c888bbbb1a8eb9f3e9b87acaad986c466e2f7071c83083b8a557971918850e5"

val encryptedDataAndInitializationVector = AesCbc.encrypt(key, plainText.hexStringToUByteArray())
val decrypted = AesCbc.decrypt(
    key,
    encryptedDataAndInitializationVector.encryptedData,
    encryptedDataAndInitializationVector.initilizationVector
)
plainText == decrypted.toHexString()

```

## Libsodium bindings
TODO

















