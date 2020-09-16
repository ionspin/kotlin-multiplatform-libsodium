package com.ionspin.kotlin.crypto.kdf

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import kotlin.test.Test
import kotlin.test.assertFails
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 16/Sep/2020
 */
class KdfTest {
    @Test
    fun testKdf() {
        LibsodiumInitializer.initializeWithCallback {
            val masterKey = Kdf.keygen()
            val subkey1 = Kdf.deriveFromKey(1, crypto_kdf_BYTES_MAX, "test1234", masterKey)
            val subkey2 = Kdf.deriveFromKey(2, crypto_kdf_BYTES_MAX, "test1234", masterKey)

            assertTrue {
                subkey1.size == crypto_kdf_BYTES_MAX &&
                        subkey2.size == crypto_kdf_BYTES_MAX
            }

            val repeatSubkey1 = Kdf.deriveFromKey(1, crypto_kdf_BYTES_MAX, "test1234", masterKey)
            assertTrue {
                subkey1.contentEquals(repeatSubkey1)
            }
            assertFalse {
                subkey1.contentEquals(subkey2)
            }
        }
    }
}