//package debug.test
//
//import com.ionspin.kotlin.crypto.getSodium
//import ext.libsodium.com.ionspin.kotlin.crypto.toUByteArray
//import ext.libsodium.com.ionspin.kotlin.crypto.toUInt8Array
//import kotlin.Any
//import kotlin.Int
//import kotlin.UByte
//import kotlin.UByteArray
//import org.khronos.webgl.Uint8Array
//
//actual typealias Sha256State = Any
//
//actual typealias Sha512State = Any
//
//actual typealias GenericHashState = Any
//
//actual typealias SecretStreamState = Any
//
//actual class Crypto internal actual constructor() {
//  /**
//   * Initialize the SHA256 hash
//   * returns sha 256 state
//   */
//  actual fun crypto_hash_sha256_init(): dynamic {
//    println("Debug crypto_hash_sha256_init")
//    val result  = js("getSodium().crypto_hash_sha256_init()")
//        return result
//  }
//
//  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
//    println("Debug crypto_hash_sha256_update")
//    getSodium().crypto_hash_sha256_update(state, input.toUInt8Array())
//  }
//
//  actual fun crypto_hash_sha256_final(state: Sha256State): UByteArray {
//    println("Debug crypto_hash_sha256_final")
//    return getSodium().crypto_hash_sha256_final(state).toUByteArray()
//  }
//
//  actual fun crypto_hash_sha512_init(): dynamic {
//    println("Debug crypto_hash_sha512_init")
//    val result  = js("getSodium().crypto_hash_sha512_init()")
//        return result
//  }
//
//  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
//    println("Debug crypto_hash_sha512_update")
//    getSodium().crypto_hash_sha512_update(state, input.toUInt8Array())
//  }
//
//  actual fun crypto_hash_sha512_final(state: Sha512State): UByteArray {
//    println("Debug crypto_hash_sha512_final")
//    return getSodium().crypto_hash_sha512_final(state).toUByteArray()
//  }
//
//  actual fun crypto_generichash_init(key: UByteArray, outlen: Int): dynamic {
//    println("Debug crypto_generichash_init")
//    return getSodium().crypto_generichash_init(key.toUInt8Array(), outlen)
//  }
//
//  /**
//   * Initialize a state and generate a random header. Both are returned inside
//   * `SecretStreamStateAndHeader` object.
//   */
//  actual fun crypto_secretstream_xchacha20poly1305_init_push(key: UByteArray):
//      SecretStreamStateAndHeader {
//    println("Debug crypto_secretstream_xchacha20poly1305_init_push")
//    val stateAndHeader =
//        getSodium().crypto_secretstream_xchacha20poly1305_init_push(key.toUInt8Array())
//        val state = stateAndHeader.state
//        val header = (stateAndHeader.header as Uint8Array).toUByteArray()
//        return SecretStreamStateAndHeader(state, header)
//  }
//
//  /**
//   * Initialize state from header and key. The state can then be used for decryption.
//   */
//  actual fun crypto_secretstream_xchacha20poly1305_init_pull(header: UByteArray, key: UByteArray):
//      dynamic {
//    println("Debug crypto_secretstream_xchacha20poly1305_init_pull")
//    return getSodium().crypto_secretstream_xchacha20poly1305_init_pull(header.toUInt8Array(),
//        key.toUInt8Array())
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
//    println("Debug crypto_secretstream_xchacha20poly1305_push")
//    return getSodium().crypto_secretstream_xchacha20poly1305_push(state, m.toUInt8Array(),
//        ad.toUInt8Array(), tag).toUByteArray()
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
//    println("Debug crypto_secretstream_xchacha20poly1305_pull")
////    return getSodium().crypto_secretstream_xchacha20poly1305_pull(state, c.toUInt8Array(),
////        ad.toUInt8Array())
//    return DecryptedDataAndTag(ubyteArrayOf(), 0U)
//  }
//}
