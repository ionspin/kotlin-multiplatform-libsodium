cd libsodium
./dist-build/ios.sh
mkdir ../static-ios
cp -R ./libsodium-ios/lib ../static-ios/lib
cp -R ./libsodium-ios/include ../static-ios/include
