package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-Jun-2020
 */
class XSalsa20Test {
    @Test
    fun testXSalsa20() {
        // values from https://go.googlesource.com/crypto/+/master/salsa20/salsa20_test.go xSalsa20TestData
        assertTrue {
            val message = "Hello world!".encodeToUByteArray()
            val nonce = "24-byte nonce for xsalsa".encodeToUByteArray()
            val key = "this is 32-byte key for xsalsa20".encodeToUByteArray()
            val expected = ubyteArrayOf(0x00U, 0x2dU, 0x45U, 0x13U, 0x84U, 0x3fU, 0xc2U, 0x40U, 0xc4U, 0x01U, 0xe5U, 0x41U)
            val result = XSalsa20Pure.encrypt(key, nonce, message)
            result.contentEquals(expected)
        }

        assertTrue {
            val message = UByteArray(64) { 0U }
            val nonce = "24-byte nonce for xsalsa".encodeToUByteArray()
            val key = "this is 32-byte key for xsalsa20".encodeToUByteArray()
            val expected = ubyteArrayOf(
                0x48U, 0x48U, 0x29U, 0x7fU, 0xebU, 0x1fU, 0xb5U, 0x2fU, 0xb6U,
                0x6dU, 0x81U, 0x60U, 0x9bU, 0xd5U, 0x47U, 0xfaU, 0xbcU, 0xbeU, 0x70U,
                0x26U, 0xedU, 0xc8U, 0xb5U, 0xe5U, 0xe4U, 0x49U, 0xd0U, 0x88U, 0xbfU,
                0xa6U, 0x9cU, 0x08U, 0x8fU, 0x5dU, 0x8dU, 0xa1U, 0xd7U, 0x91U, 0x26U,
                0x7cU, 0x2cU, 0x19U, 0x5aU, 0x7fU, 0x8cU, 0xaeU, 0x9cU, 0x4bU, 0x40U,
                0x50U, 0xd0U, 0x8cU, 0xe6U, 0xd3U, 0xa1U, 0x51U, 0xecU, 0x26U, 0x5fU,
                0x3aU, 0x58U, 0xe4U, 0x76U, 0x48U
            )
            val result = XSalsa20Pure.encrypt(key, nonce, message)
            result.contentEquals(expected)
        }


    }
}