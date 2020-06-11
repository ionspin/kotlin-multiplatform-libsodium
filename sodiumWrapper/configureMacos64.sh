#!/bin/bash
## and then borrowed and slightly modified from https://github.com/datkt/sodium/configure
## borrowed from https://github.com/sodium-friends/sodium-native/blob/master/configure
PREFIX="${PREFIX:-$PWD/static-macos-x86-64}"
KONAN="${KONAN:-$HOME/.konan}"
ARCH=${ARCH:-$(uname -m)}
echo $PREFIX
echo $KONAN
echo $ARCH
cd libsodium

./autogen.sh -s -f

./configure --prefix=$PREFIX "$@"
