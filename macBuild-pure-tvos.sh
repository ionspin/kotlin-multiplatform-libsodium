set -e
#!/bin/sh
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:tvosArm64MainKlibrary multiplatform-crypto:tvosArm64TestKlibrary \
multiplatform-crypto:tvosX64MainKlibrary multiplatform-crypto:tvosX64TestKlibrary
./gradlew multiplatform-crypto:tvosX64Test
set +e
