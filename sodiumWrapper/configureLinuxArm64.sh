#! /bin/sh
export PREFIX="$(pwd)/static-arm64"
export CC=/usr/bin/aarch64-linux-gnu-gcc
export TARGET_ARCH=armv8-a
export CFLAGS="-Os -march=${TARGET_ARCH}"
export SYSROOT="$HOME/.konan/dependencies/target-sysroot-1-linux-glibc-arm64/"
cd libsodium
./configure  --prefix=$PREFIX --host=aarch64-linux-gnu-gcc --with-sysroot=$SYSROOT "$@"

