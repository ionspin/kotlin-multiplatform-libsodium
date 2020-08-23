//package debug.test
//
//import com.goterl.lazycode.lazysodium.SodiumJava
//import com.goterl.lazycode.lazysodium.interfaces.Hash
//import com.goterl.lazycode.lazysodium.interfaces.SecretStream
//import kotlin.ByteArray
//import kotlin.Int
//import kotlin.UByte
//import kotlin.UByteArray
//
//val sodium: SodiumJava = SodiumJava()
//
//actual typealias Sha256State = Hash.State256
//
//actual typealias Sha512State = Hash.State512
//
//actual typealias GenericHashState = ByteArray
//
//actual typealias SecretStreamState = SecretStream.State
//
//actual class Crypto internal actual constructor() {
//  /**
//   * Initialize the SHA256 hash
//   * returns sha 256 state
//   */
//  actual fun crypto_hash_sha256_init(): Sha256State {
//    val state = debug.test.Sha256State()
//    println("Debug crypto_hash_sha256_init")
//    sodium.crypto_hash_sha256_init(state)
//    return state
//  }
//
//  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
//    println("Debug crypto_hash_sha256_update")
//    sodium.crypto_hash_sha256_update(state, input.asByteArray(), input.size.toLong())
//  }
//
//  actual fun crypto_hash_sha256_final(state: Sha256State): UByteArray {
//    val out = UByteArray(32)
//    println("Debug crypto_hash_sha256_final")
//    sodium.crypto_hash_sha256_final(state, out.asByteArray())
//    return out
//  }
//
//  actual fun crypto_hash_sha512_init(): Sha512State {
//    val state = debug.test.Sha512State()
//    println("Debug crypto_hash_sha512_init")
//    sodium.crypto_hash_sha512_init(state)
//    return state
//  }
//
//  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
//    println("Debug crypto_hash_sha512_update")
//    sodium.crypto_hash_sha512_update(state, input.asByteArray(), input.size.toLong())
//  }
//
//  actual fun crypto_hash_sha512_final(state: Sha512State): UByteArray {
//    val out = UByteArray(64)
//    println("Debug crypto_hash_sha512_final")
//    sodium.crypto_hash_sha512_final(state, out.asByteArray())
//    return out
//  }
//
//  actual fun crypto_generichash_init(key: UByteArray, outlen: Int): GenericHashState {
//    val state = debug.test.GenericHashState(sodium.crypto_generichash_statebytes())
//    println("Debug crypto_generichash_init")
//    sodium.crypto_generichash_init(state, key.asByteArray(), key.size, outlen)
//    return state
//  }
//
//  /**
//   * Initialize a state and generate a random header. Both are returned inside
//   * `SecretStreamStateAndHeader` object.
//   */
//  actual fun crypto_secretstream_xchacha20poly1305_init_push(key: UByteArray):
//      SecretStreamStateAndHeader {
//    println("Debug crypto_secretstream_xchacha20poly1305_init_push")
//    val header = UByteArray(24)
//        val state = SecretStream.State()
//        sodium.crypto_secretstream_xchacha20poly1305_init_push(state, header.asByteArray(),
//            key.asByteArray())
//        return SecretStreamStateAndHeader(state, header)
//  }
//
//  /**
//   * Initialize state from header and key. The state can then be used for decryption.
//   */
//  actual fun crypto_secretstream_xchacha20poly1305_init_pull(header: UByteArray, key: UByteArray):
//      SecretStreamState {
//    val state = debug.test.SecretStreamState()
//    println("Debug crypto_secretstream_xchacha20poly1305_init_pull")
//    sodium.crypto_secretstream_xchacha20poly1305_init_pull(state, header.asByteArray(),
//        key.asByteArray())
//    return state
//  }
//
//  /**
//   * Encrypt next block of data using the previously initialized state. Returns encrypted block.
//   */
//  actual fun crypto_secretstream_xchacha20poly1305_push(
//    state: SecretStreamState,
//    m: UByteArray,
//    ad: UByteArray,
//    tag: UByte
//  ): UByteArray {
//    val c = UByteArray(m.size + 17)
//    println("Debug crypto_secretstream_xchacha20poly1305_push")
//    sodium.crypto_secretstream_xchacha20poly1305_push(state, c.asByteArray(), null, m.asByteArray(),
//        m.size.toLong(), ad.asByteArray(), ad.size.toLong(), tag.toByte())
//    return c
//  }
//
//  /**
//   * Decrypt next block of data using the previously initialized state. Returns decrypted block.
//   */
//  actual fun crypto_secretstream_xchacha20poly1305_pull(
//    state: SecretStreamState,
//    c: UByteArray,
//    ad: UByteArray
//  ): DecryptedDataAndTag {
//    val m = UByteArray(c.size - 17)
//    var tag_p : UByte = 0U
//    println("Debug crypto_secretstream_xchacha20poly1305_pull")
//    sodium.crypto_secretstream_xchacha20poly1305_pull(state, m.asByteArray(), null, byteArrayOf(),
//        c.asByteArray(), c.size.toLong(), ad.asByteArray(), ad.size.toLong())
//    return debug.test.DecryptedDataAndTag(m, tag_p)
//  }
//}
