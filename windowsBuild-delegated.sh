set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts

./gradlew multiplatform-crypto-api:build
cd sodiumWrapper
echo "Starting mingw libsodium  build"
./configureMingw64.sh
echo "Configure done"
$GNU_MAKE -j4 -C libsodium clean
$GNU_MAKE -j4 -C libsodium
$GNU_MAKE -j4 -C libsodium install
echo "completed libsodium build"
#now we can do the delegated build
cd ..
./gradlew multiplatform-crypto-delegated:build
#and then libsodium bindings
./gradlew multiplatform-crypto-libsodium-bindings:build
set +e

