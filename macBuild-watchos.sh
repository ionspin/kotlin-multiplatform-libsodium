set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeWatchos.sh
#now we can do the delegated build of ios and macos libraries
cd ..

./gradlew multiplatform-crypto-libsodium-bindings:watchosArm32MainKlibrary multiplatform-crypto-libsodium-bindings:watchosArm32TestKlibrary \
multiplatform-crypto-libsodium-bindings:watchosArm64MainKlibrary multiplatform-crypto-libsodium-bindings:watchosArm64TestKlibrary \
multiplatform-crypto-libsodium-bindings:watchosX86MainKlibrary multiplatform-crypto-libsodium-bindings:watchosX86TestKlibrary
./gradlew multiplatform-crypto-libsodium-bindings:watchosX86Test
set +e
