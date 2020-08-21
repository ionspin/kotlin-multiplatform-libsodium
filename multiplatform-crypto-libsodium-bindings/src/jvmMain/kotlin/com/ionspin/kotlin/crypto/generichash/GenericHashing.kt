package com.ionspin.kotlin.crypto.generichash

import com.ionspin.kotlin.crypto.Initializer.sodium

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual object GenericHashing {
    actual fun genericHash(
        message: UByteArray,
        requestedHashLength: Int,
        key: UByteArray?
    ): UByteArray {
        val hash = UByteArray(requestedHashLength)
        sodium.crypto_generichash(
            hash.asByteArray(),
            requestedHashLength,
            message.asByteArray(),
            message.size.toLong(),
            key?.asByteArray(),
            (key?.size ?: 0)
        )
        return hash
    }
}
