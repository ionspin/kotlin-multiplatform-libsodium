import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
@ExperimentalStdlibApi
fun main() {
    println("Test")
//    Blake
    val blake = Blake2bDelegated()
    println("Result ${blake.digest()}")
}
