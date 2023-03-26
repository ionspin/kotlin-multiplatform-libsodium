./configureMacos64.sh
cd libsodium
./dist-build/apple-xcframework.sh

mkdir ../static-ios
mkdir ../static-ios-simulators
mkdir ../static-watchos
mkdir ../static-watchos-simulators
mkdir ../static-tvos
mkdir ../static-tvos-simulators
mkdir ../static-macos

cp -R ./libsodium-apple/tmp/ios/lib ../static-ios/lib
cp -R ./libsodium-apple/tmp/ios/include ../static-ios
cp -R ./libsodium-apple/tmp/ios-simulators/lib ../static-ios-simulators/lib
cp -R ./libsodium-apple/tmp/ios-simulators/include ../static-ios-simulators

cp -R ./libsodium-apple/tmp/watchos/lib ../static-watchos/lib
cp -R ./libsodium-apple/tmp/watchos/include ../static-watchos
cp -R ./libsodium-apple/tmp/watchos-simulators/lib ../static-watchos-simulators/lib
cp -R ./libsodium-apple/tmp/watchos-simulators/include ../static-watchos-simulators

cp -R ./libsodium-apple/tmp/tvos/lib ../static-tvos/lib
cp -R ./libsodium-apple/tmp/tvos/include ../static-tvos
cp -R ./libsodium-apple/tmp/tvos-simulators/lib ../static-tvos-simulators/lib
cp -R ./libsodium-apple/tmp/tvos-simulators/include ../static-tvos-simulators

cp -R ./libsodium-apple/tmp/macos/lib ../static-macos/lib
cp -R ./libsodium-apple/tmp/macos/include ../static-macos
