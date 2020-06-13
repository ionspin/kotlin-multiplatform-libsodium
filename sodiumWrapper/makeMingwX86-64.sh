#!/bin/sh
./configureMingw64.sh
echo "Configure done"
make -j32 -C libsodium clean
make -j32 -C libsodium
make -j32 -C libsodium install
