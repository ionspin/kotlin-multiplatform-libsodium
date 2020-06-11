set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
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
