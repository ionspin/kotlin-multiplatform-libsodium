package ext.libsodium.com.ionspin.kotlin.crypto


import org.khronos.webgl.Uint8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
interface JsSodiumInterface {

    fun randombytes_buf(numberOfBytes: Int): Uint8Array

    fun crypto_generichash(hashLength: Int, inputMessage: Uint8Array, key: Uint8Array): Uint8Array

    fun crypto_hash_sha256(message: Uint8Array): Uint8Array

    fun crypto_hash_sha512(message: Uint8Array): Uint8Array

    //Updateable

    fun crypto_generichash_init(key : Uint8Array, hashLength: Int) : dynamic

    fun crypto_generichash_update(state: dynamic, inputMessage: Uint8Array)

    fun crypto_generichash_final(state: dynamic, hashLength: Int) : Uint8Array

    //Short hash
    fun crypto_shorthash(data : Uint8Array, key: Uint8Array) : Uint8Array

    fun crypto_shorthash_keygen() : Uint8Array


    fun crypto_hash_sha256_init() : dynamic

    fun crypto_hash_sha256_update(state: dynamic, message: Uint8Array)

    fun crypto_hash_sha256_final(state: dynamic): Uint8Array

    fun crypto_hash_sha512_init() : dynamic

    fun crypto_hash_sha512_update(state: dynamic, message: Uint8Array)

    fun crypto_hash_sha512_final(state: dynamic): Uint8Array

    //XChaCha20Poly1305
    fun crypto_aead_xchacha20poly1305_ietf_encrypt(message: Uint8Array, additionalData: Uint8Array, secretNonce: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_aead_xchacha20poly1305_ietf_decrypt(secretNonce: Uint8Array, ciphertext: Uint8Array, additionalData: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array

    //XChaCha20Poly1305
    //encrypt
    fun crypto_secretstream_xchacha20poly1305_init_push(key: Uint8Array) : dynamic
    fun crypto_secretstream_xchacha20poly1305_push(state: dynamic, message: Uint8Array, additionalData: Uint8Array, tag: UByte) : Uint8Array

    //decrypt
    fun crypto_secretstream_xchacha20poly1305_init_pull(header: Uint8Array, key: Uint8Array) : dynamic
    fun crypto_secretstream_xchacha20poly1305_pull(state: dynamic, ciphertext: Uint8Array, additionalData: Uint8Array) : dynamic

    //keygen and rekey
    fun crypto_secretstream_xchacha20poly1305_keygen() : Uint8Array
    fun crypto_secretstream_xchacha20poly1305_rekey(state: dynamic)

    //util
    fun memzero(array: Uint8Array)




}
