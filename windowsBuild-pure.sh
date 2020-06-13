set -e
#!/bin/sh
./gradlew multiplatform-crypto-api:build
./gradlew multiplatform-crypto:build
set +e

