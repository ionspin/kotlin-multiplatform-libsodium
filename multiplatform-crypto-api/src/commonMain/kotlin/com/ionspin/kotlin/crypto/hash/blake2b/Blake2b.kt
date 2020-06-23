package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.hash.MultiPartHash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */

object Blake2bProperties {
    const val MAX_HASH_BYTES = 64
}

interface Blake2b : MultiPartHash {
    override val MAX_HASH_BYTES: Int
        get() = Blake2bProperties.MAX_HASH_BYTES
}

interface Blake2bStateless : Hash {
    override val MAX_HASH_BYTES: Int
        get() = Blake2bProperties.MAX_HASH_BYTES

    fun digest(
        inputMessage: UByteArray = ubyteArrayOf(),
        key: UByteArray = ubyteArrayOf(),
        hashLength: Int = MAX_HASH_BYTES
    ): UByteArray
}

