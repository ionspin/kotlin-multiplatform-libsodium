cd libsodium
./autogen.sh -s -f

./configure --prefix=$PREFIX "$@"
./dist-build/watchos.sh
mkdir ../static-watchos
cp -R ./libsodium-watchos/lib ../static-watchos/lib
cp -R ./libsodium-watchos/include ../static-watchos/include
rm -r ./libsodium-watchos/
