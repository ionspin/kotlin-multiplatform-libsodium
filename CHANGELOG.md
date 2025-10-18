## Descriptive changelog
(All dates are DD.MM.YYYY)

#### 0.9.4-SNAPSHOT - current development snapshot

#### 0.9.3 - 2025-10-18
- Add Ristretto255 (Libsodium's Finite Field Arithmetic API) support (thanks to Johannes Leupold @Traderjoe95 for the contribution #52)
- Update JNA dependency
- Rebuild libsodium with latest stable branch - commit 3eabeb547fd7f91f4f8a15885050eb2a8e0272d5 (fixes #59)

#### 0.9.2 - 13.4.2024
- Use 2.0.2 release of resource loader instead of forked snapshot

#### 0.9.1 - 6.4.2024
- Fix #42, return values from libsodium calls are now checked
- Bump kotlin to 1.9.23
- Update to latest stable libsodium
- Update latest MSVC library

#### 0.9.0 - 23.9.2023
- Breaking changes: 
  - PasswordHash.str now returns String 
  - PasswordHash.strNeedsRehash and PasswordHash.strVerify now take String type for `passwordHash` parameter

- Update to latest stable build of libsodium
- Update to kotlin 1.9.10
- Targets deprecated by kotlin 1.9.10 and therefore removed from this library:  
  - iosArm32
  - watchosX86
  - wasm32
  - mingwX86
  - linuxArm32Hfp

#### 0.8.9-SNAPSHOT - 26.3.2023
- Update to latest stable build of libsodium
- Update to kotlin 1.8.10


#### 0.8.8 - 9.10.2022
- Fix for missing mingw target in published library modules.xml (#29)

#### 0.8.7 - 10.9.2022
- Bump to kotlin 1.7.10
- Update libsodium to latest stable version
- Fix for #26 - problems running in test environment on JVM
#### 0.8.6 - 19.3.2022
- Bump to 1.6.21
- Enable initialization on 32bit Android devices
- Fix for #23 JS libsodium won't initalize
- API change chacha20IetfXorIc now takes UInt initial counter parameter

#### 0.8.5 - 5.3.2022
- Libsodium updated to 7d67f1909bfa6e12254 (2022)
- Fix array out of bounds on native when trying to get a pointer to empty array. Return null instead.
- Fixed #20 - calling `crypto_sign_ed25519_sk_to_curve25519` instead of `crypto_sign_ed25519_pk_to_curve25519` in jvm and native implementations
- Bump to kotlin 1.6.10

#### 0.8.4 - 19.7.2021
- Bump to kotlin 1.5.21
- Libsodium.js bump to 0.7.9
- Removed bintray/jcenter repositories

#### 0.8.3 - 28.5.2021
- Built with kotlin 1.5.10
- Fixed loading but not initializing libsodium on jvm
- Changed subkey id to UInt from Int, limited by JS api
- Updated libsodium to latest master 710b2d3963347017ba (potentially will be switched to stable branch)

#### 0.8.2 - Split from crypto repository and initial release

