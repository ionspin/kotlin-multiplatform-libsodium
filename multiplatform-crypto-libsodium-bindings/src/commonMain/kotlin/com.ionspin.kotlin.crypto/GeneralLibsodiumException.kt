package com.ionspin.kotlin.crypto

class GeneralLibsodiumException : RuntimeException("Libsodium reported error! Returned value was -1") {
    companion object {
        fun Int.ensureLibsodiumSuccess() {
            if (this == -1) {
                throw GeneralLibsodiumException()
            }
        }
    }
}