package com.ionspin.kotlin.crypto.ristretto255

import com.ionspin.kotlin.crypto.ed25519.HashToCurveAlgorithm
import com.ionspin.kotlin.crypto.util.LibsodiumUtil
import kotlin.UByteArray

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

expect abstract class Ristretto255LowLevel() {
  fun isValidPoint(encoded: UByteArray): Boolean
  fun addPoints(p: UByteArray, q: UByteArray): UByteArray
  fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray
  fun encodedPointFromHash(hash: UByteArray): UByteArray
  fun encodedPointFromString(ctx: String?, msg: UByteArray, hashAlg: HashToCurveAlgorithm): UByteArray
  fun encodedPointFromStringRo(ctx: String?, msg: UByteArray, hashAlg: HashToCurveAlgorithm): UByteArray
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
  fun scalarMultiplicationBase(n: UByteArray): UByteArray
}

object Ristretto255 : Ristretto255LowLevel() {
  fun add(p: Point, q: Point): Point =
    Point(addPoints(p.encoded, q.encoded))

  fun subtract(p: Point, q: Point): Point =
    Point(subtractPoints(p.encoded, q.encoded))

  fun pointFromHash(hash: UByteArray): Point = Point(encodedPointFromHash(hash))

  fun pointFromString(ctx: String?, msg: UByteArray, hashAlg: HashToCurveAlgorithm): Point =
    Point(encodedPointFromString(ctx, msg, hashAlg))

  fun pointFromStringRo(ctx: String?, msg: UByteArray, hashAlg: HashToCurveAlgorithm): Point =
   Point(encodedPointFromStringRo(ctx, msg, hashAlg))

  fun randomPoint(): Point = Point(randomEncodedPoint())

  fun randomScalar(): Scalar = Scalar(randomEncodedScalar())

  fun invert(scalar: Scalar): Scalar =
    Scalar(invertScalar(scalar.encoded))

  fun negate(scalar: Scalar): Scalar =
    Scalar(negateScalar(scalar.encoded))

  fun complement(scalar: Scalar): Scalar =
    Scalar(complementScalar(scalar.encoded))

  fun add(x: Scalar, y: Scalar): Scalar =
    Scalar(addScalars(x.encoded, y.encoded))

  fun subtract(x: Scalar, y: Scalar): Scalar =
    Scalar(subtractScalars(x.encoded, y.encoded))

  fun multiply(x: Scalar, y: Scalar): Scalar =
    Scalar(multiplyScalars(x.encoded, y.encoded))

  fun reduce(scalar: Scalar): Scalar =
    Scalar(reduceScalar(scalar.encoded))

  fun scalarMultiplication(p: Point, n: Scalar): Point =
    Point(scalarMultiplication(n.encoded, p.encoded))

  fun scalarMultiplicationBase(n: Scalar): Point =
    Point(scalarMultiplicationBase(n.encoded))

  data class Point(val encoded: UByteArray) {
    operator fun plus(q: Point): Point = add(this, q)
    operator fun minus(q: Point): Point = subtract(this, q)

    operator fun times(n: Scalar): Point = scalarMultiplication(this, n)

    fun toHex(): String = LibsodiumUtil.toHex(encoded)

    override fun equals(other: Any?): Boolean = (other as? Point)?.encoded?.contentEquals(encoded) == true
    override fun hashCode(): Int = encoded.contentHashCode()

    companion object {
      val IDENTITY: Point = Point(UByteArray(crypto_core_ristretto255_BYTES))
      val BASE: Point = scalarMultiplicationBase(Scalar.ONE)

      fun fromHash(hash: UByteArray): Point = pointFromHash(hash)

      fun random(): Point = randomPoint()

      fun multiplyBase(n: Scalar): Point = scalarMultiplicationBase(n)

      fun fromHex(hex: String): Point = Point(LibsodiumUtil.fromHex(hex))
    }
  }

  data class Scalar(val encoded: UByteArray) {
    operator fun plus(y: Scalar): Scalar = add(this, y)
    operator fun plus(y: UInt): Scalar = this + fromUInt(y)
    operator fun plus(y: ULong): Scalar = this + fromULong(y)

    operator fun minus(y: Scalar): Scalar = subtract(this, y)
    operator fun minus(y: UInt): Scalar = this - fromUInt(y)
    operator fun minus(y: ULong): Scalar = this - fromULong(y)

    operator fun times(y: Scalar): Scalar = multiply(this, y)
    operator fun times(y: UInt): Scalar = this * fromUInt(y)
    operator fun times(y: ULong): Scalar = this * fromULong(y)

    operator fun div(y: Scalar): Scalar = multiply(this, invert(y))
    operator fun div(y: UInt): Scalar = this / fromUInt(y)
    operator fun div(y: ULong): Scalar = this / fromULong(y)

    operator fun unaryMinus(): Scalar = negate(this)

    operator fun times(p: Point): Point = scalarMultiplication(p, this)

    fun reduce(): Scalar = reduce(this)
    fun invert(): Scalar = invert(this)
    fun complement(): Scalar = complement(this)

    fun multiplyWithBase(): Point = scalarMultiplicationBase(this)

    fun toHex(): String = LibsodiumUtil.toHex(encoded)

    override fun equals(other: Any?): Boolean = (other as? Scalar)?.encoded?.contentEquals(encoded) == true
    override fun hashCode(): Int = encoded.contentHashCode()

    companion object {
      val ZERO = fromUInt(0U)
      val ONE = fromUInt(1U)
      val TWO = fromUInt(2U)

      fun random(): Scalar = randomScalar()

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
          val encoded = LibsodiumUtil.fromHex(hex.padEnd(2 * crypto_core_ristretto255_NONREDUCEDSCALARBYTES, '0'))
          // Scalars are encoded in little-endian order, so the end can be padded with zeroes up to the size of a
          // non-reduced scalar. After decoding, it is reduced, to obtain a scalar in the canonical range
          return Scalar(reduceScalar(encoded))
        } else {
          val encoded = LibsodiumUtil.fromHex(hex.padEnd(2 * crypto_core_ristretto255_SCALARBYTES, '0'))
          // Scalars are encoded in little-endian order, so the end can be padded with zeroes up to the size of a
          // scalar.
          return Scalar(encoded)
        }
      }
    }
  }
}