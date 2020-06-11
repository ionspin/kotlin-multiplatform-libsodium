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
./gradlew multiplatform-crypto-delegated:watchosArm32MainKlibrary multiplatform-crypto-delegated:watchosArm32TestKlibrary \
multiplatform-crypto-delegated:watchosArm64MainKlibrary multiplatform-crypto-delegated:watchosArm64TestKlibrary \
multiplatform-crypto-delegated:watchosX86MainKlibrary multiplatform-crypto-delegated:watchosX86TestKlibrary
set +e