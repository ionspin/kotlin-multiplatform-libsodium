#!/bin/sh
./configureMingw64.sh
echo "Configure done"
$GNU_MAKE -j4 -C libsodium clean
$GNU_MAKE -j4 -C libsodium
$GNU_MAKE -j4 -C libsodium install
#make -j4 -C libsodium clean
#make -j4 -C libsodium
#make -j4 -C libsodium install
