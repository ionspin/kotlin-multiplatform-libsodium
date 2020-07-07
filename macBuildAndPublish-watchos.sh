set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeWatchos.sh
#now we can do the delegated build of ios and macos libraries
cd ..
./gradlew multiplatform-crypto-delegated:publishWatchosArm32PublicationToSnapshotRepository \
multiplatform-crypto-delegated:publishWatchosArm64PublicationToSnapshotRepository \
multiplatform-crypto-delegated:publishWatchosX86PublicationToSnapshotRepository

./gradlew multiplatform-crypto-libsodium-bindings:publishWatchosArm32PublicationToSnapshotRepository \
multiplatform-crypto-libsodium-bindings:publishWatchosArm64PublicationToSnapshotRepository \
multiplatform-crypto-libsodium-bindings:publishWatchosX86PublicationToSnapshotRepository
set +e
