package com.ionspin.kotlin.crypto.generichash

import kotlin.Byte
import kotlin.ByteArray
import kotlin.Int
import kotlin.UByte
import kotlin.UByteArray
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import libsodium.crypto_generichash
import libsodium.crypto_generichash_blake2b_state
import libsodium.crypto_hash_sha256_state
import libsodium.crypto_hash_sha512_state
import libsodium.crypto_secretstream_xchacha20poly1305_state
import libsodium.sodium_malloc
/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
actual object GenericHashing {
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
