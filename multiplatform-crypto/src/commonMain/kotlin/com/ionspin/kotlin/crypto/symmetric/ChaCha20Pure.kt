package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.symmetric.LatinDancesCommon.littleEndianInverted
import com.ionspin.kotlin.crypto.util.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Jun-2020
 */
internal class ChaCha20Pure {
    companion object {
        fun quarterRound(input: UIntArray, aPosition: Int, bPosition: Int, cPosition: Int, dPosition: Int) {
            input[aPosition] += input[bPosition]
            input[dPosition] = input[dPosition] xor input[aPosition]
            input[dPosition] = input[dPosition] rotateLeft 16

            input[cPosition] += input[dPosition]
            input[bPosition] = input[bPosition] xor input[cPosition]
            input[bPosition] = input[bPosition] rotateLeft 12

            input[aPosition] += input[bPosition]
            input[dPosition] = input[dPosition] xor input[aPosition]
            input[dPosition] = input[dPosition] rotateLeft 8

            input[cPosition] += input[dPosition]
            input[bPosition] = input[bPosition] xor input[cPosition]
            input[bPosition] = input[bPosition] rotateLeft 7
        }

        fun doubleRound(input: UIntArray) {
            quarterRound(input, 0, 4, 8, 12)
            quarterRound(input, 1, 5, 9, 13)
            quarterRound(input, 2, 6, 10, 14)
            quarterRound(input, 3, 7, 11, 15)

            quarterRound(input, 0, 5, 10, 15)
            quarterRound(input, 1, 6, 11, 12)
            quarterRound(input, 2, 7, 8, 13)
            quarterRound(input, 3, 4, 9, 14)
        }

        fun hash(initialState: UIntArray): UByteArray {
            val state = initialState.copyOf()
            for (i in 0 until 10) {
                doubleRound(state)
            }
            val result = UByteArray(64)
            for (i in 0 until 16) {
                littleEndianInverted(initialState[i] + state[i], result, i * 4)
            }
            return result
        }

        val sigma0_32 = 1634760805U //ubyteArrayOf(101U, 120U, 112U, 97U)
        val sigma1_32 = 857760878U //ubyteArrayOf(110U, 100U, 32U, 51U)
        val sigma2_32 = 2036477234U //ubyteArrayOf(50U, 45U, 98U, 121U)
        val sigma3_32 = 1797285236U //ubyteArrayOf(116U, 101U, 32U, 107U)

        fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, initialCounter: UInt): UByteArray {
            val ciphertext = UByteArray(message.size)
            val state = UIntArray(16) {
                when (it) {
                    0 -> sigma0_32
                    1 -> sigma1_32
                    2 -> sigma2_32
                    3 -> sigma3_32
                    4 -> key.fromLittleEndianArrayToUIntWithPosition(0)
                    5 -> key.fromLittleEndianArrayToUIntWithPosition(4)
                    6 -> key.fromLittleEndianArrayToUIntWithPosition(8)
                    7 -> key.fromLittleEndianArrayToUIntWithPosition(12)
                    8 -> key.fromLittleEndianArrayToUIntWithPosition(16)
                    9 -> key.fromLittleEndianArrayToUIntWithPosition(20)
                    10 -> key.fromLittleEndianArrayToUIntWithPosition(24)
                    11 -> key.fromLittleEndianArrayToUIntWithPosition(28)
                    12 -> initialCounter
                    13 -> nonce.fromLittleEndianArrayToUIntWithPosition(0)
                    14 -> nonce.fromLittleEndianArrayToUIntWithPosition(4)
                    15 -> nonce.fromLittleEndianArrayToUIntWithPosition(8)
                    else -> 0U
                }
            }
            val blocks = message.size / 64
            val remainder = message.size % 64
            for (i in 0 until blocks) {
                hash(state).xorWithPositionsAndInsertIntoArray(0, 64, message, i * 64, ciphertext, i * 64)
                state[12] += 1U
            }

            hash(state).xorWithPositionsAndInsertIntoArray(
                0, remainder,
                message, blocks * 64,
                ciphertext, blocks * 64
            )
            return ciphertext
        }
    }
}