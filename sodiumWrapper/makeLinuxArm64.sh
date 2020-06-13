./configureLinuxArm64.sh
make -j32 -C libsodium clean
make -j32 -C libsodium
make -j32 -C libsodium install
