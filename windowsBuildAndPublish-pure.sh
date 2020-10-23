#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew clean || exit 1
./gradlew --no-daemon multiplatform-crypto-api:build || exit 1
./gradlew --no-daemon multiplatform-crypto:build || exit 1
./gradlew --no-daemon multiplatform-crypto:publishMingwX64PublicationToSnapshotRepository || exit 1
exit 0
