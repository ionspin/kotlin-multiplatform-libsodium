package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.hash.StatelessHash
import com.ionspin.kotlin.crypto.hash.UpdatableHash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */
object Sha512Properties {
    const val MAX_HASH_BYTES = 64
}
interface Sha512 : UpdatableHash {
    override val MAX_HASH_BYTES: Int
        get() = Sha256Properties.MAX_HASH_BYTES
}
interface StatelessSha512 : StatelessHash {
    override val MAX_HASH_BYTES: Int
        get() = Sha256Properties.MAX_HASH_BYTES
}