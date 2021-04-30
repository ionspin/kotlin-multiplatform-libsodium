package com.ionspin.kotlin.crypto.kdf

import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna

actual object Kdf {
    /**
     * The deriveFromKey function derives a subkeyId-th subkey of length subkeyLenght bytes using
     * the master key key and the context ctx.
     * subkey_id can be any value up to (2^32) because javascript doesn't support long types
     * subkey_len has to be between crypto_kdf_BYTES_MIN (inclusive) and crypto_kdf_BYTES_MAX (inclusive).
     * Similar to a type, the context ctx is a 8 characters string describing what the key is going to be used for.
     * Its purpose is to mitigate accidental bugs by separating domains. The same function used with the same key but
     * in two distinct contexts is likely to generate two different outputs.
     * Contexts don't have to be secret and can have a low entropy.
     * Examples of contexts include UserName, __auth__, pictures and userdata.
     * They must be crypto_kdf_CONTEXTBYTES bytes long.
     * If more convenient, it is also fine to use a single global context for a whole application. This will still
     * prevent the same keys from being mistakenly used by another application.
     */
    actual fun deriveFromKey(
        subkeyId: UInt,
        subkeyLength: Int,
        context: String,
        masterKey: UByteArray
    ): UByteArray {
        val subkey = UByteArray(subkeyLength)
        val contextEncoded = context.encodeToByteArray()

        sodiumJna.crypto_kdf_derive_from_key(
            subkey.asByteArray(),
            subkeyLength,
            subkeyId.toLong(),
            contextEncoded,
            masterKey.asByteArray()
        )

        return subkey
    }

    /**
     * The crypto_kdf_keygen() function creates a master key.
     */
    actual fun keygen(): UByteArray {
        val masterKey = UByteArray(crypto_kdf_KEYBYTES)
        sodiumJna.crypto_kdf_keygen(masterKey.asByteArray())
        return masterKey
    }

}
