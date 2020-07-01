package com.ionspin.kotlin.crypto.mac

import com.ionspin.kotlin.bignum.Endianness
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.crypto.util.hexColumsPrint

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 18-Jun-2020
 */
class Poly1305(key: UByteArray) {
    companion object {
        fun clampR(r: UByteArray) : UByteArray {
            val clamped = UByteArray(16) { r[it] }
            clamped[3] = r[3] and 0b00001111U
            clamped[7] = r[7] and 0b00001111U
            clamped[11] = r[11] and 0b00001111U
            clamped[15] = r[15] and 0b00001111U

            clamped[4] = r[4] and 0b11111100U
            clamped[8] = r[8] and 0b11111100U
            clamped[12] = r[12] and 0b11111100U
            return clamped

        }

        val P = BigInteger.fromUByteArray(
            ubyteArrayOf(
                0x03U, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xfbU
            )
        )
        val powersOfTwo = Array(129) {
            BigInteger.ONE shl it
        }
        val resultMask = (BigInteger.ONE shl 128) - 1
        //Doesn't have to be every power, just divisible by 8
        val twoToThe128 = BigInteger.ONE.shl(128)

        /**
         * Limit - stop poly calculating tag at desired index, ignored if 0
         */
        fun poly1305Authenticate(key: UByteArray, message: UByteArray) : UByteArray {
            val r = clampR(UByteArray(16) { key[it] })
            val s= UByteArray(16) { key[it + 16]}

            var accumulator = BigInteger.ZERO
            val rAsBigInt = BigInteger.fromUByteArray(r, Endianness.LITTLE) //TODO update BigInt to make this eraseable
            val sAsBigInt = BigInteger.fromUByteArray(s, Endianness.LITTLE)
            val blocks = message.size / 16
            val remainder = message.size % 16

            for (i in 0 until blocks) {
                val slice = message.sliceArray(i * 16 until i * 16 + 16)
                slice.hexColumsPrint()
                val blockAsInt = BigInteger.fromUByteArray(slice, Endianness.LITTLE) + powersOfTwo[128]
                accumulator += blockAsInt
                accumulator *= rAsBigInt
                accumulator %= P
            }
            if (remainder != 0) {
                val slice = message.sliceArray(blocks * 16 until blocks * 16 + remainder)
                val blockAsInt = BigInteger.fromUByteArray(slice, Endianness.LITTLE) + powersOfTwo[remainder * 8]
                accumulator += blockAsInt
                accumulator *= rAsBigInt
                accumulator %= P
            }

            accumulator += sAsBigInt
            accumulator = accumulator and resultMask
            val result = accumulator.toUByteArray(Endianness.BIG)
            result.reverse()
            return result


        }
    }
    var rAsBigInt = BigInteger.fromUByteArray(clampR(key.sliceArray(0 until 16)), Endianness.LITTLE)
    var sAsBigInt = BigInteger.fromUByteArray(key.sliceArray(16 until 32), Endianness.LITTLE)
    var accumulator = BigInteger.ZERO

    fun updateMac(data : UByteArray) {
        if (data.size != 16) {
            throw RuntimeException("Invalide block size, required 16, got ${data.size}")
        }
        val blockAsInt = BigInteger.fromUByteArray(data, Endianness.LITTLE) + powersOfTwo[128]
        accumulator += blockAsInt
        accumulator *= rAsBigInt
        accumulator %= P
    }

    fun finalizeMac(data: UByteArray = ubyteArrayOf()) : UByteArray{
        if (data.size != 0) {
            val blockAsInt = BigInteger.fromUByteArray(data, Endianness.LITTLE) + powersOfTwo[data.size * 8]
            accumulator += blockAsInt
            accumulator *= rAsBigInt
            accumulator %= P
        }
        accumulator += sAsBigInt
        accumulator = accumulator and resultMask
        val result = accumulator.toUByteArray(Endianness.BIG)
        result.reverse()
        return result
    }
}
