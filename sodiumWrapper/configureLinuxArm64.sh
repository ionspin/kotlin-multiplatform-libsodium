#! /bin/sh
export PREFIX="$(pwd)/static-arm64"
export CC=/usr/bin/aarch64-linux-gnu-gcc
export CFLAGS="-O3"
export SYSROOT=$HOME/.konan/dependencies/target-sysroot-1-linux-glibc-arm64/
cd libsodium
./configure --prefix=$PREFIX --host=aarch64-linux-gnu-gcc --with-sysroot=$SYSROOT "$@"

