#! /bin/sh
export PREFIX="$(pwd)/libsodium-ios"
export CC=/usr/bin/aarch64-linux-gnu-gcc
export TARGET_ARCH=armv8-a
export CFLAGS="-Os -march=${TARGET_ARCH}"
cd libsodium
./configure  --prefix=$PREFIX --host=aarch64-linux-gnu-gcc --with-sysroot=/home/ionspin/.konan/dependencies/target-sysroot-1-linux-glibc-arm64/ "$@"

