package com.ionspin.kotlin.crypto.util

fun String.toCString(): UByteArray {
  val encoded = encodeToUByteArray()
  val cStr = UByteArray(encoded.size + 1)

  encoded.copyInto(cStr)

  LibsodiumUtil.memzero(encoded)

  return cStr
}