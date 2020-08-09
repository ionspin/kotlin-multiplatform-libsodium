set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:publishIos32ArmPublicationToSnapshotRepository \
multiplatform-crypto:publishIos64ArmPublicationToSnapshotRepository \
multiplatform-crypto:publishIosPublicationToSnapshotRepository
set +e
