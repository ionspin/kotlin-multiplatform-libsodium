cd libsodium
./autogen.sh -s -f

./configure --prefix=$PREFIX "$@"
./dist-build/tvos.sh
mkdir ../static-tvos
cp -R ./libsodium-tvos/lib ../static-tvos/lib
cp -R ./libsodium-tvos/include ../static-tvos/include
rm -r ./libsodium-tvos/


