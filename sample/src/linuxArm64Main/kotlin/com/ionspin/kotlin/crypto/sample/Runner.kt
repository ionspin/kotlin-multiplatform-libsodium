import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bStateless
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
@ExperimentalStdlibApi
fun main() {
    println("Test")
//    Blake
    val blake = Blake2bDelegated()
    val res = blake.digest()
    println("Result of res")
//    println(res)
    val staticRes = Blake2bStateless.digest("test")
    println("Result:")
    println(staticRes)
}
