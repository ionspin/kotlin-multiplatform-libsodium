package com.ionspin.kotlin.crypto.pwhash

/**
 * Created by Ugljesa Jovanovic (jovanovic.ugljesa@gmail.com) on 17/Sep/2020
 */


const val crypto_pwhash_BYTES_MIN = 16U
const val crypto_pwhash_MEMLIMIT_INTERACTIVE = 67108864U
const val crypto_pwhash_MEMLIMIT_MIN = 8192U
const val crypto_pwhash_MEMLIMIT_MODERATE = 268435456U
const val crypto_pwhash_MEMLIMIT_SENSITIVE = 1073741824U
const val crypto_pwhash_OPSLIMIT_INTERACTIVE = 2U
const val crypto_pwhash_OPSLIMIT_MAX = 4294967295U
const val crypto_pwhash_OPSLIMIT_MIN = 1U
const val crypto_pwhash_OPSLIMIT_MODERATE = 3U
const val crypto_pwhash_OPSLIMIT_SENSITIVE = 4U
const val crypto_pwhash_PASSWD_MAX = 4294967295U
const val crypto_pwhash_PASSWD_MIN = 0U
const val crypto_pwhash_SALTBYTES = 16U
const val crypto_pwhash_STRBYTES = 128U
const val crypto_pwhash_STRPREFIX = "\$argon2id$"

val crypto_pwhash_argon2id_ALG_ARGON2ID13 = 2
val crypto_pwhash_argon2i_ALG_ARGON2I13 = 1
val crypto_pwhash_ALG_DEFAULT = crypto_pwhash_argon2id_ALG_ARGON2ID13

class PasswordHashFailed() : RuntimeException("Password hashing failed")

expect object PasswordHash {
    /**
     * The crypto_pwhash() function derives an outlen bytes long key from a password passwd whose length is passwdlen
     * and a salt salt whose fixed length is crypto_pwhash_SALTBYTES bytes. passwdlen should be at least crypto_pwhash_
     * PASSWD_MIN and crypto_pwhash_PASSWD_MAX. outlen should be at least crypto_pwhash_BYTES_MIN = 16 (128 bits) and
     * at most crypto_pwhash_BYTES_MAX.
     *
     * See https://libsodium.gitbook.io/doc/password_hashing/default_phf for more details
     */
    fun pwhash(
        outputLength: Int,
        password: String,
        salt: UByteArray,
        opsLimit: ULong,
        memLimit: Int,
        algorithm: Int
    ): UByteArray

    /**
     * The crypto_pwhash_str() function puts an ASCII encoded string into out, which includes:
     * the result of a memory-hard, CPU-intensive hash function applied to the   password passwd of length passwdlen
     * the automatically generated salt used for the previous computation
     * the other parameters required to verify the password, including the algorithm identifier, its version, opslimit and memlimit.
     * out must be large enough to hold crypto_pwhash_STRBYTES bytes, but the actual output string may be shorter.
     * The output string is zero-terminated, includes only ASCII characters and can be safely stored into SQL databases
     * and other data stores. No extra information has to be stored in order to verify the password.
     * The function returns 0 on success and -1 if it didn't complete successfully.
     */
    fun str(password: String, opslimit: ULong, memlimit: Int): String

    /**
     * Check if a password verification string str matches the parameters opslimit and memlimit, and the current default algorithm.
     * The function returns 1 if the string appears to be correct, but doesn't match the given parameters. In that situation, applications may want to compute a new hash using the current parameters the next time the user logs in.
     * The function returns 0 if the parameters already match the given ones.
     * It returns -1 on error. If it happens, applications may want to compute a correct hash the next time the user logs in.
     */
    fun strNeedsRehash(password: String, opslimit: ULong, memlimit: Int): Boolean
    fun strVerify(passwordHash: String, password: UByteArray): Boolean
}