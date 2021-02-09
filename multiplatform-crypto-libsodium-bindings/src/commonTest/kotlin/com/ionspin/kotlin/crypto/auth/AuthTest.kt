package com.ionspin.kotlin.crypto.auth

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-Aug-2020
 */
class AuthTest {
    @Test
    fun testAuth() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ("I wonder if it would be possible" +
                    " to get some lyrics in these tests").encodeToUByteArray()

            val key = "We'll see1We'll see1We'll see123".encodeToUByteArray()

            val expected = "702beb4494a1d80795512668df016807ec052dc848a4c958eb1544ec1c8d6314".hexStringToUByteArray()

            val hashed = Auth.auth(message, key)
            println(hashed.toHexString())
            assertTrue {
                hashed.contentEquals(expected)
            }
            assertTrue { Auth.authVerify(hashed, message, key) }

            assertFalse {
                val tampered = hashed.copyOf()
                tampered[5] = 0U
                Auth.authVerify(tampered, message, key)
            }
        }

    }

    @Test
    fun testAuthHmacSha256() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ("I wonder if it would be possible" +
                    " to get some lyrics in these tests").encodeToUByteArray()

            val key = "We'll see1We'll see1We'll see123".encodeToUByteArray()

            val expected = "b1b3cf73089e04106a135629ba586b6c94b81b87162302f3f32b4fb797f82e8a".hexStringToUByteArray()

            val hashed = Auth.authHmacSha256(message, key)
            println(hashed.toHexString())
            assertTrue {
                hashed.contentEquals(expected)
            }
            assertTrue { Auth.authHmacSha256Verify(hashed, message, key) }

            assertFalse {
                val tampered = hashed.copyOf()
                tampered[5] = 0U
                Auth.authHmacSha256Verify(tampered, message, key)
            }
        }
    }

    @Test
    fun testAuthHmacSha512() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = ("I wonder if it would be possible" +
                    " to get some lyrics in these tests").encodeToUByteArray()

            val key = "We'll see1We'll see1We'll see123".encodeToUByteArray()

            val expected = ("702beb4494a1d80795512668df016807ec052dc848a4c958eb1544ec1c8d63145fd11513c2d" +
                    "aecb03780e2b8b121e87f0a171033489de92665d9a218f4ed9589").hexStringToUByteArray()

            val hashed = Auth.authHmacSha512(message, key)
            println(hashed.toHexString())
            assertTrue {
                hashed.contentEquals(expected)
            }
            println(hashed.toHexString())
            assertTrue { Auth.authHmacSha512Verify(hashed, message, key) }

            assertFalse {
                val tampered = hashed.copyOf()
                tampered[5] = 0U
                Auth.authHmacSha512Verify(tampered, message, key)
            }
        }
    }

    @Test
    fun simpleKeygenTest() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val authKey = Auth.authKeygen()
            assertTrue { authKey.size == crypto_auth_KEYBYTES }
            val auth256Key = Auth.authHmacSha256Keygen()
            assertTrue { auth256Key.size == crypto_auth_hmacsha256_KEYBYTES }
            val auth512Key = Auth.authHmacSha512Keygen()
            assertTrue { auth512Key.size == crypto_auth_hmacsha512_KEYBYTES }
        }


    }
}
