package com.ionspin.kotlin.crypto.stream

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.convert
import kotlinx.cinterop.pin
import libsodium.crypto_stream_chacha20
import libsodium.crypto_stream_chacha20_ietf_xor
import libsodium.crypto_stream_chacha20_ietf_xor_ic
import libsodium.crypto_stream_chacha20_keygen
import libsodium.crypto_stream_chacha20_xor
import libsodium.crypto_stream_chacha20_xor_ic

//import libsodium.crypto_stream_xchacha20_keygen
//import libsodium.crypto_stream_xchacha20_xor
//import libsodium.crypto_stream_xchacha20_xor_ic

actual object Stream {
    actual fun chacha20(clen: Int, nonce: UByteArray, key: UByteArray): UByteArray {
        val result = UByteArray(clen)
        val resultPinned = result.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        crypto_stream_chacha20(resultPinned.toPtr(), clen.convert(), noncePinned.toPtr(), keyPinned.toPtr())

        resultPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return result
    }

    actual fun chacha20IetfXor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)
        val messagePinned = message.pin()
        val resultPinned = result.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        crypto_stream_chacha20_ietf_xor(
            resultPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()
        resultPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return result
    }

    actual fun chacha20IetfXorIc(
        message: UByteArray,
        nonce: UByteArray,
        initialCounter: ULong,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)
        val messagePinned = message.pin()
        val resultPinned = result.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        crypto_stream_chacha20_ietf_xor_ic(
            resultPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            initialCounter.convert(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()
        resultPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return result
    }

    actual fun chacha20Keygen(): UByteArray {
        val result = UByteArray(crypto_stream_chacha20_KEYBYTES)
        val resultPinned = result.pin()

        crypto_stream_chacha20_keygen(resultPinned.toPtr())

        resultPinned.unpin()

        return result
    }

    actual fun chacha20Xor(
        message: UByteArray,
        nonce: UByteArray,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)
        val messagePinned = message.pin()
        val resultPinned = result.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        crypto_stream_chacha20_xor(
            resultPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()
        resultPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return result
    }

    actual fun chacha20XorIc(
        message: UByteArray,
        nonce: UByteArray,
        initialCounter: ULong,
        key: UByteArray
    ): UByteArray {
        val result = UByteArray(message.size)
        val messagePinned = message.pin()
        val resultPinned = result.pin()
        val noncePinned = nonce.pin()
        val keyPinned = key.pin()

        crypto_stream_chacha20_xor_ic(
            resultPinned.toPtr(),
            messagePinned.toPtr(),
            message.size.convert(),
            noncePinned.toPtr(),
            initialCounter.convert(),
            keyPinned.toPtr()
        )

        messagePinned.unpin()
        resultPinned.unpin()
        noncePinned.unpin()
        keyPinned.unpin()

        return result
    }

//    actual fun xChacha20Keygen(): UByteArray {
//        val result = UByteArray(crypto_stream_xchacha20_KEYBYTES)
//        val resultPinned = result.pin()
//
//        crypto_stream_xchacha20_keygen(resultPinned.toPtr())
//
//        resultPinned.unpin()
//
//        return result
//    }
//
//    actual fun xChacha20Xor(
//        message: UByteArray,
//        nonce: UByteArray,
//        key: UByteArray
//    ): UByteArray {
//        val result = UByteArray(message.size)
//        val messagePinned = message.pin()
//        val resultPinned = result.pin()
//        val noncePinned = nonce.pin()
//        val keyPinned = key.pin()
//
//        crypto_stream_xchacha20_xor(
//            resultPinned.toPtr(),
//            messagePinned.toPtr(),
//            message.size.convert(),
//            noncePinned.toPtr(),
//            keyPinned.toPtr()
//        )
//
//        messagePinned.unpin()
//        resultPinned.unpin()
//        noncePinned.unpin()
//        keyPinned.unpin()
//
//        return result
//    }
//
//    actual fun xChacha20XorIc(
//        message: UByteArray,
//        nonce: UByteArray,
//        initialCounter: ULong,
//        key: UByteArray
//    ): UByteArray {
//        val result = UByteArray(message.size)
//        val messagePinned = message.pin()
//        val resultPinned = result.pin()
//        val noncePinned = nonce.pin()
//        val keyPinned = key.pin()
//
//        crypto_stream_xchacha20_xor_ic(
//            resultPinned.toPtr(),
//            messagePinned.toPtr(),
//            message.size.convert(),
//            noncePinned.toPtr(),
//            initialCounter.convert(),
//            keyPinned.toPtr()
//        )
//
//        messagePinned.unpin()
//        resultPinned.unpin()
//        noncePinned.unpin()
//        keyPinned.unpin()
//
//        return result
//    }
}
