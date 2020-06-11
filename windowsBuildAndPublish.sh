set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeMingwX86-64.sh
#now we can do the delegated build
cd ..
./gradlew multiplatform-crypto-delegated:build
#and finally pure build
./gradlew multiplatform-crypto:build
./gradlew publishJvmPublicationToSnapshotRepository publishJsPublicationToSnapshotRepository \
publishKotlinMultiplatformPublicationToSnapshotRepository publishLinuxX64PublicationToSnapshotRepository \
publishLinuxArm64PublicationToSnapshotRepository publishMetadataPublicationToSnapshotRepository
set +e