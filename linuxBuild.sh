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
#now we can do the delegated build
cd ..
# TODO collect static libraries from mac and windows build and include them in jvm resources automatically

#build libsodium bindings
./gradlew multiplatform-crypto-libsodium-bindings:build
set +e

