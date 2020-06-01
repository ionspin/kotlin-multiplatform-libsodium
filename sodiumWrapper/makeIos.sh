./configure
cd libsodium
./dist-build/ios.sh
cp -R ./static-ios/lib ../ios-lib
cp -R ./static-ios/include ../ios-include
