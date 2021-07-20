package ext.libsodium.com.ionspin.kotlin.crypto


import com.ionspin.kotlin.crypto.box.BoxKeyPair
import org.khronos.webgl.Uint8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
interface JsSodiumInterface {

    @JsName("sodium_init")
    fun sodium_init() : Int

    @JsName("crypto_generichash")
    fun crypto_generichash(hashLength: Int, inputMessage: Uint8Array, key: Uint8Array): Uint8Array

    @JsName("crypto_hash_sha256")
    fun crypto_hash_sha256(message: Uint8Array): Uint8Array

    @JsName("crypto_hash_sha512")
    fun crypto_hash_sha512(message: Uint8Array): Uint8Array

    // ---- Generic hash ---- // Updateable

    @JsName("crypto_generichash_init")
    fun crypto_generichash_init(key : Uint8Array, hashLength: Int) : dynamic

    @JsName("crypto_generichash_update")
    fun crypto_generichash_update(state: dynamic, inputMessage: Uint8Array)

    @JsName("crypto_generichash_final")
    fun crypto_generichash_final(state: dynamic, hashLength: Int) : Uint8Array

    @JsName("crypto_generichash_keygen")
    fun crypto_generichash_keygen() : Uint8Array

    // ---- Generic hash end ---- // Updateable

    // ---- Blake2b ----

    @JsName("crypto_generichash_blake2b")
    fun crypto_generichash_blake2b(hashLength: Int, inputMessage: Uint8Array, key: Uint8Array): Uint8Array

    @JsName("crypto_generichash_blake2b_init")
    fun crypto_generichash_blake2b_init(key : Uint8Array, hashLength: Int) : dynamic

    @JsName("crypto_generichash_blake2b_update")
    fun crypto_generichash_blake2b_update(state: dynamic, inputMessage: Uint8Array)

    @JsName("crypto_generichash_blake2b_final")
    fun crypto_generichash_blake2b_final(state: dynamic, hashLength: Int) : Uint8Array

    @JsName("crypto_generichash_blake2b_keygen")
    fun crypto_generichash_blake2b_keygen() : Uint8Array

    // ---- Blake2b end ----

    // ---- Short hash ----
    @JsName("crypto_shorthash")
    fun crypto_shorthash(data : Uint8Array, key: Uint8Array) : Uint8Array

    @JsName("crypto_shorthash_keygen")
    fun crypto_shorthash_keygen() : Uint8Array
    // ---- Short hash end ----


    @JsName("crypto_hash_sha256_init")
    fun crypto_hash_sha256_init() : dynamic

    @JsName("crypto_hash_sha256_update")
    fun crypto_hash_sha256_update(state: dynamic, message: Uint8Array)

    @JsName("crypto_hash_sha256_final")
    fun crypto_hash_sha256_final(state: dynamic): Uint8Array

    @JsName("crypto_hash_sha512_init")
    fun crypto_hash_sha512_init() : dynamic

    @JsName("crypto_hash_sha512_update")
    fun crypto_hash_sha512_update(state: dynamic, message: Uint8Array)

    @JsName("crypto_hash_sha512_final")
    fun crypto_hash_sha512_final(state: dynamic): Uint8Array

    //XChaCha20Poly1305 - also in bindings
    //fun crypto_aead_xchacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, secretNonce: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    //fun crypto_aead_xchacha20poly1305_ietf_decrypt(secretNonce: Uint8Array, ciphertext: Uint8Array, associatedData: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array

    //XChaCha20Poly1305
    //encrypt
    @JsName("crypto_secretstream_xchacha20poly1305_init_push")
    fun crypto_secretstream_xchacha20poly1305_init_push(key: Uint8Array) : dynamic
    @JsName("crypto_secretstream_xchacha20poly1305_push")
    fun crypto_secretstream_xchacha20poly1305_push(state: dynamic, message: Uint8Array, associatedData: Uint8Array, tag: UByte) : Uint8Array

    //decrypt
    @JsName("crypto_secretstream_xchacha20poly1305_init_pull")
    fun crypto_secretstream_xchacha20poly1305_init_pull(header: Uint8Array, key: Uint8Array) : dynamic
    @JsName("crypto_secretstream_xchacha20poly1305_pull")
    fun crypto_secretstream_xchacha20poly1305_pull(state: dynamic, ciphertext: Uint8Array, associatedData: Uint8Array) : dynamic

