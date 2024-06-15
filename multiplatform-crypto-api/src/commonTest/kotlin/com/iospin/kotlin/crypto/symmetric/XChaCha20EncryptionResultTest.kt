package com.iospin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.symmetric.XChaCha20EncryptionResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class XChaCha20EncryptionResultTest {
    @Test
    fun testEquality() {
        val firstResult = XChaCha20EncryptionResult(
            nonce = ubyteArrayOf(0x12u, 0x34u, 0x56u),
            encryptionData = ubyteArrayOf(0x78u, 0x9Au),
        )
        val secondResult = XChaCha20EncryptionResult(
            nonce = ubyteArrayOf(0x12u, 0x34u, 0x56u),
            encryptionData = ubyteArrayOf(0x78u, 0x9Au),
        )
        val differingResult = XChaCha20EncryptionResult(
            nonce = ubyteArrayOf(0u),
            encryptionData = ubyteArrayOf(0u),
        )

        assertEquals(firstResult, secondResult)
        assertNotEquals(firstResult, differingResult)
        assertNotEquals(secondResult, differingResult)
    }
}
