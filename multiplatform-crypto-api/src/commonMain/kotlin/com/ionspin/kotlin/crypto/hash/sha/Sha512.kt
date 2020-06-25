package com.ionspin.kotlin.crypto.hash.sha

import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.hash.MultipartHash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 24-May-2020
 */
object Sha512Properties {
    const val MAX_HASH_BYTES = 64
}
interface Sha512Multipart : MultipartHash {
    override val MAX_HASH_BYTES: Int
        get() = Sha256Properties.MAX_HASH_BYTES
}
interface Sha512 : Hash {
    override val MAX_HASH_BYTES: Int
        get() = Sha512Properties.MAX_HASH_BYTES

    fun digest(
        inputMessage: UByteArray = ubyteArrayOf()
    ): UByteArray
}
