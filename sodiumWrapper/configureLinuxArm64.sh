#! /bin/sh
export PREFIX="$(pwd)/static-arm64"
export CLANG_BIN=$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/
export CC=$HOME/.konan/dependencies/aarch64-unknown-linux-gnu-gcc-8.3.0-glibc-2.25-kernel-4.9-2/bin/aarch64-unknown-linux-gnu-gcc
export SYSROOT=$HOME/.konan/dependencies/qemu-aarch64-static-5.1.0-linux-2
#export TOOLCHAIN=$HOME/.konan/dependencies/target-gcc-toolchain-3-linux-x86-64/x86_64-unknown-linux-gnu
#export CFLAGS="-O3 -target aarch64-unknown-linux-gnu --sysroot $SYSROOT -gcc-toolchain $TOOLCHAIN -fuse-ld=$CLANG_BIN/ld.lld -B$CLANG_BIN"
export CFLAGS="-O3"
export LDFLAGS=""
cd libsodium
./autogen.sh -s -f
./configure --prefix=$PREFIX --with-sysroot=$SYSROOT --host=aarch64-unknown-linux-gnu "$@"

