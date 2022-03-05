## Descriptive changelog
(All dates are DD.MM.YYYY)

#### 0.8.6-SNAPSHOT

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

