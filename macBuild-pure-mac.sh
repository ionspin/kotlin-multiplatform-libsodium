set -e
#!/bin/sh
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:macosX64MainKlibrary multiplatform-crypto:macosX64TestKlibrary
./gradlew multiplatform-crypto:macosX64Test

set +e
