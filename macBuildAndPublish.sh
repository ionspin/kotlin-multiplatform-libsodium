set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeMacosX86-64
./makeIos.sh
./makeTvos.sh
./makeWatchos.sh
#now we can do the delegated build
cd ..
./gradlew multiplatform-crypto-delegated:build
#and finally pure build
./gradlew multiplatform-crypto:build
./gradlew build publishIos64ArmPublicationToSnapshotRepository publishIosPublicationToSnapshotRepository publishMacosX64PublicationToSnapshotRepository publishIos32ArmPublicationToSnapshotRepository
set +e