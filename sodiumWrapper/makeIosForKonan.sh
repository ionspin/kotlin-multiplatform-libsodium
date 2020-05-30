./configure
cd libsodium
./dist-build/ios.sh
cp -R ./libsodium-ios/lib ../ios-lib
cp -R ./libsodium-ios/include ../ios-include