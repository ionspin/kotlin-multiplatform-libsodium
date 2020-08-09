set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:publishMacosX64PublicationToSnapshotRepository
set +e
