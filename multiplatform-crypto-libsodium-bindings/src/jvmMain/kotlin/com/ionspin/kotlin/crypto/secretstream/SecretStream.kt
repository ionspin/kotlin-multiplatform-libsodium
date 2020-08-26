package com.ionspin.kotlin.crypto.secretstream

import com.goterl.lazycode.lazysodium.interfaces.SecretStream
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodium

actual typealias SecretStreamState = SecretStream.State

actual object SecretStream {
    actual fun xChaCha20Poly1305InitPush(key: UByteArray): SecretStreamStateAndHeader {
        TODO("not implemented yet")
//        sodium.crypto_secretstream_xchacha20poly1305_init_push()
    }

    actual fun xChaCha20Poly1305Push(
        state: SecretStreamState,
        message: UByteArray,
        additionalData: UByteArray,
        tag: UByte
    ): UByteArray {
        TODO("not implemented yet")
    }

    actual fun xChaCha20Poly1305InitPull(
        key: UByteArray,
        header: UByteArray
    ): SecretStreamStateAndHeader {
        TODO("not implemented yet")
    }

    actual fun xChaCha20Poly1305Pull(
        state: SecretStreamState,
        ciphertext: UByteArray,
        additionalData: UByteArray
    ): DecryptedDataAndTag {
        TODO("not implemented yet")
    }

}
