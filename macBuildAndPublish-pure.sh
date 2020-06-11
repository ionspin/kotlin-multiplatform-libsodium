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
./gradlew multiplatform-crypto:publishIosArm32PublicationToMavenRepository \
multiplatform-crypto:publishIosArm64PublicationToMavenRepository \
multiplatform-crypto:publishIosX64PublicationToMavenRepository \
multiplatform-crypto:publishMacosX64PublicationToMavenRepository \
multiplatform-crypto:publishTvosArm64PublicationToMavenRepository \
multiplatform-crypto:publishTvosX64PublicationToMavenRepository \
multiplatform-crypto:publishWatchosArm32PublicationToMavenRepository \
multiplatform-crypto:publishWatchosArm64PublicationToMavenRepository \
multiplatform-crypto:publishWatchosX86PublicationToMavenRepository
set +e
