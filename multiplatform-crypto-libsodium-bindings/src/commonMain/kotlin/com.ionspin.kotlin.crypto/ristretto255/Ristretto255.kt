package com.ionspin.kotlin.crypto.ristretto255

import com.ionspin.kotlin.crypto.util.LibsodiumUtil

/**
 * Created by Johannes Leupold
 * johannes.leupold@kobil.com
 * on 12-Aug-2024
 */

const val crypto_core_ristretto255_BYTES = 32
const val crypto_core_ristretto255_HASHBYTES = 64
const val crypto_core_ristretto255_SCALARBYTES = 32
const val crypto_core_ristretto255_NONREDUCEDSCALARBYTES = 64

const val crypto_scalarmult_ristretto255_BYTES = 32U
const val crypto_scalarmult_ristretto255_SCALARBYTES = 32U

expect object Ristretto255LowLevel {
    fun isValidPoint(encoded: UByteArray): Boolean
    fun addPoints(p: UByteArray, q: UByteArray): UByteArray
    fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray
    fun pointFromHash(hash: UByteArray): UByteArray
    fun randomPoint(): UByteArray
    fun randomScalar(): UByteArray
    fun invertScalar(scalar: UByteArray): UByteArray
    fun negateScalar(scalar: UByteArray): UByteArray
    fun complementScalar(scalar: UByteArray): UByteArray
    fun addScalars(x: UByteArray, y: UByteArray): UByteArray
    fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray
    fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray
    fun reduceScalar(scalar: UByteArray): UByteArray
    fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray
    fun scalarMultiplicationBase(n: UByteArray): UByteArray
}

object Ristretto255 {
    data class Point(val encoded: UByteArray) {
        companion object {
            val IDENTITY: Point = Point(UByteArray(crypto_core_ristretto255_BYTES))
            val BASE: Point = multiplyBase(Scalar.ONE)

            fun fromHash(hash: UByteArray): Point = Point(Ristretto255LowLevel.pointFromHash(hash))

            fun random(): Point = Point(Ristretto255LowLevel.randomPoint())

            fun multiplyBase(n: Scalar): Point = Point(Ristretto255LowLevel.scalarMultiplicationBase(n.encoded))

            fun fromHex(hex: String): Point = Point(LibsodiumUtil.fromHex(hex))
        }

        val isValid: Boolean
            get() = Ristretto255LowLevel.isValidPoint(this.encoded)

        operator fun plus(q: Point): Point = Point(Ristretto255LowLevel.addPoints(this.encoded, q.encoded))
        operator fun minus(q: Point): Point = Point(Ristretto255LowLevel.subtractPoints(this.encoded, q.encoded))

        operator fun times(n: Scalar): Point = Point(Ristretto255LowLevel.scalarMultiplication(n.encoded, this.encoded))

        fun toHex(): String = LibsodiumUtil.toHex(encoded)

        override fun equals(other: Any?): Boolean = (other as? Point)?.encoded?.contentEquals(encoded) == true
        override fun hashCode(): Int = encoded.contentHashCode()
    }

    data class Scalar(val encoded: UByteArray) {
        companion object {
            val ZERO = fromUInt(0U)
            val ONE = fromUInt(1U)
            val TWO = fromUInt(2U)

            fun random(): Scalar = Scalar(Ristretto255LowLevel.randomScalar())

            fun fromUInt(i: UInt): Scalar = fromULong(i.toULong())

            fun fromULong(l: ULong): Scalar {
                val encoded = UByteArray(crypto_core_ristretto255_SCALARBYTES)
                var rem = l

                for (i in 0..7) {
                    encoded[i] = (rem and 0xffU).toUByte()
                    rem = rem shr 8
                }

                return Scalar(encoded)
            }

            fun fromHex(hex: String): Scalar {
                require(hex.length <= 2 * crypto_core_ristretto255_NONREDUCEDSCALARBYTES) {
                    "Scalars must be at most $crypto_core_ristretto255_NONREDUCEDSCALARBYTES bytes long"
                }

                if (hex.length > 2 * crypto_core_ristretto255_SCALARBYTES) {
                    val encoded =
                        LibsodiumUtil.fromHex(hex.padEnd(2 * crypto_core_ristretto255_NONREDUCEDSCALARBYTES, '0'))
                    // Scalars are encoded in little-endian order, so the end can be padded with zeroes up to the size of a
                    // non-reduced scalar. After decoding, it is reduced, to obtain a scalar in the canonical range
                    return Scalar(Ristretto255LowLevel.reduceScalar(encoded))
                } else {
                    val encoded = LibsodiumUtil.fromHex(hex.padEnd(2 * crypto_core_ristretto255_SCALARBYTES, '0'))
                    // Scalars are encoded in little-endian order, so the end can be padded with zeroes up to the size of a
                    // scalar.
                    return Scalar(encoded)
                }
            }
        }

        operator fun plus(y: Scalar): Scalar = Scalar(Ristretto255LowLevel.addScalars(this.encoded, y.encoded))
        operator fun plus(y: UInt): Scalar = this + fromUInt(y)
        operator fun plus(y: ULong): Scalar = this + fromULong(y)

        operator fun minus(y: Scalar): Scalar = Scalar(Ristretto255LowLevel.subtractScalars(this.encoded, y.encoded))
        operator fun minus(y: UInt): Scalar = this - fromUInt(y)
        operator fun minus(y: ULong): Scalar = this - fromULong(y)

        operator fun times(y: Scalar): Scalar = Scalar(Ristretto255LowLevel.multiplyScalars(this.encoded, y.encoded))
        operator fun times(y: UInt): Scalar = this * fromUInt(y)
        operator fun times(y: ULong): Scalar = this * fromULong(y)

        operator fun div(y: Scalar): Scalar =
            Scalar(Ristretto255LowLevel.multiplyScalars(this.encoded, y.invert().encoded))

        operator fun div(y: UInt): Scalar = this / fromUInt(y)
        operator fun div(y: ULong): Scalar = this / fromULong(y)

        operator fun unaryMinus(): Scalar = Scalar(Ristretto255LowLevel.negateScalar(this.encoded))

        operator fun times(p: Point): Point = p.times(this)

        fun reduce(): Scalar = Scalar(Ristretto255LowLevel.reduceScalar(this.encoded))
        fun invert(): Scalar = Scalar(Ristretto255LowLevel.invertScalar(this.encoded))
        fun complement(): Scalar = Scalar(Ristretto255LowLevel.complementScalar(this.encoded))

        fun multiplyWithBase(): Point = Point.multiplyBase(this)

        fun toHex(): String = LibsodiumUtil.toHex(encoded)

        override fun equals(other: Any?): Boolean = (other as? Scalar)?.encoded?.contentEquals(encoded) == true
        override fun hashCode(): Int = encoded.contentHashCode()
    }
}