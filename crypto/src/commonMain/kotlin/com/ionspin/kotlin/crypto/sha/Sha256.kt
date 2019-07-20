/*
 * Copyright (c) 2019. Ugljesa Jovanovic
 */

package com.ionspin.crypto.sha

import com.ionspin.crypto.blake2b.chunked
import com.ionspin.crypto.blake2b.rotateRight

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */

@ExperimentalUnsignedTypes
class Sha256() {
    companion object {
        const val BLOCK_SIZE = 512
        const val W_SIZE = 64
        const val UINT_MASK = 0xFFFFFFFFU
        const val BYTE_MASK_FROM_ULONG = 0xFFUL
        const val BYTE_MASK_FROM_UINT = 0xFFU

    }

    var h0 = 0x6a09e667U
    var h1 = 0xbb67ae85U
    var h2 = 0x3c6ef372U
    var h3 = 0xa54ff53aU
    var h4 = 0x510e527fU
    var h5 = 0x9b05688cU
    var h6 = 0x1f83d9abU
    var h7 = 0x5be0cd19U

    val k = arrayOf(
        0x428a2f98U, 0x71374491U, 0xb5c0fbcfU, 0xe9b5dba5U, 0x3956c25bU, 0x59f111f1U, 0x923f82a4U, 0xab1c5ed5U,
        0xd807aa98U, 0x12835b01U, 0x243185beU, 0x550c7dc3U, 0x72be5d74U, 0x80deb1feU, 0x9bdc06a7U, 0xc19bf174U,
        0xe49b69c1U, 0xefbe4786U, 0x0fc19dc6U, 0x240ca1ccU, 0x2de92c6fU, 0x4a7484aaU, 0x5cb0a9dcU, 0x76f988daU,
        0x983e5152U, 0xa831c66dU, 0xb00327c8U, 0xbf597fc7U, 0xc6e00bf3U, 0xd5a79147U, 0x06ca6351U, 0x14292967U,
        0x27b70a85U, 0x2e1b2138U, 0x4d2c6dfcU, 0x53380d13U, 0x650a7354U, 0x766a0abbU, 0x81c2c92eU, 0x92722c85U,
        0xa2bfe8a1U, 0xa81a664bU, 0xc24b8b70U, 0xc76c51a3U, 0xd192e819U, 0xd6990624U, 0xf40e3585U, 0x106aa070U,
        0x19a4c116U, 0x1e376c08U, 0x2748774cU, 0x34b0bcb5U, 0x391c0cb3U, 0x4ed8aa4aU, 0x5b9cca4fU, 0x682e6ff3U,
        0x748f82eeU, 0x78a5636fU, 0x84c87814U, 0x8cc70208U, 0x90befffaU, 0xa4506cebU, 0xbef9a3f7U, 0xc67178f2U
    )

