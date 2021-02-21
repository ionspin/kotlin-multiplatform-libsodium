package com.ionspin.kotlin.crypto.secretstream

actual fun modifyState(state: SecretStreamState, forceNonce: UByteArray) {
    forceNonce.sliceArray(12 until 24).asByteArray().copyInto(state.nonce)
    println("Nonce modified ${state.nonce}")
}
