package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.util.*

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 14-Jun-2020
 */
class Salsa20 {
    companion object {
        fun coreHash(input: UByteArray) : UByteArray {
            val y0 = input.fromBigEndianArrayToUintWithPosition(0)
            val y1 = input.fromBigEndianArrayToUintWithPosition(4)
            val y2 = input.fromBigEndianArrayToUintWithPosition(8)
            val y3 = input.fromBigEndianArrayToUintWithPosition(12);

            val z1 = y1 xor ((y0 + y3) rotateLeft  7)
            val z2 = y2 xor ((z1 + y0) rotateLeft  9)
            val z3 = y3 xor ((z2 + z1) rotateLeft 13)
            val z0 = y0 xor ((z3 + z2) rotateLeft 18)
            val result = UByteArray(16)
            result.insertUIntAtPositionAsBigEndian(0, z0)
            result.insertUIntAtPositionAsBigEndian(4, z1)
            result.insertUIntAtPositionAsBigEndian(8, z2)
            result.insertUIntAtPositionAsBigEndian(12, z3)
            return result
        }
    }

}