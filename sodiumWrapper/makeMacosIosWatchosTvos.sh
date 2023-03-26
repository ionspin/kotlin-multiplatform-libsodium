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

cp -R ./libsodium-apple/ios/lib ../static-ios/lib
cp -R ./libsodium-apple/ios/include ../static-ios
cp -R ./libsodium-apple/ios-simulators/lib ../static-ios-simulators/lib
cp -R ./libsodium-apple/ios-simulators/include ../static-ios-simulators

cp -R ./libsodium-apple/watchos/lib ../static-watchos/lib
cp -R ./libsodium-apple/watchos/include ../static-watchos
cp -R ./libsodium-apple/watchos-simulators/lib ../static-watchos-simulators/lib
cp -R ./libsodium-apple/watchos-simulators/include ../static-watchos-simulators

cp -R ./libsodium-apple/tvos/lib ../static-tvos/lib
cp -R ./libsodium-apple/tvos/include ../static-tvos
cp -R ./libsodium-apple/tvos-simulators/lib ../static-tvos-simulators/lib
cp -R ./libsodium-apple/tvos-simulators/include ../static-tvos-simulators

cp -R ./libsodium-apple/macos/lib ../static-macos/lib
cp -R ./libsodium-apple/macos/include ../static-macos
