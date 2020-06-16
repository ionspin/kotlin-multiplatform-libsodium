package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.util.rotateLeft

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Jun-2020
 */
class ChaCha20Pure {
    companion object {
        fun quarterRound(input: UIntArray, aPosition: Int, bPosition: Int, cPosition: Int, dPosition: Int) {
            input[aPosition] += input[bPosition]; input[dPosition] = input[dPosition] xor input[aPosition]; input[dPosition] = input[dPosition] rotateLeft 16
            input[cPosition] += input[dPosition]; input[bPosition] = input[bPosition] xor input[cPosition]; input[bPosition] = input[bPosition] rotateLeft 12
            input[aPosition] += input[bPosition]; input[dPosition] = input[dPosition] xor input[aPosition]; input[dPosition] = input[dPosition] rotateLeft 8
            input[cPosition] += input[dPosition]; input[bPosition] = input[bPosition] xor input[cPosition]; input[bPosition] = input[bPosition] rotateLeft 7
        }

        fun rowRound(input: UIntArray) {
            Salsa20Pure.quarterRound(input, 0, 1, 2, 3)
            Salsa20Pure.quarterRound(input, 5, 6, 7, 4)
            Salsa20Pure.quarterRound(input, 10, 11, 8, 9)
            Salsa20Pure.quarterRound(input, 15, 12, 13, 14)
        }

        fun columnRound(input: UIntArray) {
            Salsa20Pure.quarterRound(input, 0, 4, 8, 12)
            Salsa20Pure.quarterRound(input, 5, 9, 13, 1)
            Salsa20Pure.quarterRound(input, 10, 14, 2, 6)
            Salsa20Pure.quarterRound(input, 15, 3, 7, 11)
        }

        fun doubleRound(input: UIntArray) {
            columnRound(input)
            rowRound(input)
        }
    }
}