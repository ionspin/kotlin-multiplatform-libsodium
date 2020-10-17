

import com.ionspin.kotlin.crypto.LibsodiumInitializer
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.LibsodiumRandom
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import react.dom.render
import kotlinx.browser.document
import kotlin.browser.window

fun main() {
    val runningOnNode = jsTypeOf(window) == "undefined"
    if (!runningOnNode) {
        LibsodiumInitializer.initializeWithCallback {
            render(document.getElementById("root")) {
                app {

                }
            }
        }
    } else {
        LibsodiumInitializer.initializeWithCallback {
            val hash = Hash.sha512("123".encodeToUByteArray())
            println("Hash (SHA512) of 123: ${hash.toHexString()}")
        }
    }
}
