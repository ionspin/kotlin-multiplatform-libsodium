//package debug.test
//
//import kotlin.Int
//import kotlin.UByte
//import kotlin.UByteArray
//import kotlin.js.JsName
//
//expect class Sha256State
//
//expect class Sha512State
//
//expect class GenericHashState
//
//expect class SecretStreamState
//
//data class SecretStreamStateAndHeader(
//  @JsName("state")
//  val state: SecretStreamState,
//  @JsName("header")
//  val header: UByteArray
//)
//
//data class DecryptedDataAndTag(
//  @JsName("decrypted")
//  val decrypted: UByteArray,
//  @JsName("tag")
//  val tag: UByte
//)
//
//expect class Crypto internal constructor() {
//  /**
//   * Initialize the SHA256 hash
//   * returns sha 256 state
//   */
//  fun crypto_hash_sha256_init(): Sha256State
//
//  fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray)
//
//  fun crypto_hash_sha256_final(state: Sha256State): UByteArray
//
//  fun crypto_hash_sha512_init(): Sha512State
//
//  fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray)
//
//  fun crypto_hash_sha512_final(state: Sha512State): UByteArray
//
//  fun crypto_generichash_init(key: UByteArray, outlen: Int): GenericHashState
//
//  /**
//   * Initialize a state and generate a random header. Both are returned inside
//   * `SecretStreamStateAndHeader` object.
//   */
//  fun crypto_secretstream_xchacha20poly1305_init_push(key: UByteArray): SecretStreamStateAndHeader
//
//  /**
//   * Initialize state from header and key. The state can then be used for decryption.
//   */
//  fun crypto_secretstream_xchacha20poly1305_init_pull(header: UByteArray, key: UByteArray):
//      SecretStreamState
//
//  /**
//   * Encrypt next block of data using the previously initialized state. Returns encrypted block.
//   */
//  fun crypto_secretstream_xchacha20poly1305_push(
//    state: SecretStreamState,
//    m: UByteArray,
//    ad: UByteArray,
//    tag: UByte
//  ): UByteArray
//
//  /**
//   * Decrypt next block of data using the previously initialized state. Returns decrypted block.
//   */
//  fun crypto_secretstream_xchacha20poly1305_pull(
//    state: SecretStreamState,
//    c: UByteArray,
//    ad: UByteArray
//  ): DecryptedDataAndTag
//}
