set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew clean
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:build
./gradlew multiplatform-crypto:publishMingwX64PublicationToSnapshotRepository
set +e
