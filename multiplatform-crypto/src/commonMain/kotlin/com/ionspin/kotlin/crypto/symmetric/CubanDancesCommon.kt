package com.ionspin.kotlin.crypto.symmetric

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Jun-2020
 */
fun littleEndian(
    input: UByteArray,
    byte0Position: Int,
    byte1Position: Int,
    byte2Position: Int,
    byte3Position: Int
): UInt {
    var uint = 0U
    uint = input[byte0Position].toUInt()
    uint = uint or (input[byte1Position].toUInt() shl 8)
    uint = uint or (input[byte2Position].toUInt() shl 16)
    uint = uint or (input[byte3Position].toUInt() shl 24)

    return uint
}

fun littleEndianInverted(
    input: UIntArray,
    startingPosition: Int,
    output: UByteArray,
    outputPosition: Int
) {
    output[outputPosition] = (input[startingPosition] and 0xFFU).toUByte()
    output[outputPosition + 1] = ((input[startingPosition] shr 8) and 0xFFU).toUByte()
    output[outputPosition + 2] = ((input[startingPosition] shr 16) and 0xFFU).toUByte()
    output[outputPosition + 3] = ((input[startingPosition] shr 24) and 0xFFU).toUByte()
}

fun littleEndianInverted(
    input: UInt,
    output: UByteArray,
    outputPosition: Int
) {
    output[outputPosition] = (input and 0xFFU).toUByte()
    output[outputPosition + 1] = ((input shr 8) and 0xFFU).toUByte()
    output[outputPosition + 2] = ((input shr 16) and 0xFFU).toUByte()
    output[outputPosition + 3] = ((input shr 24) and 0xFFU).toUByte()
}