#! /bin/sh
export PREFIX="$(pwd)/static-arm32"
#export CC=/usr/bin/arm-none-eabi-gcc
export TARGET_ARCH=armv7
export SYSROOT="$HOME/.konan/dependencies/target-sysroot-2-raspberrypi --host=arm-none-eabi"
export CFLAGS=""
export LDFLAGS="--specs=nosys.specs"
cd libsodium
./configure  --prefix=$PREFIX --with-sysroot=$SYSROOT --host=arm-none-eabi "$@"

