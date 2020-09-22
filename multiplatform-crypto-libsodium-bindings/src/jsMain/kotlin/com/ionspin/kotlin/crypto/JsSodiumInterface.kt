package ext.libsodium.com.ionspin.kotlin.crypto


import com.ionspin.kotlin.crypto.box.BoxKeyPair
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

    // ---- Generic hash ---- // Updateable

    fun crypto_generichash_init(key : Uint8Array, hashLength: Int) : dynamic

    fun crypto_generichash_update(state: dynamic, inputMessage: Uint8Array)

    fun crypto_generichash_final(state: dynamic, hashLength: Int) : Uint8Array

    fun crypto_generichash_keygen() : Uint8Array

    // ---- Generic hash end ---- // Updateable

    // ---- Blake2b ----

    fun crypto_generichash_blake2b(hashLength: Int, inputMessage: Uint8Array, key: Uint8Array): Uint8Array

    fun crypto_generichash_blake2b_init(key : Uint8Array, hashLength: Int) : dynamic

    fun crypto_generichash_blake2b_update(state: dynamic, inputMessage: Uint8Array)

    fun crypto_generichash_blake2b_final(state: dynamic, hashLength: Int) : Uint8Array

    fun crypto_generichash_blake2b_keygen() : Uint8Array

    // ---- Blake2b end ----

    // ---- Short hash ----
    fun crypto_shorthash(data : Uint8Array, key: Uint8Array) : Uint8Array

    fun crypto_shorthash_keygen() : Uint8Array
    // ---- Short hash end ----

    fun crypto_hash_sha256_init() : dynamic

    fun crypto_hash_sha256_update(state: dynamic, message: Uint8Array)

    fun crypto_hash_sha256_final(state: dynamic): Uint8Array

    fun crypto_hash_sha512_init() : dynamic

    fun crypto_hash_sha512_update(state: dynamic, message: Uint8Array)

    fun crypto_hash_sha512_final(state: dynamic): Uint8Array

    //XChaCha20Poly1305 - also in bindings
    //fun crypto_aead_xchacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, secretNonce: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    //fun crypto_aead_xchacha20poly1305_ietf_decrypt(secretNonce: Uint8Array, ciphertext: Uint8Array, associatedData: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array

    //XChaCha20Poly1305
    //encrypt
    fun crypto_secretstream_xchacha20poly1305_init_push(key: Uint8Array) : dynamic
    fun crypto_secretstream_xchacha20poly1305_push(state: dynamic, message: Uint8Array, associatedData: Uint8Array, tag: UByte) : Uint8Array

    //decrypt
    fun crypto_secretstream_xchacha20poly1305_init_pull(header: Uint8Array, key: Uint8Array) : dynamic
    fun crypto_secretstream_xchacha20poly1305_pull(state: dynamic, ciphertext: Uint8Array, associatedData: Uint8Array) : dynamic

    //keygen and rekey
    fun crypto_secretstream_xchacha20poly1305_keygen() : Uint8Array
    fun crypto_secretstream_xchacha20poly1305_rekey(state: dynamic)

    // ---- SecretBox ----
    fun crypto_secretbox_detached(message: Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic
    fun crypto_secretbox_easy(message: Uint8Array, nonce: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_secretbox_keygen() : Uint8Array
    fun crypto_secretbox_open_detached(ciphertext : Uint8Array, tag : Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic
    fun crypto_secretbox_open_easy(ciphertext : Uint8Array, nonce: Uint8Array, key: Uint8Array) : dynamic


    // ---- SecretBox End ----


    // ---- AEAD ----
    fun crypto_aead_chacha20poly1305_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_aead_chacha20poly1305_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
    fun crypto_aead_chacha20poly1305_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_aead_chacha20poly1305_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
    fun crypto_aead_chacha20poly1305_ietf_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_aead_chacha20poly1305_ietf_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
    fun crypto_aead_chacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_aead_chacha20poly1305_ietf_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
    fun crypto_aead_chacha20poly1305_ietf_keygen() : Uint8Array
    fun crypto_aead_chacha20poly1305_keygen() : Uint8Array
    fun crypto_aead_xchacha20poly1305_ietf_decrypt(nsec : Uint8Array?, ciphertext: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_aead_xchacha20poly1305_ietf_decrypt_detached(nsec: Uint8Array?, ciphertext: Uint8Array, mac: Uint8Array, associatedData: Uint8Array, npub: Uint8Array, key: Uint8Array): Uint8Array
    fun crypto_aead_xchacha20poly1305_ietf_encrypt(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_aead_xchacha20poly1305_ietf_encrypt_detached(message: Uint8Array, associatedData: Uint8Array, nsec: Uint8Array?, npub: Uint8Array, key: Uint8Array) : dynamic
    fun crypto_aead_xchacha20poly1305_ietf_keygen(): Uint8Array

    // ---- AEAD end ----

    // ---- Auth ----

    fun crypto_auth(message: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_auth_keygen() : Uint8Array
    fun crypto_auth_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean
    fun crypto_auth_hmacsha256(message: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_auth_hmacsha256_keygen() : Uint8Array
    fun crypto_auth_hmacsha256_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean
    fun crypto_auth_hmacsha512(message: Uint8Array, key: Uint8Array) : Uint8Array
    fun crypto_auth_hmacsha512_keygen() : Uint8Array
    fun crypto_auth_hmacsha512_verify(tag: Uint8Array, message: Uint8Array, key: Uint8Array) : Boolean

    // ---- Auth end ----

    // ---- Box ----

    fun crypto_box_keypair() : dynamic
    fun crypto_box_seed_keypair(seed : Uint8Array) : dynamic
    fun crypto_box_easy(message: Uint8Array,
                        nonce: Uint8Array,
                        recipientsPublicKey: Uint8Array,
                        sendersSecretKey: Uint8Array) : Uint8Array
    fun crypto_box_open_easy(ciphertext: Uint8Array,
                             nonce: Uint8Array,
                             sendersPublicKey: Uint8Array,
                             recipientsSecretKey: Uint8Array) : Uint8Array
    fun crypto_box_detached(message: Uint8Array,
                            nonce: Uint8Array,
                            recipientsPublicKey: Uint8Array,
                            sendersSecretKey: Uint8Array) : dynamic
    fun crypto_box_open_detached(ciphertext: Uint8Array,
                                 tag: Uint8Array,
                                 nonce: Uint8Array,
                                 sendersPublicKey: Uint8Array,
                                 recipientsSecretKey: Uint8Array) : Uint8Array
    fun crypto_box_beforenm(publicKey: Uint8Array, secretKey: Uint8Array) : Uint8Array
    fun crypto_box_easy_afternm(message: Uint8Array,
                                nonce: Uint8Array,
                                precomputedKey: Uint8Array) : Uint8Array
    fun crypto_box_open_easy_afternm(ciphertext: Uint8Array,
                                     nonce: Uint8Array,
                                     precomputedKey: Uint8Array) : Uint8Array
    fun crypto_box_seal(message: Uint8Array, recipientsPublicKey: Uint8Array) : Uint8Array
    fun crypto_box_seal_open(ciphertext: Uint8Array, recipientsPublicKey: Uint8Array, recipientsSecretKey: Uint8Array) : Uint8Array

    // ---- Box end ----

    // ---- Sign start ----
    fun crypto_sign(message: Uint8Array, secretKey: Uint8Array) : Uint8Array
    fun crypto_sign_detached(message: Uint8Array, secretKey: Uint8Array) : Uint8Array
    fun crypto_sign_ed25519_pk_to_curve25519(ed25519PublicKey: Uint8Array) : Uint8Array
    fun crypto_sign_ed25519_sk_to_curve25519(ed25519SecretKey: Uint8Array) : Uint8Array
    fun crypto_sign_ed25519_sk_to_pk(ed25519SecretKey: Uint8Array) : Uint8Array
    fun crypto_sign_ed25519_sk_to_seed(ed25519SecretKey: Uint8Array) : Uint8Array
    fun crypto_sign_final_create(state: dynamic, secretKey: Uint8Array) : Uint8Array
    fun crypto_sign_final_verify(state: dynamic, signature: Uint8Array, publicKey: Uint8Array) : Boolean
    fun crypto_sign_init() : dynamic
    fun crypto_sign_keypair() : dynamic
    fun crypto_sign_open(signedMessage: Uint8Array, publicKey: Uint8Array) : Uint8Array
    fun crypto_sign_seed_keypair(seed: Uint8Array) : dynamic
    fun crypto_sign_update(state: dynamic, message: Uint8Array)
    fun crypto_sign_verify_detached(signature: Uint8Array, message: Uint8Array, publicKey: Uint8Array) : Boolean


    // ---- Sign end ----


    // ---- KDF ----

    fun crypto_kdf_derive_from_key(subkey_len: UInt, subkeyId : UInt, ctx: String, key: Uint8Array) : Uint8Array
    fun crypto_kdf_keygen() : Uint8Array

    // ---- KDF end -----

    // ---- Password hashing ----

    fun crypto_pwhash(keyLength : UInt, password : Uint8Array, salt: Uint8Array, opsLimit: UInt, memLimit: UInt, algorithm: UInt) : Uint8Array
    fun crypto_pwhash_str(password: Uint8Array, opsLimit: UInt, memLimit: UInt) : String
    fun crypto_pwhash_str_needs_rehash(hashedPassword: String, opsLimit: UInt, memLimit: UInt) : Boolean
    fun crypto_pwhash_str_verify(hashedPassword: String, password: Uint8Array) : Boolean


    // ---- Password hashing end ----


    //util
    fun memzero(array: Uint8Array)




}
