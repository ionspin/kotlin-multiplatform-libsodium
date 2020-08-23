package com.ionspin.kotlin.crypto.generichash

import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_generichash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual object GenericHashing {
    val _emitByte: Byte = 0
    val _emitByteArray: ByteArray = ByteArray(0)

    actual fun genericHash(message: UByteArray, requestedHashLength: Int, key: UByteArray?) : UByteArray {
        val hash = UByteArray(requestedHashLength)
        val pinnedHash = hash.pin()
        val pinnedKey = key?.pin()
        val pinnedMessage = message.pin()
        crypto_generichash(
            pinnedHash.addressOf(0),
            requestedHashLength.convert(),
            pinnedMessage.addressOf(0),
            message.size.convert(),
            pinnedKey?.addressOf(0),
            (key?.size ?: 0).convert()
        )
        pinnedHash.unpin()
        pinnedKey?.unpin()
        pinnedMessage.unpin()
        return hash
    }
}
