package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.util.isLibsodiumSuccessCode

class GeneralLibsodiumException : RuntimeException("Libsodium reported error! Returned value was -1") {
    companion object {
        /**
         * Throws a [GeneralLibsodiumException] if the return code is not
         * successful.
         *
         * This will throw an [IllegalStateException] if the return code is invalid/unknown.
         */
        fun Int.ensureLibsodiumSuccess() {
            if (!isLibsodiumSuccessCode()) {
                throw GeneralLibsodiumException()
            }
        }
    }
}
