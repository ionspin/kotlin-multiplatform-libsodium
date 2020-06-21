package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.util.fromLittleEndianArrayToUInt
import com.ionspin.kotlin.crypto.util.fromLittleEndianArrayToUIntWithPosition
import com.ionspin.kotlin.crypto.util.overwriteWithZeroes
import com.ionspin.kotlin.crypto.util.xorWithPositionsAndInsertIntoArray

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Jun-2020
 */
internal class XSalsa20Pure {
    companion object {
        fun hSalsa(key: UByteArray, nonce: UByteArray): UIntArray {
            val state = UIntArray(16) {
                when (it) {
                    0 -> Salsa20Pure.sigma0_32.fromLittleEndianArrayToUInt()
                    1 -> key.fromLittleEndianArrayToUIntWithPosition(0)
                    2 -> key.fromLittleEndianArrayToUIntWithPosition(4)
                    3 -> key.fromLittleEndianArrayToUIntWithPosition(8)
                    4 -> key.fromLittleEndianArrayToUIntWithPosition(12)
                    5 -> Salsa20Pure.sigma1_32.fromLittleEndianArrayToUInt()
                    6 -> nonce.fromLittleEndianArrayToUIntWithPosition(0)
                    7 -> nonce.fromLittleEndianArrayToUIntWithPosition(4)
                    8 -> nonce.fromLittleEndianArrayToUIntWithPosition(8)
                    9 -> nonce.fromLittleEndianArrayToUIntWithPosition(12)
                    10 -> Salsa20Pure.sigma2_32.fromLittleEndianArrayToUInt()
                    11 -> key.fromLittleEndianArrayToUIntWithPosition(16)
                    12 -> key.fromLittleEndianArrayToUIntWithPosition(20)
                    13 -> key.fromLittleEndianArrayToUIntWithPosition(24)
                    14 -> key.fromLittleEndianArrayToUIntWithPosition(28)
                    15 -> Salsa20Pure.sigma3_32.fromLittleEndianArrayToUInt()
                    else -> throw RuntimeException("Invalid index $it")
                }
            }
            for (i in 0 until 10) {
                Salsa20Pure.doubleRound(state)
            }
            val result = UIntArray(8) {
                when (it) {
                    0 -> state[0]
                    1 -> state[5]
                    2 -> state[10]
                    3 -> state[15]
                    4 -> state[6]
                    5 -> state[7]
                    6 -> state[8]
                    7 -> state[9]
                    else -> throw RuntimeException("Invalid index $it")
                }
            }
            return result

        }

        fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray): UByteArray {
            if (nonce.size != 24) {
                throw RuntimeException("Invalid nonce size. required 192 bits, got ${nonce.size * 8}")
            }
            val ciphertext = UByteArray(message.size)
            val hSalsaKey = hSalsa(key, nonce)
            val state = UIntArray(16) {
                when (it) {
                    0 -> Salsa20Pure.sigma0_32_uint
                    1 -> hSalsaKey[0]
                    2 -> hSalsaKey[1]
                    3 -> hSalsaKey[2]
                    4 -> hSalsaKey[3]
                    5 -> Salsa20Pure.sigma1_32_uint
                    6 -> nonce.fromLittleEndianArrayToUIntWithPosition(16) //Last 63 bit of 192 bit nonce
                    7 -> nonce.fromLittleEndianArrayToUIntWithPosition(20)
                    8 -> 0U
                    9 -> 0U
                    10 -> Salsa20Pure.sigma2_32_uint
                    11 -> hSalsaKey[4]
                    12 -> hSalsaKey[5]
                    13 -> hSalsaKey[6]
                    14 -> hSalsaKey[7]
                    15 -> Salsa20Pure.sigma3_32_uint
                    else -> 0U
                }
            }
            hSalsaKey.overwriteWithZeroes()
            val blocks = message.size / 64
            val remainder = message.size % 64
            for (i in 0 until blocks) {
                Salsa20Pure.hash(state).xorWithPositionsAndInsertIntoArray(0, 64, message, i * 64, ciphertext, i * 64)
                state[8] += 1U
                if (state[8] == 0U) {
                    state[9] += 1U
                }
            }

            Salsa20Pure.hash(state).xorWithPositionsAndInsertIntoArray(
                0, remainder,
                message, blocks * 64,
                ciphertext, blocks * 64)
            state.overwriteWithZeroes()
            return ciphertext
        }

    }

}