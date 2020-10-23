set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeMacosX86-64.sh
./makeIosWatchosTvos.sh
#now we can do the delegated build
cd ..
./gradlew multiplatform-crypto-delegated:build
#pure build
./gradlew multiplatform-crypto:build
#libsodium bindings
./gradlew multiplatform-crypto-libsodium-bindings:build
set +e
