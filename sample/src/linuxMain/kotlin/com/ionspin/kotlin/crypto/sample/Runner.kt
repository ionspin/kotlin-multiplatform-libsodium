import com.ionspin.kotlin.crypto.keyderivation.argon2.Argon2
import com.ionspin.kotlin.crypto.keyderivation.argon2.ArgonType
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

@ExperimentalTime
@ExperimentalStdlibApi
fun main() {
    println("Test")
    val argon2Instance = Argon2(
        password = "Password",
        salt = "RandomSalt",
        parallelism = 1,
        tagLength = 64U,
        requestedMemorySize = 4096U,
        numberOfIterations = 100,
        key = "",
        associatedData = "",
        argonType = ArgonType.Argon2id
    )
    val time = measureTime {
        val tag = argon2Instance.derive()
        val tagString = tag.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "")
        val expectedTagString = "27c61b6538ef9f4a1250f8712cac09fc4329969295f9440249437d38c1617a005c2702d76a8a59e4cda2dfba48e1132261dacdfd31296945906992ea32f1d06e"
        println("Tag: ${tagString}")
    }
    println("Time $time")
}
