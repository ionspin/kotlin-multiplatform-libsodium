package com.ionspin.kotlin.crypto

import com.sun.jna.Library
import com.sun.jna.NativeLong
import com.sun.jna.Structure

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Feb-2021
 */
class Hash256State : Structure() {
    override fun getFieldOrder(): List<String> = listOf("state", "count", "buf")
    @JvmField
    val state = IntArray(8)
    @JvmField
    var count: Long = 0
    @JvmField
    val buf = ByteArray(64)
}

class Hash512State : Structure() {
    override fun getFieldOrder(): List<String> = listOf("state", "count", "buf")
    @JvmField
    val state = IntArray(8)
    @JvmField
    var count: LongArray = LongArray(2)
    @JvmField
    val buf = ByteArray(128)
}

class Blake2bState: Structure() {
    override fun getFieldOrder(): List<String> = listOf("opaque")
    @JvmField
    val opaque = ByteArray(384)
}

interface JnaLibsodiumInterface : Library {
    fun sodium_version_string(): String

    fun randombytes_buf(buffer: ByteArray, bufferSize: Int)

    //    int crypto_generichash(unsigned char *out, size_t outlen,
    //    const unsigned char *in, unsigned long long inlen,
    //    const unsigned char *key, size_t keylen)
    fun crypto_generichash(
        out: ByteArray,
        outlen: Int,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray,
        keylen: Int
    )

    //    int crypto_hash_sha256(unsigned char *out, const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha256(out: ByteArray, input: ByteArray, inputLength: Long)

    //    int crypto_hash_sha512(unsigned char *out, const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha512(out: ByteArray, input: ByteArray, inputLength: Long)
//
//    // ---- Generic hash ---- // Updateable
//

    //    int crypto_generichash_init(crypto_generichash_state *state,
    //    const unsigned char *key,
    //    const size_t keylen, const size_t outlen)
    //    Output cant be larger than 64 so no need to use Long to represent size_t here
    fun crypto_generichash_init(state: Blake2bState, key: ByteArray, keylen: Int, outlen: Int)

    //    int crypto_generichash_update(crypto_generichash_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_generichash_update(state: Blake2bState, inputMessage: ByteArray, inputLength: Long)

    //    int crypto_generichash_final(crypto_generichash_state *state,
    //    unsigned char *out, const size_t outlen)
    //    Output cant be larger than 64 so no need to use Long to represent size_t here
    fun crypto_generichash_final(state: Blake2bState, out: ByteArray, hashLength: Int)

    //    void crypto_generichash_keygen(unsigned char k[crypto_generichash_KEYBYTES])
    fun crypto_generichash_keygen(key: ByteArray)

    fun crypto_generichash_statebytes(): Int

    //
//    // ---- Generic hash end ---- // Updateable
//
//    // ---- Blake2b ----
    //    int crypto_generichash_blake2b(unsigned char *out, size_t outlen,
    //    const unsigned char *in,
    //    unsigned long long inlen,
    //    const unsigned char *key, size_t keylen)
    //    Output cant be larger than 64 so no need to use Long to represent size_t here
    fun crypto_generichash_blake2b(
        out: ByteArray,
        outlen: Int,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray,
        keylen: Int
    )

    //    int crypto_generichash_blake2b_init(crypto_generichash_blake2b_state *state,
    //    const unsigned char *key,
    //    const size_t keylen, const size_t outlen)
    fun crypto_generichash_blake2b_init(kstate: ByteArray, key: ByteArray, keylen: Int, outlen: Int)

    //    int crypto_generichash_blake2b_update(crypto_generichash_blake2b_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_generichash_blake2b_update(state: ByteArray, inputMessage: ByteArray, inputLength: Long)

    //    int crypto_generichash_blake2b_final(crypto_generichash_blake2b_state *state,
    //    unsigned char *out,
    //    const size_t outlen)
    fun crypto_generichash_blake2b_final(state: ByteArray, out: ByteArray, hashLength: Int)

