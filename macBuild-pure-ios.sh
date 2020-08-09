set -e
#!/bin/sh
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:iosArm32MainKlibrary multiplatform-crypto:iosArm32TestKlibrary \
multiplatform-crypto:iosArm64MainKlibrary multiplatform-crypto:iosArm64TestKlibrary \
multiplatform-crypto:iosX64MainKlibrary multiplatform-crypto:iosX64TestKlibrary
./gradlew multiplatform-crypto:iosX64Test

set +e
