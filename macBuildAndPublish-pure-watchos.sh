set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:publishWatchosArm32PublicationToSnapshotRepository \
multiplatform-crypto:publishWatchosArm64PublicationToSnapshotRepository \
multiplatform-crypto:publishWatchosX86PublicationToSnapshotRepository
set +e
