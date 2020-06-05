import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bStateless
import com.ionspin.kotlin.crypto.sample.Sample
import kotlinx.coroutines.runBlocking
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime

fun main() = runBlocking {
    Sample.runSample()
//    println("Test")
////    Blake
//    val blake = Blake2bDelegated()
//    val res = blake.digest()
//    println("Result of res")
////    println(res)
//    val staticRes = Blake2bStateless.digest("test")
//    println("Result:")
//    println(staticRes)
}
