#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeLinux64.sh
./makeLinuxArm64.sh
#now we can do the delegated build
./gradlew multiplatform-crypto-delegated:build
#and finally pure build
./gradlew multiplatform-crypto:build


