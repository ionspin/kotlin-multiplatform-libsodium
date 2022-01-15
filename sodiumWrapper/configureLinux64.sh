#!/bin/bash
## and then borrowed and slightly modified from https://github.com/datkt/sodium/configure
## borrowed from https://github.com/sodium-friends/sodium-native/blob/master/configure
PREFIX="${PREFIX:-$PWD/static-linux-x86-64}"
KONAN="${KONAN:-$HOME/.konan}"
ARCH=${ARCH:-$(uname -m)}
echo $PREFIX
echo $KONAN
echo $ARCH
cd libsodium

export CC=$HOME/.konan/dependencies/x86_64-unknown-linux-gnu-gcc-8.3.0-glibc-2.19-kernel-4.9-2/bin/x86_64-unknown-linux-gnu-gcc

./autogen.sh -s -f
./configure --prefix=$PREFIX "$@"
