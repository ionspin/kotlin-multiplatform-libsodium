package com.ionspin.kotlin.crypto.mac

import com.ionspin.kotlin.bignum.Endianness
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.crypto.util.hexColumsPrint

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jun-2020
 */
class XChaCha20Poly1305Pure {
    companion object {
        fun clampR(r: UByteArray) {
            r[3] = r[3] and 0b00001111U
            r[7] = r[7] and 0b00001111U
            r[11] = r[11] and 0b00001111U
            r[15] = r[15] and 0b00001111U

            r[4] = r[4] and 0b11111100U
            r[8] = r[8] and 0b11111100U
            r[12] = r[12] and 0b11111100U

        }

        val P = BigInteger.fromUByteArray(
            ubyteArrayOf(
                0x03U, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xffU, 0xfbU
            ).toTypedArray() //TODO remove to typed array after bignum update
        )
        val powersOfTwo = Array(129) {
            BigInteger.ONE shl it
        }
        val resultMask = (BigInteger.ONE shl 129) - 1
        //Doesn't have to be every power, just divisible by 8
        val twoToThe128 = BigInteger.ONE.shl(128)

        fun poly1305Authenticate(key: UByteArray, message: UByteArray) : UByteArray {
            val r = UByteArray(16) { key[it] }
            val s= UByteArray(16) { key[it + 16]}
            clampR(r)
            println("P: ${P.toString(16)}")
            var accumulator = BigInteger.ZERO
            val rAsBigInt = BigInteger.fromUByteArray(r, Endianness.LITTLE)
            val sAsBigInt = BigInteger.fromUByteArray(s, Endianness.LITTLE)
            val blocks = message.size / 16
            val remainder = message.size % 16

            for (i in 0 until blocks) {
                val slice = message.sliceArray(i * 16 until i * 16 + 16)
                slice.hexColumsPrint()
                val blockAsInt = BigInteger.fromUByteArray(slice, Endianness.LITTLE) + powersOfTwo[128]
                println("blockAsInt: ${blockAsInt.toString(16)}")
                accumulator += blockAsInt
                println("Accumlator: ${accumulator.toString(16)}")
                accumulator *= rAsBigInt
                println("Accumlator: ${accumulator.toString(16)}")
                accumulator %= P
                println("Accumlator: ${accumulator.toString(16)}")
            }

            val slice = message.sliceArray(blocks * 16 until blocks * 16 + remainder)
            val blockAsInt = BigInteger.fromUByteArray(slice, Endianness.LITTLE)  + powersOfTwo[remainder * 8]
            println("blockAsInt: ${blockAsInt.toString(16)}")
            accumulator += blockAsInt
            println("Accumlator: ${accumulator.toString(16)}")
            accumulator *= rAsBigInt
            println("Accumlator: ${accumulator.toString(16)}")
            accumulator %= P
            println("Accumlator: ${accumulator.toString(16)}")


            println("Result mask: ${resultMask.toString(2)}")
            accumulator += sAsBigInt
            accumulator = accumulator and resultMask
            println("Accumlator: ${accumulator.toString(16)}")
            val result = accumulator.toUByteArray(Endianness.BIG)
            result.reverse()
            return result


        }
    }
}