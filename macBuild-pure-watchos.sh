set -e
#!/bin/sh
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:watchosArm32MainKlibrary multiplatform-crypto:watchosArm32TestKlibrary \
multiplatform-crypto:watchosArm64MainKlibrary multiplatform-crypto:watchosArm64TestKlibrary \
multiplatform-crypto:watchosX86MainKlibrary multiplatform-crypto:watchosX86TestKlibrary
./gradlew multiplatform-crypto:watchosX86Test
set +e
