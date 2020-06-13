set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeTvos.sh
#now we can do the delegated build of ios and macos libraries
cd ..
./gradlew multiplatform-crypto-delegated:publishTvosArm64PublicationToSnapshotRepository \
multiplatform-crypto-delegated:publishTvosX64PublicationToSnapshotRepository
set +e