    //    void crypto_generichash_blake2b_keygen(unsigned char k[crypto_generichash_blake2b_KEYBYTES])
    fun crypto_generichash_blake2b_keygen(key: ByteArray)
//
//    // ---- Blake2b end ----
//
//    // ---- Short hash ----
//

    //    int crypto_shorthash(unsigned char *out, const unsigned char *in,
    //    unsigned long long inlen, const unsigned char *k)
    fun crypto_shorthash(out: ByteArray, input: ByteArray, inlen: Long, key: ByteArray)

    //    void crypto_shorthash_keygen(unsigned char k[crypto_shorthash_KEYBYTES])
    fun crypto_shorthash_keygen(key: ByteArray)

//
// ---- Short hash end ----
//

    //    int crypto_hash_sha256_init(crypto_hash_sha256_state *state)
    fun crypto_hash_sha256_init(state: Hash256State)

    //    int crypto_hash_sha256_update(crypto_hash_sha256_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha256_update(state: Hash256State, input: ByteArray, inlen: Long)

    //    int crypto_hash_sha256_final(crypto_hash_sha256_state *state,
    //    unsigned char *out)
    fun crypto_hash_sha256_final(state: Hash256State, out: ByteArray)


    //    int crypto_hash_sha512_init(crypto_hash_sha512_state *state)
    fun crypto_hash_sha512_init(state: Hash512State)

    //    int crypto_hash_sha512_update(crypto_hash_sha512_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha512_update(state: Hash512State, input: ByteArray, inlen: Long)

