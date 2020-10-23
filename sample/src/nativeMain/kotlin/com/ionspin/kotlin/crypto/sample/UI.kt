import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import libui.ktx.TextArea
import libui.ktx.appWindow
import libui.ktx.button
import libui.ktx.textarea
import libui.ktx.vbox

fun ui() = appWindow(
    title = "Hello",
    width = 320,
    height = 240
) {
    val hash = Hash.sha512("123".encodeToUByteArray())
    val text = "Hash (SHA512) of 123: ${hash.toHexString()}"
    vbox {
        lateinit var scroll: TextArea

        button("Test") {
            action {
                scroll.append(text.trimMargin())
            }
        }
        scroll = textarea {
            readonly = true
            stretchy = true
        }
    }
}
