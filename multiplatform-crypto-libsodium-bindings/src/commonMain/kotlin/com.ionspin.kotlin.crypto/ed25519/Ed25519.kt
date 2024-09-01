package com.ionspin.kotlin.crypto.ed25519

import com.ionspin.kotlin.crypto.util.LibsodiumUtil

/**
 * Created by Johannes Leupold
 * johannes.leupold@kobil.com
 * on 12-Aug-2024
 */

const val crypto_core_ed25519_BYTES = 32
const val crypto_core_ed25519_UNIFORMBYTES = 32
const val crypto_core_ed25519_HASHBYTES = 64
const val crypto_core_ed25519_SCALARBYTES = 32
const val crypto_core_ed25519_NONREDUCEDSCALARBYTES = 64

const val crypto_scalarmult_ed25519_BYTES = 32U
const val crypto_scalarmult_ed25519_SCALARBYTES = 32U

expect object Ed25519LowLevel {
    fun isValidPoint(encoded: UByteArray): Boolean
    fun addPoints(p: UByteArray, q: UByteArray): UByteArray
    fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray
    fun encodedPointFromUniform(uniform: UByteArray): UByteArray
    fun randomEncodedPoint(): UByteArray
    fun randomEncodedScalar(): UByteArray
    fun invertScalar(scalar: UByteArray): UByteArray
    fun negateScalar(scalar: UByteArray): UByteArray
    fun complementScalar(scalar: UByteArray): UByteArray
    fun addScalars(x: UByteArray, y: UByteArray): UByteArray
    fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray
    fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray
    fun reduceScalar(scalar: UByteArray): UByteArray
    fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray
    fun scalarMultiplicationNoClamp(n: UByteArray, p: UByteArray): UByteArray
    fun scalarMultiplicationBase(n: UByteArray): UByteArray
    fun scalarMultiplicationBaseNoClamp(n: UByteArray): UByteArray
}

object Ed25519 {

    data class Point(private val encoded: UByteArray) {

        companion object {
            val IDENTITY: Point = Point(UByteArray(crypto_core_ed25519_BYTES))
            val BASE: Point = multiplyBaseNoClamp(Scalar.ONE)

            fun fromUniform(uniform: UByteArray): Point = Point(Ed25519LowLevel.encodedPointFromUniform(uniform))

            fun random(): Point = Point(Ed25519LowLevel.randomEncodedPoint())

            fun multiplyBase(n: Scalar): Point = Point(Ed25519LowLevel.scalarMultiplicationBase(n.encoded))

            fun multiplyBaseNoClamp(n: Scalar): Point =
                Point(Ed25519LowLevel.scalarMultiplicationBaseNoClamp(n.encoded))

            fun fromHex(hex: String): Point = Point(LibsodiumUtil.fromHex(hex))

            fun isValid(point: Point) : Boolean = Ed25519LowLevel.isValidPoint(point.encoded)
        }

        operator fun plus(q: Point): Point = Point(Ed25519LowLevel.addPoints(this.encoded, q.encoded))
        operator fun minus(q: Point): Point = Point(Ed25519LowLevel.subtractPoints(this.encoded, q.encoded))

        operator fun times(n: Scalar): Point = Point(Ed25519LowLevel.scalarMultiplication(n.encoded, this.encoded))
        fun times(n: Scalar, clamp: Boolean): Point =
            if (clamp) times(n) else Point(Ed25519LowLevel.scalarMultiplicationNoClamp(n.encoded, this.encoded))

        fun toHex(): String = LibsodiumUtil.toHex(encoded)

        override fun equals(other: Any?): Boolean = (other as? Point)?.encoded?.contentEquals(encoded) == true
        override fun hashCode(): Int = encoded.contentHashCode()
    }

    data class Scalar(val encoded: UByteArray) {

