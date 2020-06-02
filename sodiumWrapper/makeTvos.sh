cd libsodium
./dist-build/tvos.sh
mkdir ../static-tvos
cp -R ./libsodium-tvos/lib ../static-tvos/lib
cp -R ./libsodium-tvos/include ../static-tvos/include


