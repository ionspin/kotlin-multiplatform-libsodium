package com.ionspin.kotlin.crypto.secretstream

actual fun modifyState(state: SecretStreamState, forceNonce: UByteArray) {
    state.nonce = forceNonce.sliceArray(12 until 24).asByteArray()
    println("Nonce modified ${state.nonce}")
}
