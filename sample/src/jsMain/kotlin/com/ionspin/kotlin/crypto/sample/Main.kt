

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString


fun main() {
     LibsodiumInitializer.initializeWithCallback {
            val hash = Hash.sha512("123".encodeToUByteArray())
            println("Hash (SHA512) of 123: ${hash.toHexString()}")
     }
}
