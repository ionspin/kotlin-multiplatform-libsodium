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

./autogen.sh -s -f

if [ -z "$SYSROOT" ]; then
  case $(uname -a) in
    **Linux*x86_64**)
      echo "Linux env"
      GCC=${GCC:-gcc}
      GCC=gcc
      export CC=$(find $KONAN/dependencies -wholename *${ARCH/_/-}/bin/*$GCC | head -n1)
      ;;

    **Linux*aarch64**)
      echo "Linux ARM env"
      GCC=${GCC:-gcc}
      GCC=gcc
      export CC=$(find $KONAN/dependencies -wholename *${ARCH/_/-}/bin/*$GCC | head -n1)
      ;;


    **Darwin*x86_64**)
      echo "Darwin env"
      GCC=${GCC:-clang}
      export CC=$(find $KONAN/dependencies -wholename *${ARCH/_/-}/bin/*$GCC | head -n1)
      ;;

    **MSYS*x86_64**)
      echo "Msys env"
      GCC=clang.exe
      export CC=$(find $KONAN/dependencies -wholename *${ARCH}*/bin/*$GCC | head -n1)
      ;;

  esac


  echo "CC"
  echo $CC
fi

./configure --prefix=$PREFIX "$@"
