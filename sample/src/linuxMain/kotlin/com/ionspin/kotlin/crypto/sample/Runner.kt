import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bStateless
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime

fun main() {
    println("Test")
//    Blake
    val blake = Blake2bDelegated()
    blake.digest()
    println("Result: ${Blake2bStateless.digest("test")}")

}
