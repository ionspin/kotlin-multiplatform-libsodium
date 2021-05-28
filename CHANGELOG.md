## Descriptive changelog
(All dates are DD.MM.YYYY)

#### 0.8.4-SNAPSHOT

#### 0.8.3 - 28.5.2021
- Built with kotlin 1.5.10
- Fixed loading but not initializing libsodium on js and jvm
- Changed subkey id to UInt from Int, limited by JS api
- Updated libsodium to latest master 710b2d3963347017ba (potentially will be switched to stable branch)
- Experimentig with sodium_malloc usage instead of pinned UByteArrays

#### 0.8.2 - Split from crypto repository and initial release

