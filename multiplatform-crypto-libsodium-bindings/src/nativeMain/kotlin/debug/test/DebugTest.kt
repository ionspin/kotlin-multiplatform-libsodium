//package debug.test
//
//import kotlin.Byte
//import kotlin.ByteArray
//import kotlin.Int
//import kotlin.UByte
//import kotlin.UByteArray
//import kotlinx.cinterop.addressOf
//import kotlinx.cinterop.convert
//import kotlinx.cinterop.pin
//import kotlinx.cinterop.pointed
//import kotlinx.cinterop.ptr
//import kotlinx.cinterop.reinterpret
//import kotlinx.cinterop.toCValues
//import libsodium.crypto_generichash_blake2b_state
//import libsodium.crypto_hash_sha256_state
//import libsodium.crypto_hash_sha512_state
//import libsodium.crypto_secretstream_xchacha20poly1305_state
//import libsodium.sodium_malloc
//
//actual typealias Sha256State = crypto_hash_sha256_state
//
//actual typealias Sha512State = crypto_hash_sha512_state
//
//actual typealias GenericHashState = crypto_generichash_blake2b_state
//
//actual typealias SecretStreamState = crypto_secretstream_xchacha20poly1305_state
//
//actual class Crypto internal actual constructor() {
//  val _emitByte: Byte = 0
//
//  val _emitByteArray: ByteArray = ByteArray(0)
//
//  /**
//   * Initialize the SHA256 hash
//   * returns sha 256 state
//   */
//  actual fun crypto_hash_sha256_init(): Sha256State {
//    val allocated = sodium_malloc(debug.test.Sha256State.size.convert())!!
//    val state = allocated.reinterpret<debug.test.Sha256State>().pointed
//    println("Debug crypto_hash_sha256_init")
//    libsodium.crypto_hash_sha256_init(state.ptr)
//    return state
//  }
//
//  actual fun crypto_hash_sha256_update(state: Sha256State, input: UByteArray) {
//    println("Debug crypto_hash_sha256_update")
//    val pinnedInput = input.pin()
//    libsodium.crypto_hash_sha256_update(state.ptr, pinnedInput.addressOf(0), input.size.convert())
//    pinnedInput.unpin()
//  }
//
//  actual fun crypto_hash_sha256_final(state: Sha256State): UByteArray {
//    val out = UByteArray(32)
//    println("Debug crypto_hash_sha256_final")
//    val pinnedOut = out.pin()
//    libsodium.crypto_hash_sha256_final(state.ptr, pinnedOut.addressOf(0))
//    pinnedOut.unpin()
//    return out
//  }
//
//  actual fun crypto_hash_sha512_init(): Sha512State {
//    val allocated = sodium_malloc(debug.test.Sha512State.size.convert())!!
//    val state = allocated.reinterpret<debug.test.Sha512State>().pointed
//    println("Debug crypto_hash_sha512_init")
//    libsodium.crypto_hash_sha512_init(state.ptr)
//    return state
//  }
//
//  actual fun crypto_hash_sha512_update(state: Sha512State, input: UByteArray) {
//    println("Debug crypto_hash_sha512_update")
//    val pinnedInput = input.pin()
//    libsodium.crypto_hash_sha512_update(state.ptr, pinnedInput.addressOf(0), input.size.convert())
//    pinnedInput.unpin()
//  }
//
//  actual fun crypto_hash_sha512_final(state: Sha512State): UByteArray {
//    val out = UByteArray(64)
//    println("Debug crypto_hash_sha512_final")
//    val pinnedOut = out.pin()
//    libsodium.crypto_hash_sha512_final(state.ptr, pinnedOut.addressOf(0))
//    pinnedOut.unpin()
//    return out
//  }
//
//  actual fun crypto_generichash_init(key: UByteArray, outlen: Int): GenericHashState {
//    val allocated = sodium_malloc(debug.test.GenericHashState.size.convert())!!
//    val state = allocated.reinterpret<debug.test.GenericHashState>().pointed
//    println("Debug crypto_generichash_init")
//    val pinnedKey = key.pin()
//    libsodium.crypto_generichash_init(state.ptr, pinnedKey.addressOf(0), key.size.convert(),
//        outlen.convert())
//    pinnedKey.unpin()
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
//    val pinnedKey = key.pin()
//        val state =
//            sodium_malloc(libsodium.crypto_secretstream_xchacha20poly1305_state.size.convert())!!
//            .reinterpret<libsodium.crypto_secretstream_xchacha20poly1305_state>()
//            .pointed
//        val header = UByteArray(libsodium.crypto_secretstream_xchacha20poly1305_HEADERBYTES.toInt())
//            { 0U }
//        val pinnedHeader = header.pin()
//        libsodium.crypto_secretstream_xchacha20poly1305_init_push(state.ptr,
//            pinnedHeader.addressOf(0), pinnedKey.addressOf(0))
//        pinnedHeader.unpin()
//        pinnedKey.unpin()
//        return SecretStreamStateAndHeader(state, header)
//  }
//
//  /**
//   * Initialize state from header and key. The state can then be used for decryption.
//   */
//  actual fun crypto_secretstream_xchacha20poly1305_init_pull(header: UByteArray, key: UByteArray):
//      SecretStreamState {
//    val allocated = sodium_malloc(debug.test.SecretStreamState.size.convert())!!
//    val state = allocated.reinterpret<debug.test.SecretStreamState>().pointed
//    println("Debug crypto_secretstream_xchacha20poly1305_init_pull")
//    val pinnedHeader = header.pin()
//    val pinnedKey = key.pin()
//    libsodium.crypto_secretstream_xchacha20poly1305_init_pull(state.ptr, pinnedHeader.addressOf(0),
//        pinnedKey.addressOf(0))
//    pinnedHeader.unpin()
//    pinnedKey.unpin()
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
//    val pinnedC = c.pin()
//    val pinnedM = m.pin()
//    val pinnedAd = ad.pin()
//    libsodium.crypto_secretstream_xchacha20poly1305_push(state.ptr, pinnedC.addressOf(0), null,
//        pinnedM.addressOf(0), m.size.convert(), pinnedAd.addressOf(0), ad.size.convert(), tag)
//    pinnedC.unpin()
//    pinnedM.unpin()
//    pinnedAd.unpin()
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
//    val pinnedM = m.pin()
//    val pinnedC = c.pin()
//    val pinnedAd = ad.pin()
//    libsodium.crypto_secretstream_xchacha20poly1305_pull(state.ptr, pinnedM.addressOf(0), null,
//        ubyteArrayOf().toCValues(), pinnedC.addressOf(0), c.size.convert(), pinnedAd.addressOf(0), ad.size.convert())
//    pinnedM.unpin()
//    pinnedC.unpin()
//    pinnedAd.unpin()
//    return debug.test.DecryptedDataAndTag(m, tag_p)
//  }
//}
