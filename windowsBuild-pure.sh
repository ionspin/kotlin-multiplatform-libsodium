set -e
#!/bin/sh
./gradlew --no-daemon multiplatform-crypto-api:build
./gradlew --no-daemon multiplatform-crypto:build
set +e

