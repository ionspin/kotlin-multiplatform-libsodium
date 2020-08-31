package com.ionspin.kotlin.crypto.hash

actual object Hash {
    actual fun hash(data: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun sha256(data: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun sha256Init(): Sha256State {
        TODO("not implemented yet")
    }

    actual fun sha256Update(state: Sha256State, data: UByteArray) {
    }

    actual fun sha256Final(state: Sha256State): UByteArray {
        TODO("not implemented yet")
    }

    actual fun sha512(data: UByteArray): UByteArray {
        TODO("not implemented yet")
    }

    actual fun sha512Init(): Sha512State {
        TODO("not implemented yet")
    }

    actual fun sha512Update(state: Sha512State, data: UByteArray) {
    }

    actual fun sha512Final(state: Sha512State): UByteArray {
        TODO("not implemented yet")
    }


}
