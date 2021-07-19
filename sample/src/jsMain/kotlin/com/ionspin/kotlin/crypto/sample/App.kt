import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.ReactElement
import react.dom.h1

external interface RAppState : RState {
    var currentState: String
}

external interface RAppProps : RProps {

}

class App(props: RAppProps) : RComponent<RAppProps, RAppState>(props) {
    override fun RBuilder.render() {
        val hash = Hash.sha512("123".encodeToUByteArray())
        h1 {
            +"Hash (SHA512) of 123: ${hash.toHexString()}"

        }
    }

}

fun RBuilder.app(handler: RAppProps.() -> Unit): ReactElement {
    return child(App::class) {
        this.attrs(handler)
    }
}
