import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bDelegated
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bStateless
import com.ionspin.kotlin.crypto.sample.Sample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import platform.posix.pthread_self
import platform.posix.sleep
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime

fun main() {

    Sample.runSample()

}
