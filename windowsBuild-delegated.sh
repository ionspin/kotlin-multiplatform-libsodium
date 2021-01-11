#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew --no-daemon clean || exit 1
./gradlew --no-daemon multiplatform-crypto-api:build || exit 1
cd sodiumWrapper
echo "Starting mingw libsodium  build"
./configureMingw64.sh
echo "Configure done"
make -j4 -C libsodium clean
make -j4 -C libsodium
make -j4 -C libsodium install
echo "completed libsodium build"
#now we can do the delegated build
cd ..
#and then libsodium bindings
./gradlew --no-daemon multiplatform-crypto-libsodium-bindings:build || exit 1
echo "Script completed"
exit 0

