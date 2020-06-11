set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeMacosX86-64.sh
./makeIos.sh
#now we can do the delegated build of ios and macos libraries
cd ..
./gradlew multiplatform-crypto-delegated:publishIosArm32PublicationToMavenRepository \
multiplatform-crypto-delegated:publishIosArm64PublicationToMavenRepository \
multiplatform-crypto-delegated:publishIosX64PublicationToMavenRepository \
multiplatform-crypto-delegated:publishMacosX64PublicationToMavenRepository
set +e
