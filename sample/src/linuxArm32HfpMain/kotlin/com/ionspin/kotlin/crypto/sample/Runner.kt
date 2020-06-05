import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime

fun main() {
    println("Test")
//    Blake
    val blake = Blake2bDelegated()
    blake.digest()
}
