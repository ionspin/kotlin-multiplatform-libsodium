package com.ionspin.kotlin.crypto.stream

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.LibsodiumRandom
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.randombytes_SEEDBYTES
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.random.Random
import kotlin.random.nextUBytes
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 11-Oct-2020
 */
class StreamTest {

    val seed = UByteArray(randombytes_SEEDBYTES) { 0U }

    @Test
    fun testChaCha20Stream() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = "This is a cha cha message".encodeToUByteArray()
            val nonce = LibsodiumRandom.bufDeterministic(crypto_stream_chacha20_NONCEBYTES, seed)
            val key = Stream.chacha20Keygen()
            val stream = Stream.chacha20(message.size, nonce, key)
            val encryptedManually = message.mapIndexed { index, it -> it xor stream[index] }.toUByteArray()
            val encryptedUsingLibsodium = Stream.chacha20Xor(message, nonce, key)
            val encryptedUsingLibsodiumWithInitialCounter = Stream.chacha20XorIc(message, nonce, 0U, key)
            println(encryptedManually.toHexString())
            println(encryptedUsingLibsodium.toHexString())
            println(encryptedUsingLibsodiumWithInitialCounter.toHexString())
            println(seed.toHexString())
            assertTrue {
                encryptedManually.contentEquals(encryptedUsingLibsodium)
            }
            assertTrue {
                encryptedManually.contentEquals(encryptedUsingLibsodiumWithInitialCounter)
            }
            val decryptedUsingLibsodium = Stream.chacha20Xor(encryptedUsingLibsodium, nonce, key)
            assertTrue {
                decryptedUsingLibsodium.contentEquals(message)
            }
        }
    }

    @Test
    fun testChaCha20IetfStream() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val message = "This is a cha cha message".encodeToUByteArray()
            val nonce = LibsodiumRandom.bufDeterministic(crypto_stream_chacha20_ietf_NONCEBYTES, seed)
            val key = Stream.chacha20Keygen()
            val encryptedUsingLibsodium = Stream.chacha20IetfXor(message, nonce, key)
            val encryptedUsingLibsodiumWithInitialCounter = Stream.chacha20IetfXorIc(message, nonce, 0U, key)
            println(encryptedUsingLibsodium.toHexString())
            println(encryptedUsingLibsodiumWithInitialCounter.toHexString())
            assertTrue {
                encryptedUsingLibsodium.contentEquals(encryptedUsingLibsodiumWithInitialCounter)
            }
            val decryptedUsingLibsodium = Stream.chacha20IetfXor(encryptedUsingLibsodium, nonce, key)
            println(message.toHexString())
            println(decryptedUsingLibsodium.toHexString())
            assertTrue {
                decryptedUsingLibsodium.contentEquals(message)
            }
        }
    }

//    @Test
//    fun testXChaCha20IetfStream() = runTest {
//        LibsodiumInitializer.initializeWithCallback {
//            val message = "This is a cha cha message".encodeToUByteArray()
//            val nonce = LibsodiumRandom.bufDeterministic(crypto_stream_xchacha20_NONCEBYTES, seed)
//            val key = Stream.xChacha20Keygen()
//            val encryptedUsingLibsodium = Stream.xChacha20Xor(message, nonce, key)
//            val encryptedUsingLibsodiumWithInitialCounter = Stream.xChacha20XorIc(message, nonce, 0U, key)
//            println(encryptedUsingLibsodium.toHexString())
//            println(encryptedUsingLibsodiumWithInitialCounter.toHexString())
//            assertTrue {
//                encryptedUsingLibsodium.contentEquals(encryptedUsingLibsodiumWithInitialCounter)
//            }
//            val decryptedUsingLibsodium = Stream.xChacha20Xor(encryptedUsingLibsodium, nonce, key)
//            println(message.toHexString())
//            println(decryptedUsingLibsodium.toHexString())
//            assertTrue {
//                decryptedUsingLibsodium.contentEquals(message)
//            }
//        }
//    }
}
