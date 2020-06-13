package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.hash.StatelessHash
import com.ionspin.kotlin.crypto.hash.UpdatableHash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */

object Blake2bProperties {
    const val MAX_HASH_BYTES = 64
}

interface Blake2b : UpdatableHash {
    override val MAX_HASH_BYTES: Int
        get() = Blake2bProperties.MAX_HASH_BYTES
}

interface Blake2bStateless : StatelessHash {
    override val MAX_HASH_BYTES: Int
        get() = Blake2bProperties.MAX_HASH_BYTES

    fun digest(
        inputMessage: UByteArray = ubyteArrayOf(),
        key: UByteArray = ubyteArrayOf(),
        hashLength: Int = MAX_HASH_BYTES
    ): UByteArray
}