    //    int crypto_hash_sha512_final(crypto_hash_sha512_state *state,
    //    unsigned char *out)
    fun crypto_hash_sha512_final(state: Hash512State, out: ByteArray)
//
//    //XChaCha20Poly1305 - also in bindings
//    //fun crypto_aead_xchacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, secretNonce: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
//    //fun crypto_aead_xchacha20poly1305_ietf_decrypt(secretNonce: Uint8Array, ciphertext: Uint8Array, associatedData: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
//
//    //XChaCha20Poly1305
//    //encrypt
//    fun crypto_secretstream_xchacha20poly1305_init_push(key: Uint8Array) : dynamic
//    fun crypto_secretstream_xchacha20poly1305_push(state: dynamic, message: Uint8Array, associatedData: Uint8Array, tag: UByte) : Uint8Array
//
//    //decrypt
//    fun crypto_secretstream_xchacha20poly1305_init_pull(header: Uint8Array, key: Uint8Array) : dynamic
//    fun crypto_secretstream_xchacha20poly1305_pull(state: dynamic, ciphertext: Uint8Array, associatedData: Uint8Array) : dynamic
//
//    //keygen and rekey
//    fun crypto_secretstream_xchacha20poly1305_keygen() : Uint8Array
//    fun crypto_secretstream_xchacha20poly1305_rekey(state: dynamic)
//
//    // ---- SecretBox ----
//    fun crypto_secretbox_detached(message: Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic
//    fun crypto_secretbox_easy(message: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_secretbox_keygen() : Uint8Array
//    fun crypto_secretbox_open_detached(ciphertext : Uint8Array, tag : Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic
//    fun crypto_secretbox_open_easy(ciphertext : Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic
//
//
//    // ---- SecretBox End ----
//
//
//    // ---- AEAD ----
//    fun crypto_aead_chacha20poly1305_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_aead_chacha20poly1305_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
//    fun crypto_aead_chacha20poly1305_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_aead_chacha20poly1305_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
//    fun crypto_aead_chacha20poly1305_ietf_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_aead_chacha20poly1305_ietf_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
//    fun crypto_aead_chacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_aead_chacha20poly1305_ietf_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
//    fun crypto_aead_chacha20poly1305_ietf_keygen() : Uint8Array
//    fun crypto_aead_chacha20poly1305_keygen() : Uint8Array
//    fun crypto_aead_xchacha20poly1305_ietf_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_aead_xchacha20poly1305_ietf_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
//    fun crypto_aead_xchacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_aead_xchacha20poly1305_ietf_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
//    fun crypto_aead_xchacha20poly1305_ietf_keygen(): Uint8Array
//
//    // ---- AEAD end ----
//
//    // ---- Auth ----
//
//    fun crypto_auth(message: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_auth_keygen() : Uint8Array
//    fun crypto_auth_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean
//    fun crypto_auth_hmacsha256(message: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_auth_hmacsha256_keygen() : Uint8Array
//    fun crypto_auth_hmacsha256_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean
//    fun crypto_auth_hmacsha512(message: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_auth_hmacsha512_keygen() : Uint8Array
//    fun crypto_auth_hmacsha512_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean
//
//    // ---- Auth end ----
//
//    // ---- Box ----
//
//    fun crypto_box_keypair() : dynamic
//    fun crypto_box_seed_keypair(seed : Uint8Array) : dynamic
//    fun crypto_box_easy(message: Uint8Array,
//                        nonce: Uint8Array,
//                        recipientsPublicKey: Uint8Array,
//                        sendersSecretKey: Uint8Array) : Uint8Array
//    fun crypto_box_open_easy(ciphertext: Uint8Array,
//                             nonce: Uint8Array,
//                             sendersPublicKey: Uint8Array,
//                             recipientsSecretKey: Uint8Array) : Uint8Array
//    fun crypto_box_detached(message: Uint8Array,
//                            nonce: Uint8Array,
//                            recipientsPublicKey: Uint8Array,
//                            sendersSecretKey: Uint8Array) : dynamic
//    fun crypto_box_open_detached(ciphertext: Uint8Array,
//                                 tag: Uint8Array,
//                                 nonce: Uint8Array,
//                                 sendersPublicKey: Uint8Array,
//                                 recipientsSecretKey: Uint8Array) : Uint8Array
//    fun crypto_box_beforenm(publicKey: Uint8Array, secretKey: Uint8Array) : Uint8Array
//    fun crypto_box_easy_afternm(message: Uint8Array,
//                                nonce: Uint8Array,
//                                precomputedKey: Uint8Array) : Uint8Array
//    fun crypto_box_open_easy_afternm(ciphertext: Uint8Array,
//                                     nonce: Uint8Array,
//                                     precomputedKey: Uint8Array) : Uint8Array
//    fun crypto_box_seal(message: Uint8Array, recipientsPublicKey: Uint8Array) : Uint8Array
//    fun crypto_box_seal_open(ciphertext: Uint8Array, recipientsPublicKey: Uint8Array, recipientsSecretKey: Uint8Array) : Uint8Array
//
//    // ---- Box end ----
//
//    // ---- Sign start ----
//    fun crypto_sign(message: Uint8Array, secretKey: Uint8Array) : Uint8Array
//    fun crypto_sign_detached(message: Uint8Array, secretKey: Uint8Array) : Uint8Array
//    fun crypto_sign_ed25519_pk_to_curve25519(ed25519PublicKey: Uint8Array) : Uint8Array
//    fun crypto_sign_ed25519_sk_to_curve25519(ed25519SecretKey: Uint8Array) : Uint8Array
//    fun crypto_sign_ed25519_sk_to_pk(ed25519SecretKey: Uint8Array) : Uint8Array
//    fun crypto_sign_ed25519_sk_to_seed(ed25519SecretKey: Uint8Array) : Uint8Array
//    fun crypto_sign_final_create(state: dynamic, secretKey: Uint8Array) : Uint8Array
//    fun crypto_sign_final_verify(state: dynamic, signature: Uint8Array, publicKey: Uint8Array) : Boolean
//    fun crypto_sign_init() : dynamic
//    fun crypto_sign_keypair() : dynamic
//    fun crypto_sign_open(signedMessage: Uint8Array, publicKey: Uint8Array) : Uint8Array
//    fun crypto_sign_seed_keypair(seed: Uint8Array) : dynamic
//    fun crypto_sign_update(state: dynamic, message: Uint8Array)
//    fun crypto_sign_verify_detached(signature: Uint8Array, message: Uint8Array, publicKey: Uint8Array) : Boolean
//
//
//    // ---- Sign end ----
//
//
//    // ---- KDF ----
//
//    fun crypto_kdf_derive_from_key(subkey_len: UInt, subkeyId : UInt, ctx: String, key: Uint8Array) : Uint8Array
//    fun crypto_kdf_keygen() : Uint8Array
//
//    // ---- KDF end -----
//
//    // ---- Password hashing ----
//
//    fun crypto_pwhash(keyLength : UInt, password : Uint8Array, salt: Uint8Array, opsLimit: UInt, memLimit: UInt, algorithm: UInt) : Uint8Array
//    fun crypto_pwhash_str(password: Uint8Array, opsLimit: UInt, memLimit: UInt) : String
//    fun crypto_pwhash_str_needs_rehash(hashedPassword: String, opsLimit: UInt, memLimit: UInt) : Boolean
//    fun crypto_pwhash_str_verify(hashedPassword: String, password: Uint8Array) : Boolean
//
//
//    // ---- Password hashing end ----
//
//    // ---- Utils ----
//
//    fun memcmp(first: Uint8Array, second: Uint8Array) : Boolean
//    fun memzero(data: Uint8Array)
//    fun pad(data : Uint8Array, blocksize: Int) : Uint8Array
//    fun unpad(data: Uint8Array, blocksize: Int) : Uint8Array
//    fun to_base64(data: Uint8Array, variant: Int) : String
//    fun to_hex(data: Uint8Array) : String
//    fun to_string(data: Uint8Array) : String
//    fun from_base64(data: String, variant: Int): Uint8Array
//    fun from_hex(data : String): Uint8Array
//    fun from_string(data : String): Uint8Array
//
//    //  ---- > ---- Random ---- < -----
//
//    fun randombytes_buf(length: UInt) : Uint8Array
//    fun randombytes_buf_deterministic(length: UInt, seed : Uint8Array) : Uint8Array
//    fun randombytes_random() : UInt
//    fun randombytes_uniform(upper_bound: UInt) : UInt
//
//    // ---- Utils end ----
//
//    // ---- Key exchange ----
//    fun crypto_kx_client_session_keys(clientPublicKey: Uint8Array, clientSecretKey: Uint8Array, serverPublicKey: Uint8Array) : dynamic
//    fun crypto_kx_keypair() : dynamic
//    fun crypto_kx_seed_keypair(seed: Uint8Array) : dynamic
//    fun crypto_kx_server_session_keys(serverPublicKey: Uint8Array, serverSecretKey: Uint8Array, clientPublicKey: Uint8Array) : dynamic
//
//    // ---- Key exchange end ----
//
//    // -- Stream ----
//    fun crypto_stream_chacha20(outLength: UInt, key: Uint8Array, nonce: Uint8Array) : Uint8Array
//    fun crypto_stream_chacha20_ietf_xor(message : Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_stream_chacha20_ietf_xor_ic(message : Uint8Array, nonce: Uint8Array, initialCounter: UInt, key: Uint8Array) : Uint8Array
//    fun crypto_stream_chacha20_keygen() : Uint8Array
//    fun crypto_stream_chacha20_xor(message : Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
//    fun crypto_stream_chacha20_xor_ic(message : Uint8Array, nonce: Uint8Array, initialCounter: UInt, key: Uint8Array) : Uint8Array
//
//    // ---- Stream end ----
//
//    // ---- Scalar multiplication ----
//
//    fun crypto_scalarmult(privateKey: Uint8Array, publicKey: Uint8Array) : Uint8Array
//    fun crypto_scalarmult_base(privateKey: Uint8Array) : Uint8Array
//
//    // ---- Scalar multiplication end ----
}
