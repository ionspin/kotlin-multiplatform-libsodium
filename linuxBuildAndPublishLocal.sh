#!/bin/sh
set -e
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeLinuxX86-64.sh
./makeLinuxArm64.sh
./makeAndroid.sh
#now we can do the delegated build
cd ..
#build libsodium bindings
./gradlew multiplatform-crypto-libsodium-bindings:build
 
./gradlew publishJvmPublicationToMavenLocal publishJsPublicationToMavenLocal \
publishKotlinMultiplatformPublicationToMavenLocal publishLinuxX64PublicationToMavenLocal \
publishLinuxArm64PublicationToMavenLocal \
publishAndroidDebugPublicationToMavenLocal publishAndroidReleasePublicationToMavenLocal
set +e
 
