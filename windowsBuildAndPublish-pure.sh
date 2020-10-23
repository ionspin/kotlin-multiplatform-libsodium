set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew clean
./gradlew --no-daemon multiplatform-crypto-api:build
./gradlew --no-daemon multiplatform-crypto:build
./gradlew --no-daemon multiplatform-crypto:publishMingwX64PublicationToSnapshotRepository
set +e
