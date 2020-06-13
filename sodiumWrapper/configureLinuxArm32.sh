#! /bin/sh
export PREFIX="$(pwd)/static-arm32"
#export CC=/usr/bin/arm-none-eabi-gcc
export SYSROOT=$HOME/.konan/dependencies/target-sysroot-2-raspberrypi
#export CFLAGS="-fno-stack-protector -mfpu=vfp -mfloat-abi=hard -I$SYSROOT/usr/include/c++/4.8.3 -I$SYSROOT/usr/include/c++/4.8.3/arm-linux-gnueabihf"
export CFLAGS=""
export LDFLAGS=""
cd libsodium
./autogen.sh -s -f
./configure  --prefix=$PREFIX --with-sysroot=$SYSROOT --host=arm-linux-gnueabihf "$@"
