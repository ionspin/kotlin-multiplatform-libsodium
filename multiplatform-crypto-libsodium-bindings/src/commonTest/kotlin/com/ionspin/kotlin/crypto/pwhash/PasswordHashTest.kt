package com.ionspin.kotlin.crypto.pwhash

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.random.Random
import kotlin.random.nextUBytes
import com.ionspin.kotlin.crypto.util.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Sep-2020
 */
class PasswordHashTest {
    @Test
    fun testPasswordHash() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val randomBytes = Random(0).nextUBytes(crypto_pwhash_SALTBYTES)
            val password = "correct horse battery staple"
            val hashedPassword = PasswordHash.pwhash(
                64,
                password,
                randomBytes,
                crypto_pwhash_OPSLIMIT_MIN,
                crypto_pwhash_MEMLIMIT_MIN,
                crypto_pwhash_ALG_DEFAULT
            )
            println("Hashed password: ${hashedPassword.toHexString()}")
            assertTrue {
                hashedPassword.toHexString().equals("e762ee529e90e3bbc242c23e8e2f963ab9a17ed9e79f89a00c71261a979207b2213cc" +
                        "0330c53f410a9c8933c46e8642dc542efc0660c69e255b601c7244ef6b0")
            }
        }
    }

    @Test
    fun testPasswordHashForStorage() = runTest {
        LibsodiumInitializer.initializeWithCallback {
            val password = "correct horse battery staple"
            val hashedPassword = PasswordHash.str(
                password,
                crypto_pwhash_OPSLIMIT_MIN,
                crypto_pwhash_MEMLIMIT_MIN
            )
            println("Hashed password for storage: ${hashedPassword.toHexString()}")

            assertTrue {
                PasswordHash.strVerify(
                    hashedPassword,
                    password
                )
            }

            assertTrue {
                PasswordHash.strNeedsRehash(hashedPassword, crypto_pwhash_OPSLIMIT_MIN, crypto_pwhash_MEMLIMIT_MIN) == 0
            }

            assertTrue {
                PasswordHash.strNeedsRehash(hashedPassword, crypto_pwhash_OPSLIMIT_MIN, crypto_pwhash_MEMLIMIT_SENSITIVE) == 1
            }

            //TODO strNeedsRehash -1 case?
        }
    }
}
