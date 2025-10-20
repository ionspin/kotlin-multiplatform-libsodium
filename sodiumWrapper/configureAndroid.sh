#!/bin/bash
cd libsodium
./autogen.sh -s -f
./configure --prefix=$PREFIX "$@"