    //keygen and rekey
    @JsName("crypto_secretstream_xchacha20poly1305_keygen")
    fun crypto_secretstream_xchacha20poly1305_keygen() : Uint8Array
    @JsName("crypto_secretstream_xchacha20poly1305_rekey")
    fun crypto_secretstream_xchacha20poly1305_rekey(state: dynamic)

    // ---- SecretBox ----
    @JsName("crypto_secretbox_detached")
    fun crypto_secretbox_detached(message: Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic
    @JsName("crypto_secretbox_easy")
    fun crypto_secretbox_easy(message: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_secretbox_keygen")
    fun crypto_secretbox_keygen() : Uint8Array
    @JsName("crypto_secretbox_open_detached")
    fun crypto_secretbox_open_detached(ciphertext : Uint8Array, tag : Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic
    @JsName("crypto_secretbox_open_easy")
    fun crypto_secretbox_open_easy(ciphertext : Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic


    // ---- SecretBox End ----


    // ---- AEAD ----
    @JsName("crypto_aead_chacha20poly1305_decrypt")
    fun crypto_aead_chacha20poly1305_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_aead_chacha20poly1305_decrypt_detached")
    fun crypto_aead_chacha20poly1305_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
    @JsName("crypto_aead_chacha20poly1305_encrypt")
    fun crypto_aead_chacha20poly1305_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_aead_chacha20poly1305_encrypt_detached")
    fun crypto_aead_chacha20poly1305_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
    @JsName("crypto_aead_chacha20poly1305_ietf_decrypt")
    fun crypto_aead_chacha20poly1305_ietf_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_aead_chacha20poly1305_ietf_decrypt_detached")
    fun crypto_aead_chacha20poly1305_ietf_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
    @JsName("crypto_aead_chacha20poly1305_ietf_encrypt")
    fun crypto_aead_chacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_aead_chacha20poly1305_ietf_encrypt_detached")
    fun crypto_aead_chacha20poly1305_ietf_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
    @JsName("crypto_aead_chacha20poly1305_ietf_keygen")
    fun crypto_aead_chacha20poly1305_ietf_keygen() : Uint8Array
    @JsName("crypto_aead_chacha20poly1305_keygen")
    fun crypto_aead_chacha20poly1305_keygen() : Uint8Array
    @JsName("crypto_aead_xchacha20poly1305_ietf_decrypt")
    fun crypto_aead_xchacha20poly1305_ietf_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_aead_xchacha20poly1305_ietf_decrypt_detached")
    fun crypto_aead_xchacha20poly1305_ietf_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
    @JsName("crypto_aead_xchacha20poly1305_ietf_encrypt")
    fun crypto_aead_xchacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_aead_xchacha20poly1305_ietf_encrypt_detached")
    fun crypto_aead_xchacha20poly1305_ietf_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
    @JsName("crypto_aead_xchacha20poly1305_ietf_keygen")
    fun crypto_aead_xchacha20poly1305_ietf_keygen(): Uint8Array

    // ---- AEAD end ----

    // ---- Auth ----

    @JsName("crypto_auth")
    fun crypto_auth(message: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_auth_keygen")
    fun crypto_auth_keygen() : Uint8Array
    @JsName("crypto_auth_verify")
    fun crypto_auth_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean
    @JsName("crypto_auth_hmacsha256")
    fun crypto_auth_hmacsha256(message: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_auth_hmacsha256_keygen")
    fun crypto_auth_hmacsha256_keygen() : Uint8Array
    @JsName("crypto_auth_hmacsha256_verify")
    fun crypto_auth_hmacsha256_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean
    @JsName("crypto_auth_hmacsha512")
    fun crypto_auth_hmacsha512(message: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_auth_hmacsha512_keygen")
    fun crypto_auth_hmacsha512_keygen() : Uint8Array
    @JsName("crypto_auth_hmacsha512_verify")
    fun crypto_auth_hmacsha512_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean

    // ---- Auth end ----

    // ---- Box ----

    @JsName("crypto_box_keypair")
    fun crypto_box_keypair() : dynamic
    @JsName("crypto_box_seed_keypair")
    fun crypto_box_seed_keypair(seed : Uint8Array) : dynamic
    @JsName("crypto_box_easy")
    fun crypto_box_easy(message: Uint8Array,
                        nonce: Uint8Array,
                        recipientsPublicKey: Uint8Array,
                        sendersSecretKey: Uint8Array) : Uint8Array
    @JsName("crypto_box_open_easy")
    fun crypto_box_open_easy(ciphertext: Uint8Array,
                             nonce: Uint8Array,
                             sendersPublicKey: Uint8Array,
                             recipientsSecretKey: Uint8Array) : Uint8Array
    @JsName("crypto_box_detached")
    fun crypto_box_detached(message: Uint8Array,
                            nonce: Uint8Array,
                            recipientsPublicKey: Uint8Array,
                            sendersSecretKey: Uint8Array) : dynamic
    @JsName("crypto_box_open_detached")
    fun crypto_box_open_detached(ciphertext: Uint8Array,
                                 tag: Uint8Array,
                                 nonce: Uint8Array,
                                 sendersPublicKey: Uint8Array,
                                 recipientsSecretKey: Uint8Array) : Uint8Array
    @JsName("crypto_box_beforenm")
    fun crypto_box_beforenm(publicKey: Uint8Array, secretKey: Uint8Array) : Uint8Array
    @JsName("crypto_box_easy_afternm")
    fun crypto_box_easy_afternm(message: Uint8Array,
                                nonce: Uint8Array,
                                precomputedKey: Uint8Array) : Uint8Array
    @JsName("crypto_box_open_easy_afternm")
    fun crypto_box_open_easy_afternm(ciphertext: Uint8Array,
                                     nonce: Uint8Array,
                                     precomputedKey: Uint8Array) : Uint8Array
    @JsName("crypto_box_seal")
    fun crypto_box_seal(message: Uint8Array, recipientsPublicKey: Uint8Array) : Uint8Array
    @JsName("crypto_box_seal_open")
    fun crypto_box_seal_open(ciphertext: Uint8Array, recipientsPublicKey: Uint8Array, recipientsSecretKey: Uint8Array) : Uint8Array

    // ---- Box end ----

    // ---- Sign start ----
    @JsName("crypto_sign")
    fun crypto_sign(message: Uint8Array, secretKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_detached")
    fun crypto_sign_detached(message: Uint8Array, secretKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_ed25519_pk_to_curve25519")
    fun crypto_sign_ed25519_pk_to_curve25519(ed25519PublicKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_ed25519_sk_to_curve25519")
    fun crypto_sign_ed25519_sk_to_curve25519(ed25519SecretKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_ed25519_sk_to_pk")
    fun crypto_sign_ed25519_sk_to_pk(ed25519SecretKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_ed25519_sk_to_seed")
    fun crypto_sign_ed25519_sk_to_seed(ed25519SecretKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_final_create")
    fun crypto_sign_final_create(state: dynamic, secretKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_final_verify")
    fun crypto_sign_final_verify(state: dynamic, signature: Uint8Array, publicKey: Uint8Array) : Boolean
    @JsName("crypto_sign_init")
    fun crypto_sign_init() : dynamic
    @JsName("crypto_sign_keypair")
    fun crypto_sign_keypair() : dynamic
    @JsName("crypto_sign_open")
    fun crypto_sign_open(signedMessage: Uint8Array, publicKey: Uint8Array) : Uint8Array
    @JsName("crypto_sign_seed_keypair")
    fun crypto_sign_seed_keypair(seed: Uint8Array) : dynamic
    @JsName("crypto_sign_update")
    fun crypto_sign_update(state: dynamic, message: Uint8Array)
    @JsName("crypto_sign_verify_detached")
    fun crypto_sign_verify_detached(signature: Uint8Array, message: Uint8Array, publicKey: Uint8Array) : Boolean


    // ---- Sign end ----


    // ---- KDF ----

    @JsName("crypto_kdf_derive_from_key")
    fun crypto_kdf_derive_from_key(subkey_len: UInt, subkeyId : UInt, ctx: String, key: Uint8Array) : Uint8Array
    @JsName("crypto_kdf_keygen")
    fun crypto_kdf_keygen() : Uint8Array

    // ---- KDF end -----

    // ---- Password hashing ----

    @JsName("crypto_pwhash")
    fun crypto_pwhash(keyLength : UInt, password : Uint8Array, salt: Uint8Array, opsLimit: UInt, memLimit: UInt, algorithm: UInt) : Uint8Array
    @JsName("crypto_pwhash_str")
    fun crypto_pwhash_str(password: Uint8Array, opsLimit: UInt, memLimit: UInt) : String
    @JsName("crypto_pwhash_str_needs_rehash")
    fun crypto_pwhash_str_needs_rehash(hashedPassword: String, opsLimit: UInt, memLimit: UInt) : Boolean
    @JsName("crypto_pwhash_str_verify")
    fun crypto_pwhash_str_verify(hashedPassword: String, password: Uint8Array) : Boolean


    // ---- Password hashing end ----

    // ---- Utils ----

    @JsName("memcmp")
    fun memcmp(first: Uint8Array, second: Uint8Array) : Boolean
    @JsName("memzero")
    fun memzero(data: Uint8Array)
    @JsName("pad")
    fun pad(data : Uint8Array, blocksize: Int) : Uint8Array
    @JsName("unpad")
    fun unpad(data: Uint8Array, blocksize: Int) : Uint8Array
    @JsName("to_base64")
    fun to_base64(data: Uint8Array, variant: Int) : String
    @JsName("to_hex")
    fun to_hex(data: Uint8Array) : String
    @JsName("to_string")
    fun to_string(data: Uint8Array) : String
    @JsName("from_base64")
    fun from_base64(data: String, variant: Int): Uint8Array
    @JsName("from_hex")
    fun from_hex(data : String): Uint8Array
    @JsName("from_string")
    fun from_string(data : String): Uint8Array

    //  ---- > ---- Random ---- < -----

    @JsName("randombytes_buf")
    fun randombytes_buf(length: Int) : Uint8Array
    @JsName("randombytes_buf_deterministic")
    fun randombytes_buf_deterministic(length: UInt, seed : Uint8Array) : Uint8Array
    @JsName("randombytes_random")
    fun randombytes_random() : UInt
    @JsName("randombytes_uniform")
    fun randombytes_uniform(upper_bound: UInt) : UInt

    // ---- Utils end ----

    // ---- Key exchange ----
    @JsName("crypto_kx_client_session_keys")
    fun crypto_kx_client_session_keys(clientPublicKey: Uint8Array, clientSecretKey: Uint8Array, serverPublicKey: Uint8Array) : dynamic
    @JsName("crypto_kx_keypair")
    fun crypto_kx_keypair() : dynamic
    @JsName("crypto_kx_seed_keypair")
    fun crypto_kx_seed_keypair(seed: Uint8Array) : dynamic
    @JsName("crypto_kx_server_session_keys")
    fun crypto_kx_server_session_keys(serverPublicKey: Uint8Array, serverSecretKey: Uint8Array, clientPublicKey: Uint8Array) : dynamic

    // ---- Key exchange end ----

    // -- Stream ----
    @JsName("crypto_stream_chacha20")
    fun crypto_stream_chacha20(outLength: UInt, key: Uint8Array, nonce: Uint8Array) : Uint8Array
    @JsName("crypto_stream_chacha20_ietf_xor")
    fun crypto_stream_chacha20_ietf_xor(message : Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_stream_chacha20_ietf_xor_ic")
    fun crypto_stream_chacha20_ietf_xor_ic(message : Uint8Array, nonce: Uint8Array, initialCounter: UInt, key: Uint8Array) : Uint8Array
    @JsName("crypto_stream_chacha20_keygen")
    fun crypto_stream_chacha20_keygen() : Uint8Array
    @JsName("crypto_stream_chacha20_xor")
    fun crypto_stream_chacha20_xor(message : Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_stream_chacha20_xor_ic")
    fun crypto_stream_chacha20_xor_ic(message : Uint8Array, nonce: Uint8Array, initialCounter: UInt, key: Uint8Array) : Uint8Array

    @JsName("crypto_stream_xchacha20_keygen")
    fun crypto_stream_xchacha20_keygen() : Uint8Array
    @JsName("crypto_stream_xchacha20_xor")
    fun crypto_stream_xchacha20_xor(message : Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    @JsName("crypto_stream_xchacha20_xor_ic")
    fun crypto_stream_xchacha20_xor_ic(message : Uint8Array, nonce: Uint8Array, initialCounter: UInt, key: Uint8Array) : Uint8Array

    // ---- Stream end ----

    // ---- Scalar multiplication ----

    @JsName("crypto_scalarmult")
    fun crypto_scalarmult(privateKey: Uint8Array, publicKey: Uint8Array) : Uint8Array
    @JsName("crypto_scalarmult_base")
    fun crypto_scalarmult_base(privateKey: Uint8Array) : Uint8Array

    // ---- Scalar multiplication end ----



}
