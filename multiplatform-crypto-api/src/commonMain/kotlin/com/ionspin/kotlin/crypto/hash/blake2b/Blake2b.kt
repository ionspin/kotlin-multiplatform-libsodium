package com.ionspin.kotlin.crypto.hash.blake2b

import com.ionspin.kotlin.crypto.hash.StatelessHash
import com.ionspin.kotlin.crypto.hash.UpdatableHash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */
@ExperimentalUnsignedTypes
interface Blake2b : UpdatableHash
@ExperimentalUnsignedTypes
interface Blake2bStatelessInterface : StatelessHash {
    @ExperimentalUnsignedTypes
    override val MAX_HASH_BYTES: Int
        get() = 64
}

