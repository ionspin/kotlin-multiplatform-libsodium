set -e
#!/bin/sh
./gradlew clean
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:build
set +e

