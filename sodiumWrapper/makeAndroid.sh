#!/bin/sh
./configureAndroid.sh
cd libsodium
NDK_PLATFORM=android-26 ./dist-build/android-armv7-a.sh
NDK_PLATFORM=android-26 ./dist-build/android-armv8-a.sh
NDK_PLATFORM=android-26 ./dist-build/android-x86_64.sh
NDK_PLATFORM=android-26 ./dist-build/android-x86.sh
cp libsodium-android-armv8-a+crypto/lib/libsodium.so ../../multiplatform-crypto-libsodium-bindings/src/androidMain/jniLibs/arm64-v8a/
cp libsodium-android-armv7-a/lib/libsodium.so ../../multiplatform-crypto-libsodium-bindings/src/androidMain/jniLibs/armeabi-v7a/libsodium.so
cp libsodium-android-i686/lib/libsodium.so ../../multiplatform-crypto-libsodium-bindings/src/androidMain/jniLibs/x86/libsodium.so
cp libsodium-android-westmere/lib/libsodium.so ../../multiplatform-crypto-libsodium-bindings/src/androidMain/jniLibs/x86_64/libsodium.so

cd ..




