package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.cinterop.*
import libsodium.*
import platform.posix.free
import platform.posix.malloc
import kotlin.test.Ignore
import kotlin.test.Test

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 13-Jul-2020
 */
class HelperTest {
    @Ignore //Just used for debugging pure implementation
    @Test
    fun longSha256() {
        for (target in 0L until 10L) {
            generateForRounds256(target)
        }
        for (target in 0L until 16_777_216L step 1_000_000L) {
            generateForRounds256(target)
        }
        generateForRounds256(16_777_216L)
    }

    fun generateForRounds256(target: Long) {
        val updateValue =
            "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmno".encodeToUByteArray().toCValues()
        val state = malloc(crypto_hash_sha256_state.size.convert())!!
            .reinterpret<crypto_hash_sha256_state>()

        crypto_hash_sha256_init(state)
        for (i in 0 until target) {
            crypto_hash_sha256_update(state, updateValue, updateValue.size.convert())
        }
        val result = UByteArray(32)
        val resultPinned = result.pin()
        crypto_hash_sha256_final(state, resultPinned.addressOf(0))
        println("$target to \"${result.toHexString()}\",")
        free(state)
    }
    @Ignore //Just used for debugging pure implementation
    @Test
    fun longSha512() {

        for (target in 0L until 10L) {
            generateForRounds512(target)
        }
        for (target in 0L until 16_777_216L step 1_000_000L) {
            generateForRounds512(target)
        }
        generateForRounds512(16_777_216L)
    }

    fun generateForRounds512(target: Long) {
        println("Wut")
        val updateValue =
            "abcdefghbcdefghicdefghijdefghijkefghijklfghijklmghijklmnhijklmno".encodeToUByteArray().toCValues()
        val state = malloc(crypto_hash_sha512_state.size.convert())!!
            .reinterpret<crypto_hash_sha512_state>()

        crypto_hash_sha512_init(state)
        for (i in 0 until target) {
            crypto_hash_sha512_update(state, updateValue, updateValue.size.convert())
        }
        val result = UByteArray(32)
        val resultPinned = result.pin()
        crypto_hash_sha512_final(state, resultPinned.addressOf(0))
        println("$target to \"${result.toHexString()}\",")
        free(state)
    }
}
