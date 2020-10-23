#!/bin/sh
./gradlew --no-daemon multiplatform-crypto-api:build || exit 1
./gradlew --no-daemon multiplatform-crypto:build || exit 1
exit 0