        companion object {
            val ZERO = fromUInt(0U)
            val ONE = fromUInt(1U)
            val TWO = fromUInt(2U)


            fun random(): Scalar = Scalar(Ed25519LowLevel.randomEncodedScalar())

            fun invert(scalar: Scalar): Scalar = Scalar(Ed25519LowLevel.invertScalar(scalar.encoded))

            fun reduce(scalar: Scalar): Scalar = Scalar(Ed25519LowLevel.reduceScalar(scalar.encoded))

            fun complement(scalar: Scalar) : Scalar = Scalar(Ed25519LowLevel.complementScalar(scalar.encoded))

            fun fromUInt(i: UInt): Scalar = fromULong(i.toULong())

            fun fromULong(l: ULong): Scalar {
                val encoded = UByteArray(crypto_core_ed25519_SCALARBYTES)
                var rem = l

                for (i in 0..7) {
                    encoded[i] = (rem and 0xffU).toUByte()
                    rem = rem shr 8
                }

                return Scalar(encoded)
            }

            fun fromHex(hex: String): Scalar {
                require(hex.length <= 2 * crypto_core_ed25519_NONREDUCEDSCALARBYTES) {
                    "Scalars must be at most $crypto_core_ed25519_NONREDUCEDSCALARBYTES bytes long"
                }

                if (hex.length > 2 * crypto_core_ed25519_SCALARBYTES) {
                    val encoded = LibsodiumUtil.fromHex(hex.padEnd(2 * crypto_core_ed25519_NONREDUCEDSCALARBYTES, '0'))
                    // Scalars are encoded in little-endian order, so the end can be padded with zeroes up to the size of a
                    // non-reduced scalar. After decoding, it is reduced, to obtain a scalar in the canonical range
                    return Scalar(Ed25519LowLevel.reduceScalar(encoded))
                } else {
                    val encoded = LibsodiumUtil.fromHex(hex.padEnd(2 * crypto_core_ed25519_SCALARBYTES, '0'))
                    // Scalars are encoded in little-endian order, so the end can be padded with zeroes up to the size of a
                    // scalar.
                    return Scalar(encoded)
                }
            }
        }

        operator fun plus(y: Scalar): Scalar = Scalar(Ed25519LowLevel.addScalars(this.encoded, y.encoded))
        operator fun plus(y: UInt): Scalar = this + fromUInt(y)
        operator fun plus(y: ULong): Scalar = this + fromULong(y)

        operator fun minus(y: Scalar): Scalar = Scalar(Ed25519LowLevel.subtractScalars(this.encoded, y.encoded))
        operator fun minus(y: UInt): Scalar = this - fromUInt(y)
        operator fun minus(y: ULong): Scalar = this - fromULong(y)

        operator fun times(y: Scalar): Scalar = Scalar(Ed25519LowLevel.multiplyScalars(this.encoded, y.encoded))
        operator fun times(y: UInt): Scalar = this * fromUInt(y)
        operator fun times(y: ULong): Scalar = this * fromULong(y)

        operator fun div(y: Scalar): Scalar = times(invert(y))
        operator fun div(y: UInt): Scalar = this / fromUInt(y)
        operator fun div(y: ULong): Scalar = this / fromULong(y)

        operator fun unaryMinus(): Scalar = Scalar(Ed25519LowLevel.negateScalar(this.encoded))

        operator fun times(p: Point): Point = p.times(this)
        fun times(p: Point, clamp: Boolean): Point =
            if (clamp) p.times(this) else p.times(this, clamp)

        fun reduce(): Scalar = reduce(this)
        fun invert(): Scalar = invert(this)
        fun complement(): Scalar = complement(this)

        fun multiplyWithBase(): Point = Point.multiplyBase(this)

        fun multiplyWithBaseNoClamp(): Point = Point.multiplyBaseNoClamp(this)

        fun toHex(): String = LibsodiumUtil.toHex(encoded)

        override fun equals(other: Any?): Boolean = (other as? Scalar)?.encoded?.contentEquals(encoded) == true
        override fun hashCode(): Int = encoded.contentHashCode()


    }
}