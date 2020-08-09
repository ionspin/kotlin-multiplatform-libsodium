set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeMacosX86-64.sh
./makeIos.sh
#now we can do the delegated build of ios and macos libraries
cd ..
./gradlew multiplatform-crypto-delegated:iosArm32MainKlibrary multiplatform-crypto-delegated:iosArm32TestKlibrary \
multiplatform-crypto-delegated:iosArm64MainKlibrary multiplatform-crypto-delegated:iosArm64TestKlibrary \
multiplatform-crypto-delegated:iosX64MainKlibrary multiplatform-crypto-delegated:iosX64TestKlibrary
./gradlew multiplatform-crypto-delegated:iosX64Test

./gradlew multiplatform-crypto-libsodium-bindings:iosArm32MainKlibrary multiplatform-crypto-libsodium-bindings:iosArm32TestKlibrary \
multiplatform-crypto-libsodium-bindings:iosArm64MainKlibrary multiplatform-crypto-libsodium-bindings:iosArm64TestKlibrary \
multiplatform-crypto-libsodium-bindings:iosX64MainKlibrary multiplatform-crypto-libsodium-bindings:iosX64TestKlibrary
./gradlew multiplatform-crypto-libsodium-bindings:iosX64Test
set +e

