#! /bin/sh
export PREFIX="$(pwd)/static-arm64"
export CLANG_BIN=$HOME/.konan/dependencies/clang-llvm-8.0.0-linux-x86-64/bin
export CC=$HOME/.konan/dependencies/clang-llvm-8.0.0-linux-x86-64/bin/clang
export SYSROOT=$HOME/.konan/dependencies/target-sysroot-1-linux-glibc-arm64
export TOOLCHAIN=$HOME/.konan/dependencies/target-gcc-toolchain-3-linux-x86-64/x86_64-unknown-linux-gnu
#export CFLAGS="-O3 -target aarch64-unknown-linux-gnu --sysroot $SYSROOT -gcc-toolchain $TOOLCHAIN -fuse-ld=$CLANG_BIN/ld.lld -B$CLANG_BIN"
export CFLAGS="-O3 -target aarch64-unknown-linux-gnu --sysroot $SYSROOT -fuse-ld=$CLANG_BIN/ld.lld"
export LDFLAGS="-fuse-ld=$CLANG_BIN/ld.lld"
cd libsodium
./autogen.sh -s -f
./configure --prefix=$PREFIX --with-sysroot=$SYSROOT --host=aarch64-unknown-linux-gnu "$@"

