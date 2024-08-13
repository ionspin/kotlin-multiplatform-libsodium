package com.ionspin.kotlin.crypto.ed25519

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.LibsodiumUtil
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class Ed25519Test {
  // Test vectors from https://github.com/jedisct1/libsodium/blob/master/test/default/core_ed25519.c
  val badEncodings = arrayOf(
    "0000000000000000000000000000000000000000000000000000000000000000",
    "0100000000000000000000000000000000000000000000000000000000000000",
    "0200000000000000000000000000000000000000000000000000000000000000",
    // Non canonical encodings
    "f6ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff7f",
    "f5ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff7f",
  )

  // Test vectors generated with sodium.js
  private val fromHashTestVectors  = arrayOf(
    "The sodium crypto library compiled to WebAssembly and pure JavaScript" to "50127230808e661643a11badce3c7220ab8de25f890528694f5155ab9c5d5339",
    "using Emscripten, with automatically generated wrappers to" to "546d28c823c00b7d1c355c2f3ed6faaed2b7f406b45568c83f14b00ad88c212d",
    "make it easy to use in web applications." to "69f1db12b628f6a0573c3ca440dbfe23c161d0a832cf4ca263ed33d15f337780",
    "The complete library weighs 188 KB" to "9ad2302066752dccc14e26d7da4bb7a839c211a7e46f558ff106c632106d8f71",
    "(minified, gzipped, includes pure JS + WebAssembly versions)" to "07787c86d65d8157b0e7bbf634c46e638f7dc88c560f60dfd1f5e85de64d681c",
    "and can run in a web browser as well as server-side." to "c33fedca4b8e6fdd7ecc4109ec624f81900d8c207e1497297f4ca87c154c0640",
  )

  // Test vectors generated with sodium.js
  private val fromUniformTestVectors  = arrayOf(
    "d5d31a04bf9cd6b4f3f014ab57f95d439a0bd741e71f1ecb580143235545255e" to "cb9fff40134270e80e0dcfcdc66aa4ebf02cd27c9d9d26adfdf78d0012ad1b62",
    "9d2e8fc82097672be7b3eb9b9ac74d0cd22087ce04a202a51e88702dceab88a1" to "6b1f76c95d2a201a25b77e73de875637e250acb8e22c44230b2c21bb5a45bb15",
    "7863e96b9a73ffb45df22e2692f395d24b5d7acf745c5fa536818fd00e3ba6f6" to "43be765b38f32d815203e1657c261545366f15b24af2a97694b9320b4a36c407",
    "1dfd309d25f6a2c6e0358cddf8dcf8c0fd018ccc7eb799d71fa829640cb5adb3" to "4c6b7015631f4063d85f3b195c7dfcb699a242b3449dc9b4abce8948df88a28e",
    "40bc69ec71804975dfcbd90b18ca5d9d0117b2e15cacf61e21960b33742a9d55" to "722b608070036ad2e82927338c5edca18f2d0e6f8ed393321ed3704269af1f29",
    "06c705d68c6224de01437208d7af2b3d933c1822abbe8f551b584cba073dc645" to "bb713b72bf705cc5a3daf299b787d28d47fdb39dc98a13082657b4137081624f",
    "e4307d89b2e904063a6a16c9cf09b4225e0b5f4dd2367f08b11bf7787fa626d3" to "f10dea3347ab6792fac62ee6825dad3e4915f15287506db8067ecdbf00f0f30a",
    "6f61fe548ff2cd7bc64d1d3cf4a707a8efba8247e906042d76e98b730f5d1d4d" to "5c43c14cb548b09ac8180c627bcf76bd7720aca21ef72cc13c5584e34ec23ff6",
  )

  // Test vectors generated with sodium.js
  private val basePointSmallMultiplesNoClamp = arrayOf(
    // This is the basepoint
    "5866666666666666666666666666666666666666666666666666666666666666",
    // These are small multiples of the basepoint
    "c9a3f86aae465f0e56513864510f3997561fa2c9e85ea21dc2292309f3cd6022",
    "d4b4f5784868c3020403246717ec169ff79e26608ea126a1ab69ee77d1b16712",
    "2f1132ca61ab38dff00f2fea3228f24c6c71d58085b80e47e19515cb27e8d047",
    "edc876d6831fd2105d0b4389ca2e283166469289146e2ce06faefe98b22548df",
    "f47e49f9d07ad2c1606b4d94067c41f9777d4ffda709b71da1d88628fce34d85",
    "b862409fb5c4c4123df2abf7462b88f041ad36dd6864ce872fd5472be363c5b1",
    "b4b937fca95b2f1e93e41e62fc3c78818ff38a66096fad6e7973e5c90006d321",
    "c0f1225584444ec730446e231390781ffdd2f256e9fcbeb2f40dddc2c2233d7f",
    "2c7be86ab07488ba43e8e03d85a67625cfbf98c8544de4c877241b7aaafc7fe3",
    "1337036ac32d8f30d4589c3c1c595812ce0fff40e37c6f5a97ab213f318290ad",
    "f9e42d2edc81d23367967352b47e4856b82578634e6c1de72280ce8b60ce70c0",
    "801f40eaaee1ef8723279a28b2cf4037b889dad222604678748b53ed0db0db92",
    "39289c8998fd69835c26b619e89848a7bf02b7cb7ad1ba1581cbc4506f2550ce",
    "df5c2eadc44c6d94a19a9aa118afe5ac3193d26401f76251f522ff042dfbcb92",
    "eb2767c137ab7ad8279c078eff116ab0786ead3a2e0f989f72c37f82f2969670",
  )

  // Test vectors generated with sodium.js
  // Because of clamping, the lowest three bits of the scalar are cleared to make it a multiple of the cofactor (8)
  // This makes two scalars yield the same result if they only differ in the lowest three bits. Because of this, for
  // these test vectors, the scalars used to obtain them are set to s = i * 4 + 1 where i is the index
  val basePointSmallMultiplesClamped = arrayOf(
    "693e47972caf527c7883ad1b39822f026f47db2ab0e1919955b8993aa04411d1",
    "693e47972caf527c7883ad1b39822f026f47db2ab0e1919955b8993aa04411d1",
    "c9877dfd1ccda6393a15aed8aba06798456798355f2a9da4e182fecd40290157",
    "c9877dfd1ccda6393a15aed8aba06798456798355f2a9da4e182fecd40290157",
    "33598cc739b5da481888220cc8d584ba6c385a4c489cb6305446fd78d591bd96",
    "33598cc739b5da481888220cc8d584ba6c385a4c489cb6305446fd78d591bd96",
    "b46a44945eaff85c6de56812f8b035f01f6680a6f37f74bc6aa992bd0ef2d32a",
    "b46a44945eaff85c6de56812f8b035f01f6680a6f37f74bc6aa992bd0ef2d32a",
    "31b532ff5943a5c73690714ceb6414b99d50b0daee2b2d994ea78adf7ac28f4f",
    "31b532ff5943a5c73690714ceb6414b99d50b0daee2b2d994ea78adf7ac28f4f",
    "140fcdae065d38753b1b563c61ab588da04e7b822a5575483d123fb96f30868d",
    "140fcdae065d38753b1b563c61ab588da04e7b822a5575483d123fb96f30868d",
    "cb920ec5b5ebcce941d7e84c9ade21d4628c2b020b3c32f7e1b07fbb825c145d",
    "cb920ec5b5ebcce941d7e84c9ade21d4628c2b020b3c32f7e1b07fbb825c145d",
    "d479546534fa8a146475623ca938efe42c6d561732088f8c3fd687ffff15210b",
    "d479546534fa8a146475623ca938efe42c6d561732088f8c3fd687ffff15210b",
  )

  @Test
  fun testRandomPoint() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      val p = Ed25519.Point.random()
      val q = Ed25519.Point.random()
      val r = Ed25519.Point.random()

      assertNotEquals(p, q)
      assertNotEquals(q, r)
      assertNotEquals(r, p)

      assertTrue { Ed25519.isValidPoint(p.encoded) }
      assertTrue { Ed25519.isValidPoint(q.encoded) }
      assertTrue { Ed25519.isValidPoint(r.encoded) }
    }
  }

  @Test
  fun testPointHexConversion() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      repeat(10) {
        val p = Ed25519.Point.random()

        assertEquals(p, Ed25519.Point.fromHex(p.toHex()))
      }
    }
  }

  @Test
  fun testIsValidPoint() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      for (hexEncoded in badEncodings) {
        assertFalse { Ed25519.isValidPoint(LibsodiumUtil.fromHex(hexEncoded)) }
      }

      for (hexEncoded in basePointSmallMultiplesNoClamp) {
        assertTrue { Ed25519.isValidPoint(LibsodiumUtil.fromHex(hexEncoded)) }
      }

      for (hexEncoded in basePointSmallMultiplesClamped) {
        assertTrue { Ed25519.isValidPoint(LibsodiumUtil.fromHex(hexEncoded)) }
      }
    }
  }

  @Test
  fun testPointArithmeticNoClamp() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      for (i in basePointSmallMultiplesNoClamp.indices) {
        val p = Ed25519.Point.fromHex(basePointSmallMultiplesNoClamp[i])
        val b = Ed25519.Point.BASE
        val n = Ed25519.Scalar.fromUInt(i.toUInt() + 1U)

        assertEquals(p, Ed25519.scalarMultiplicationBaseNoClamp(n))
        assertEquals(p, Ed25519.scalarMultiplicationNoClamp(b, n))
        assertEquals(p, n.multiplyWithBaseNoClamp())

        for (j in 0..<i) {
          val q = Ed25519.Point.fromHex(basePointSmallMultiplesNoClamp[j])
          val m = Ed25519.Scalar.fromUInt(i.toUInt() - j.toUInt())

          assertEquals(p, q + b.times(m, clamp = false))
          assertEquals(p, b.times(m, clamp = false) + q)
          assertEquals(q, p - b.times(m, clamp = false))
        }

        for (j in i + 1..<basePointSmallMultiplesNoClamp.size) {
          val q = Ed25519.Point.fromHex(basePointSmallMultiplesNoClamp[j])
          val m = Ed25519.Scalar.fromUInt(j.toUInt() - i.toUInt())

          assertEquals(q, p + b.times(m, clamp = false))
          assertEquals(q, b.times(m, clamp = false) + p)
          assertEquals(p, q - b.times(m, clamp = false))
        }
      }
    }
  }

  @Test
  fun testPointArithmeticClamped() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      for (i in basePointSmallMultiplesNoClamp.indices) {
        println("i: $i")
        val p = Ed25519.Point.fromHex(basePointSmallMultiplesClamped[i])
        val b = Ed25519.Point.BASE
        val n = Ed25519.Scalar.fromUInt(i.toUInt() * 4U + 1U)

        assertEquals(p, Ed25519.scalarMultiplicationBase(n))
        assertEquals(p, Ed25519.scalarMultiplication(b, n))
        assertEquals(p, n.multiplyWithBase())
      }
    }
  }

  @Test
  fun testPointFromHash() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      for ((input, output) in fromHashTestVectors) {
        val outPoint = Ed25519.Point.fromHex(output)
        val hash = Hash.sha512(input.encodeToUByteArray())

        assertEquals(outPoint, Ed25519.Point.fromHash(hash))
      }
    }
  }

  @Test
  fun testPointFromUniform() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      for ((input, output) in fromUniformTestVectors) {
        val outPoint = Ed25519.Point.fromHex(output)
        val uniform = LibsodiumUtil.fromHex(input)

        assertEquals(outPoint, Ed25519.Point.fromUniform(uniform))
      }
    }
  }

  @Test
  fun testRandomScalar() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      val x = Ed25519.Scalar.random()
      val y = Ed25519.Scalar.random()
      val z = Ed25519.Scalar.random()

      assertNotEquals(x, y)
      assertNotEquals(y, z)
      assertNotEquals(z, x)
    }
  }

  @Test
  fun testScalarHexConversion() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      repeat(10) {
        val p = Ed25519.Scalar.random()

        assertEquals(p, Ed25519.Scalar.fromHex(p.toHex()))
      }
    }
  }

  @Test
  fun testScalarArithmetic() = runTest {
    LibsodiumInitializer.initializeWithCallback {
      repeat(10) {
        val x = Ed25519.Scalar.random()
        val y = Ed25519.Scalar.random()

        val xInv = x.invert()
        val xComp = x.complement()
        val xNeg = -x

        assertEquals(Ed25519.Scalar.ZERO, x + xNeg)
        assertEquals(Ed25519.Scalar.ZERO, xNeg + x)
        assertEquals(Ed25519.Scalar.ZERO, x - x)

        assertEquals(x, x + Ed25519.Scalar.ZERO)
        assertEquals(x, Ed25519.Scalar.ZERO + x)

        assertEquals(Ed25519.Scalar.ONE, x + xComp)
        assertEquals(Ed25519.Scalar.ONE, xComp + x)

        assertEquals(Ed25519.Scalar.ONE, x * xInv)
        assertEquals(Ed25519.Scalar.ONE, xInv * x)
        assertEquals(Ed25519.Scalar.ONE, x / x)

        assertEquals(x, x * Ed25519.Scalar.ONE)
        assertEquals(x, Ed25519.Scalar.ONE * x)

        assertEquals(y - x, y + xNeg)
        assertEquals(y / x, y * xInv)
      }
    }
  }
}