package com.ionspin.kotlin.crypto.pwhash

import com.ionspin.kotlin.crypto.util.toHexString
import kotlin.random.Random
import kotlin.random.nextUBytes
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Sep-2020
 */
class PasswordHashTest {
    @Test
    fun testPasswordHash() {
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
    }

    @Test
    fun testPasswordHashForStorage() {
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
