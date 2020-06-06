[![Build Status](https://travis-ci.com/ionspin/kotlin-multiplatform-crypto.svg?branch=master)](https://travis-ci.com/ionspin/kotlin-multiplatform-crypto)
![Maven Central](https://img.shields.io/maven-central/v/com.ionspin.kotlin/multiplatform-crypto.svg)

# Kotlin Multiplatform Crypto Library

Kotlin Multiplatform Crypto is a library for various cryptographic applications. 

API is very opinionated, meant to be used on both encrypting and decrypting side. The idea is that API leaves less room for 
errors when using it.

The library comes in two flavors `multiplatform-crypto` and `multiplatform-crypto-delegated`

* `multiplatform-crypto` contains pure kotlin implementations, is not reviewed, should be considered unsafe and only 
for prototyping or experimentation purposes.

* `multiplatform-crypto-delegated` relies on platform specific implementations, like libsodium, but care should still be taken that the kotlin code is not reviewed or proven safe. 


## Notes & Roadmap

**The API will move fast and break often until v1.0**

Next steps:
- Expand API (AEAD, ECC ...)

## Should I use this in production?

No, until it is reviewed.

## Should I use this in code that is *critical* in any way, shape or form?

No, but even if after being warned you decide to, then use `multiplatform-crypto-delegated`.

## Why?

This is an experimental implementation, mostly for expanding personal understanding of cryptography. 
It's not peer reviewed, not guaranteed to be bug free, and not guaranteed to be secure.

## Currently supported 

### Hashing functions
* Blake2b
* SHA512
* SHA256

### Symmetric cipher
* AES
  * Modes: CBC, CTR
  
### Key Derivation 

* Argon2

### AEAD

TODO()


### Delegated flavor dependancy table
The following table describes which library is used for particular cryptographic primitive

| Primitive | JVM | JS | Native |
| ----------|-----|----|--------|
| Blake2b | JCE | libsodium.js | libsodium |
| SHA256 | JCE | libsodium.js | libsodium
| SHA512 | JCE | libsodium.js | libsodium
| AES-CBC | JCE | libsodium.js | libsodium
| AES-CTR | JCE | libsodium.js | libsodium


## Integration

#### Gradle
Kotlin 
```kotlin
implementation("com.ionspin.kotlin:multiplatform-crypto:0.0.5")

or

implementation("com.ionspin.kotlin:multiplatform-crypto-delegated:0.0.5")
```

#### Snapshot builds
```kotlin
repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
implementation("com.ionspin.kotlin:multiplatform-crypto:0.0.6-SNAPSHOT")

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

Result is returned as a `UByteArray`

##### Updatable instance version
You can create an instance and feed the data by using `update(input : UByteArray)` call. Once all data is supplied,
you should call `digest()`.

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

You need to deliver the complete data that is to be hashed in one go. You can either provide the `UByteArray` as input
or `String`. Result is always returned as `UByteArray` (At least in verision 0.0.1)

```kotlin
val input = "abc"
val result = Sha256.digest(input)
```

```kotlin
val input ="abc"
val result = Sha512.digest(message = input.encodeToByteArray().map { it.toUByte() }.toTypedArray())
```

Result is returned as a `UByteArray`

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
### Symmetric encryption

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


















 