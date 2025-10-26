set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
export CLANG_BIN=$HOME/.konan/dependencies/clang-llvm-8.0.0-linux-x86-64/bin
cd sodiumWrapper
./makeLinuxX86-64.sh
#Workaround for travis using wrong ld
if [ "$TRAVIS" = "true" ]
then
  sudo mv /usr/bin/ld /usr/bin/ld.bck
  sudo ln -s $CLANG_BIN/ld.lld /usr/bin/ld
fi
./makeLinuxArm64.sh
./makeAndroid.sh
#now we can do the delegated build
cd ..
#build libsodium bindings
./gradlew multiplatform-crypto-libsodium-bindings:build

./gradlew publishJvmPublicationToSnapshotRepository publishJsPublicationToSnapshotRepository \
publishKotlinMultiplatformPublicationToSnapshotRepository publishLinuxX64PublicationToSnapshotRepository \
publishLinuxArm64PublicationToSnapshotRepository \
publishAndroidDebugPublicationToSnapshotRepository publishAndroidReleasePublicationToSnapshotRepository
set +e
