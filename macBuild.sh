set -e
#!/bin/sh
#this will hopefully download all konan dependancies that we use in the build scripts
./gradlew multiplatform-crypto-api:build
#now let's build linux deps
cd sodiumWrapper
./makeMacosIosWatchosTvos.sh
#now we can do the delegated build
cd ..
#libsodium bindings
./gradlew multiplatform-crypto-libsodium-bindings:build
set +e