    fun digest(message: Array<UByte>) : Array<UByte> {

        val originalMessageSizeInBits = message.size * 8


        //K such that L + 1 + K + 64 is a multiple of 512
        val expandedRemainderOf512 = (originalMessageSizeInBits + 65) % BLOCK_SIZE
        val zeroAddAmount = when (expandedRemainderOf512) {
            0 -> 0
            else -> (BLOCK_SIZE - expandedRemainderOf512) / 8
        }
        val expansionArray = Array<UByte>(zeroAddAmount + 1) {
            when (it) {
                0 -> 0b10000000U //TODO This wont work if there the byte needs to be shared with the L (length) ULong
                else -> 0U
            }
        }

        val chunks = (message + expansionArray + originalMessageSizeInBits.toULong().toPaddedByteArray()).chunked(64)

        chunks.forEach { chunk ->
            val w = Array<UInt>(W_SIZE) {
                when (it) {
                    in 0 until 16 -> {
                        var collected = (chunk[(it * 4)].toUInt() shl 24) +
                            (chunk[(it * 4) + 1].toUInt() shl 16 ) +
                            (chunk[(it * 4) + 2].toUInt() shl 8 ) +
                            (chunk[(it * 4) + 3].toUInt())
                        collected
                    }
                    else -> 0U
                }
            }
            for (i in 16 until W_SIZE) {
                val s0 = scheduleSigma0(w[i - 15])
                val s1 = scheduleSigma1(w[i - 2])
                w[i] = w[i-16] + s0 + w[i - 7] + s1
            }

            var a = h0
            var b = h1
            var c = h2
            var d = h3
            var e = h4
            var f = h5
            var g = h6
            var h = h7

            for (i in 0 until W_SIZE) {
                val s1 = compressionSigma1(e)
                val ch = ch(e, f, g)
                val temp1 = h + s1 + ch + k[i] + w[i]
                val s0 = compressionSigma0(a)
                val maj = maj(a,b,c)
                val temp2 = s0 + maj
                h = g
                g = f
                f = e
                e = d + temp1
                d = c
                c = b
                b = a
                a = temp1 + temp2
            }

            h0 += a
            h1 += b
            h2 += c
            h3 += d
            h4 += e
            h5 += f
            h6 += g
            h7 += h

        }

        val digest =  h0.toPaddedByteArray() +
            h1.toPaddedByteArray() +
            h2.toPaddedByteArray() +
            h3.toPaddedByteArray() +
            h4.toPaddedByteArray() +
            h5.toPaddedByteArray() +
            h6.toPaddedByteArray() +
            h7.toPaddedByteArray()
        reset()
        return digest
    }

    private fun reset() {
        h0 = 0x6a09e667U
        h1 = 0xbb67ae85U
        h2 = 0x3c6ef372U
        h3 = 0xa54ff53aU
        h4 = 0x510e527fU
        h5 = 0x9b05688cU
        h6 = 0x1f83d9abU
        h7 = 0x5be0cd19U
    }

    private fun scheduleSigma0(value: UInt): UInt {
        return value.rotateRight(7) xor value.rotateRight(18) xor (value shr 3)
    }

    private fun scheduleSigma1(value : UInt) : UInt {
        return value.rotateRight(17) xor value.rotateRight(19) xor (value shr 10)
    }

    private fun compressionSigma0(a : UInt) : UInt {
        return (a rotateRight  2) xor (a rotateRight 13) xor (a rotateRight 22)
    }

    private fun compressionSigma1(e : UInt) : UInt {
        return (e rotateRight  6) xor (e rotateRight 11) xor (e rotateRight 25)
    }

    private fun ch(x : UInt, y : UInt, z : UInt) : UInt {
        return ((x and y) xor ((x xor UINT_MASK) and z))
    }

    private fun maj(x : UInt, y : UInt, z : UInt) : UInt {
        return (((x and y) xor (x and z) xor (y and z)))
    }
    
    

    private fun ULong.toPaddedByteArray(): Array<UByte> {
        val byteMask = BYTE_MASK_FROM_ULONG
        return Array(8) {
            when (it) {
                7 -> (this and byteMask).toUByte()
                6 -> ((this shr 8) and byteMask).toUByte()
                5 -> ((this shr 16) and byteMask).toUByte()
                4 -> ((this shr 24) and byteMask).toUByte()
                3 -> ((this shr 32) and byteMask).toUByte()
                2 -> ((this shr 40) and byteMask).toUByte()
                1 -> ((this shr 48) and byteMask).toUByte()
                0 -> ((this shr 54) and byteMask).toUByte()
                else -> throw RuntimeException("Invalid conversion")
            }
        }
    }

    private fun UInt.toPaddedByteArray(): Array<UByte> {
        val byteMask = BYTE_MASK_FROM_UINT
        return Array(4) {
            when (it) {
                3 -> (this and byteMask).toUByte()
                2 -> ((this shr 8) and byteMask).toUByte()
                1 -> ((this shr 16) and byteMask).toUByte()
                0 -> ((this shr 24) and byteMask).toUByte()
                else -> throw RuntimeException("Invalid conversion")
            }
        }
    }






}