package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.util.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
internal class Salsa20Pure {
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

        internal val sigma0_32_uint = 1634760805U //ubyteArrayOf(101U, 120U, 112U, 97U)
        internal val sigma1_32_uint = 857760878U //ubyteArrayOf(110U, 100U, 32U, 51U)
        internal val sigma2_32_uint = 2036477234U //ubyteArrayOf(50U, 45U, 98U, 121U)
        internal val sigma3_32_uint = 1797285236U //ubyteArrayOf(116U, 101U, 32U, 107U)

        val sigma0_32 = ubyteArrayOf(101U, 120U, 112U, 97U)
        val sigma1_32 = ubyteArrayOf(110U, 100U, 32U, 51U)
        val sigma2_32 = ubyteArrayOf(50U, 45U, 98U, 121U)
        val sigma3_32 = ubyteArrayOf(116U, 101U, 32U, 107U)

        val tau0_16 = ubyteArrayOf(101U, 120U, 112U, 97U)
        val tau1_16 = ubyteArrayOf(110U, 100U, 32U, 49U)
        val tau2_16 = ubyteArrayOf(54U, 45U, 98U, 121U)
        val tau3_16 = ubyteArrayOf(116U, 101U, 32U, 107U)

        fun expansion16(k: UByteArray, n: UByteArray) : UByteArray {
            return hash((tau0_16 + k + tau1_16 + n + tau2_16 + k + tau3_16).fromLittleEndianToUInt())
        }

        fun expansion32(key :UByteArray, nonce : UByteArray) : UByteArray {
            return hash((sigma0_32 + key.slice(0 until 16) + sigma1_32 + nonce + sigma2_32 + key.slice(16 until 32) + sigma3_32).fromLittleEndianToUInt())
        }

        fun encrypt(key : UByteArray, nonce: UByteArray, message: UByteArray) : UByteArray {
            val ciphertext = UByteArray(message.size)
            val state = UIntArray(16) {
                when (it) {
                    0 -> sigma0_32_uint
                    1 -> key.fromLittleEndianArrayToUIntWithPosition(0)
                    2 -> key.fromLittleEndianArrayToUIntWithPosition(4)
                    3 -> key.fromLittleEndianArrayToUIntWithPosition(8)
                    4 -> key.fromLittleEndianArrayToUIntWithPosition(12)
                    5 -> sigma1_32_uint
                    6 -> nonce.fromLittleEndianArrayToUIntWithPosition(0)
                    7 -> nonce.fromLittleEndianArrayToUIntWithPosition(4)
                    8 -> 0U
                    9 -> 0U
                    10 -> sigma2_32_uint
                    11 -> key.fromLittleEndianArrayToUIntWithPosition(16)
                    12 -> key.fromLittleEndianArrayToUIntWithPosition(20)
                    13 -> key.fromLittleEndianArrayToUIntWithPosition(24)
                    14 -> key.fromLittleEndianArrayToUIntWithPosition(28)
                    15 -> sigma3_32_uint
                    else -> 0U
                }
            }
            val blocks = message.size / 64
            val remainder = message.size % 64
            for (i in 0 until blocks) {
                hash(state).xorWithPositionsAndInsertIntoArray(0, 64, message, i * 64, ciphertext, i * 64)
                state[8] += 1U
                if (state[8] == 0U) {
                    state[9] += 1U
                }
            }

            hash(state).xorWithPositionsAndInsertIntoArray(
                0, remainder,
                message, blocks * 64,
                ciphertext, blocks * 64)

            return ciphertext
        }

        fun decrypt(key : UByteArray, nonce: UByteArray, ciphertext: UByteArray) : UByteArray {
            return encrypt(key, nonce, ciphertext)
        }
    }

}