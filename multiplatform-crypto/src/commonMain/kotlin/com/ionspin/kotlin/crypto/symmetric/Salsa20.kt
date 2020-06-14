package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.util.rotateLeft

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
class Salsa20 {
    companion object {
        fun quarterRound(input: UIntArray, y0position: Int, y1position: Int, y2position: Int, y3position: Int) {
            input[y1position] = input[y1position] xor ((input[y0position] + input[y3position]) rotateLeft 7)
            input[y2position] = input[y2position] xor ((input[y1position] + input[y0position]) rotateLeft 9)
            input[y3position] = input[y3position] xor ((input[y2position] + input[y1position]) rotateLeft 13)
            input[y0position] = input[y0position] xor ((input[y3position] + input[y2position]) rotateLeft 18)
        }

        fun rowRound(input: UIntArray) {
            quarterRound(input, 0, 1, 2, 3)
            quarterRound(input, 5, 6, 7, 4)
            quarterRound(input, 10, 11, 8, 9)
            quarterRound(input, 15, 12, 13, 14)
        }

        fun columnRound(input: UIntArray) {
            quarterRound(input, 0, 4, 8, 12)
            quarterRound(input, 5, 9, 13, 1)
            quarterRound(input, 10, 14, 2, 6)
            quarterRound(input, 15, 3, 7, 11)
        }

        fun doubleRound(input: UIntArray) {
            columnRound(input)
            rowRound(input)
        }

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

        fun hash(input: UByteArray): UByteArray {
            val state = UIntArray(16) {
                littleEndian(input, (it * 4) + 0, (it * 4) + 1, (it * 4) + 2, (it * 4) + 3)
            }
            val initialState = state.copyOf()
            for (i in 0 until 10) {
                doubleRound(state)
            }
            val result = UByteArray(64)
            for (i in 0 until 16) {
                littleEndianInverted(initialState[i] + state[i], result, i * 4)
            }
            return result
        }

        val sigma0_32 = ubyteArrayOf(101U, 120U, 112U, 97U)
        val sigma1_32 = ubyteArrayOf(110U, 100U, 32U, 51U)
        val sigma2_32 = ubyteArrayOf(50U, 45U, 98U, 121U)
        val sigma3_32 = ubyteArrayOf(116U, 101U, 32U, 107U)

        val sigma0_16 = ubyteArrayOf(101U, 120U, 112U, 97U)
        val sigma1_16 = ubyteArrayOf(110U, 100U, 32U, 49U)
        val sigma2_16 = ubyteArrayOf(54U, 45U, 98U, 121U)
        val sigma3_16 = ubyteArrayOf(116U, 101U, 32U, 107U)

        fun expansion16(k: UByteArray, n: UByteArray) : UByteArray {
            return hash(sigma0_16 + k + sigma1_16 + n + sigma2_16 + k + sigma3_16)
        }

        fun expansion32(k:UByteArray, n: UByteArray) : UByteArray {
            return hash(sigma0_32 + k.slice(0 until 16) + sigma1_32 + n + sigma2_32 + k.slice(16 until 32) + sigma3_32)
        }
    }

}