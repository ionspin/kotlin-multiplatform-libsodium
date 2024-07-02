package com.ionspin.kotlin.crypto

import com.sun.jna.Library
import com.sun.jna.Pointer
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
    val state = LongArray(8)

    @JvmField
    var count: LongArray = LongArray(2)

    @JvmField
    val buf = ByteArray(128)
}

class Blake2bState : Structure() {
    override fun getFieldOrder(): List<String> = listOf("opaque")

    @JvmField
    val opaque = ByteArray(384)
}

//typedef struct crypto_sign_ed25519ph_state {
//    crypto_hash_sha512_state hs;
//} crypto_sign_ed25519ph_state;
class Ed25519SignatureState : Structure() {
    override fun getFieldOrder() = listOf("hs")

    @JvmField
    var hs: Hash512State = Hash512State()
}


//      typedef struct crypto_secretstream_xchacha20poly1305_state {
//          unsigned char k[crypto_stream_chacha20_ietf_KEYBYTES];
//          unsigned char nonce[crypto_stream_chacha20_ietf_NONCEBYTES];
//          unsigned char _pad[8];
//      } crypto_secretstream_xchacha20poly1305_state;
class SecretStreamXChaCha20Poly1305State : Structure() {
    override fun getFieldOrder(): List<String> = listOf("k", "nonce", "_pad")

    @JvmField
    val k = ByteArray(32)

    @JvmField
    val nonce = ByteArray(12)

    @JvmField
    val _pad = ByteArray(8)
}

interface JnaLibsodiumInterface : Library {

    // ---- Initialization ---

    fun sodium_init() : Int

    // ---- Initialization end ---

    // ---- Utils ----
    fun sodium_version_string(): String

    // void
    // randombytes_buf(void * const buf, const size_t size)
    fun randombytes_buf(buffer: ByteArray, bufferSize: Int)

    //    void randombytes_buf_deterministic(void * const buf, const size_t size,
    //    const unsigned char seed[randombytes_SEEDBYTES])
    fun randombytes_buf_deterministic(
        buffer: ByteArray,
        size: Int,
        seed: ByteArray
    )

    //    uint32_t randombytes_random(void)
    fun randombytes_random() : Long

    //    uint32_t randombytes_uniform(const uint32_t upper_bound);
    fun randombytes_uniform(
        upperBound: Long
    ) : Long

    //    void sodium_memzero(void * const pnt, const size_t len);
    fun sodium_memzero(array: ByteArray, len: Int)

    //    int sodium_memcmp(const void * const b1_, const void * const b2_, size_t len)
    fun sodium_memcmp(b1: ByteArray, b2: ByteArray, len: Int): Int

    //    char *sodium_bin2hex(char * const hex, const size_t hex_maxlen,
    //    const unsigned char * const bin, const size_t bin_len)
    fun sodium_bin2hex(
        hex: ByteArray,
        hexMaxlen: Int,
        bin: ByteArray,
        binLen: Int
    ): String

    //    int sodium_hex2bin(
    //    unsigned char * const bin, const size_t bin_maxlen,
    //    const char * const hex, const size_t hex_len,
    //    const char * const ignore, size_t * const bin_len,
    //    const char ** const hex_end)
    fun sodium_hex2bin(
        bin: ByteArray,
        binMaxLength: Int,
        hex: ByteArray,
        hexLen: Int,
        ignore: ByteArray?,
        binLen: Pointer,
        hexEnd: Pointer?
    ): Int

    //    int sodium_pad(size_t *padded_buflen_p, unsigned char *buf,
    //    size_t unpadded_buflen, size_t blocksize, size_t max_buflen)
    fun sodium_pad(
        paddedBufferLength: Pointer,
        buffer: ByteArray,
        unpaddedBufferLength: Int,
        blockSize: Int,
        maxBufferLength: Int
    ): Int

    //    int sodium_unpad(size_t *unpadded_buflen_p, const unsigned char *buf,
    //    size_t padded_buflen, size_t blocksize)
    fun sodium_unpad(
        unpaddedBufferLength: Pointer,
        buffer: ByteArray,
        paddedBufferLength: Int,
        blockSize: Int
    ) : Int


    //    char *sodium_bin2base64(char * const b64, const size_t b64_maxlen,
    //    const unsigned char * const bin, const size_t bin_len,
    //    const int variant)
    fun sodium_bin2base64(
        base64: ByteArray,
        base64MaxLength: Int,
        bin: ByteArray,
        binLength: Int,
        variant: Int
    ): Int

    //    int sodium_base642bin(
    //    unsigned char * const bin, const size_t bin_maxlen,
    //    const char * const b64, const size_t b64_len,
    //    const char * const ignore, size_t * const bin_len,
    //    const char ** const b64_end, const int variant)
    fun sodium_base642bin(
        bin: ByteArray,
        binMaxLength: Int,
        base64: ByteArray,
        base64Length: Int,
        ignore: ByteArray?,
        binLength: Pointer,
        base64End: Pointer?,
        variant: Int
    ): Int

    //    size_t sodium_base64_encoded_len(const size_t bin_len, const int variant)
    fun sodium_base64_encoded_len(binLength: Int, variant: Int): Int

    // --- Utils end ----

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
    ): Int

    //    int crypto_hash_sha256(unsigned char *out, const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha256(out: ByteArray, input: ByteArray, inputLength: Long): Int

    //    int crypto_hash_sha512(unsigned char *out, const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha512(out: ByteArray, input: ByteArray, inputLength: Long): Int
//
//    // ---- Generic hash ---- // Updateable
//

    //    int crypto_generichash_init(crypto_generichash_state *state,
    //    const unsigned char *key,
    //    const size_t keylen, const size_t outlen)
    //    Output cant be larger than 64 so no need to use Long to represent size_t here
    fun crypto_generichash_init(state: Blake2bState, key: ByteArray, keylen: Int, outlen: Int): Int

    //    int crypto_generichash_update(crypto_generichash_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_generichash_update(state: Blake2bState, inputMessage: ByteArray, inputLength: Long): Int

    //    int crypto_generichash_final(crypto_generichash_state *state,
    //    unsigned char *out, const size_t outlen)
    //    Output cant be larger than 64 so no need to use Long to represent size_t here
    fun crypto_generichash_final(state: Blake2bState, out: ByteArray, hashLength: Int): Int

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
    ): Int

    //    int crypto_generichash_blake2b_init(crypto_generichash_blake2b_state *state,
    //    const unsigned char *key,
    //    const size_t keylen, const size_t outlen)
    fun crypto_generichash_blake2b_init(kstate: ByteArray, key: ByteArray, keylen: Int, outlen: Int): Int

    //    int crypto_generichash_blake2b_update(crypto_generichash_blake2b_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_generichash_blake2b_update(state: ByteArray, inputMessage: ByteArray, inputLength: Long): Int

    //    int crypto_generichash_blake2b_final(crypto_generichash_blake2b_state *state,
    //    unsigned char *out,
    //    const size_t outlen)
    fun crypto_generichash_blake2b_final(state: ByteArray, out: ByteArray, hashLength: Int): Int

    //    void crypto_generichash_blake2b_keygen(unsigned char k[crypto_generichash_blake2b_KEYBYTES])
    fun crypto_generichash_blake2b_keygen(key: ByteArray)
//
//    // ---- Blake2b end ----
//
//    // ---- Short hash ----
//

    //    int crypto_shorthash(unsigned char *out, const unsigned char *in,
    //    unsigned long long inlen, const unsigned char *k)
    fun crypto_shorthash(out: ByteArray, input: ByteArray, inlen: Long, key: ByteArray): Int

    //    void crypto_shorthash_keygen(unsigned char k[crypto_shorthash_KEYBYTES])
    fun crypto_shorthash_keygen(key: ByteArray)

//
// ---- Short hash end ----
//

    //    int crypto_hash_sha256_init(crypto_hash_sha256_state *state)
    fun crypto_hash_sha256_init(state: Hash256State): Int

    //    int crypto_hash_sha256_update(crypto_hash_sha256_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha256_update(state: Hash256State, input: ByteArray, inlen: Long): Int

    //    int crypto_hash_sha256_final(crypto_hash_sha256_state *state,
    //    unsigned char *out)
    fun crypto_hash_sha256_final(state: Hash256State, out: ByteArray): Int


    //    int crypto_hash_sha512_init(crypto_hash_sha512_state *state)
    fun crypto_hash_sha512_init(state: Hash512State): Int

    //    int crypto_hash_sha512_update(crypto_hash_sha512_state *state,
    //    const unsigned char *in,
    //    unsigned long long inlen)
    fun crypto_hash_sha512_update(state: Hash512State, input: ByteArray, inlen: Long): Int

    //    int crypto_hash_sha512_final(crypto_hash_sha512_state *state,
    //    unsigned char *out)
    fun crypto_hash_sha512_final(state: Hash512State, out: ByteArray): Int


    //
    // --------------------- AEAD
    //

    //
    //    XChaCha20Poly1305Ietf
    //

    //    int crypto_aead_xchacha20poly1305_ietf_encrypt(
    //    unsigned char *c,
    //    unsigned long long *clen_p,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *nsec,
    //    const unsigned char *npub,
    //    const unsigned char *k
    //    )
    fun crypto_aead_xchacha20poly1305_ietf_encrypt(
        ciphertext: ByteArray,
        ciphertextLength: LongArray?,
        message: ByteArray,
        messageLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        nsec: ByteArray?,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_xchacha20poly1305_ietf_decrypt(
    //    unsigned char *m,
    //    unsigned long long *mlen_p,
    //    unsigned char *nsec,
    //    const unsigned char *c,
    //    unsigned long long clen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_xchacha20poly1305_ietf_decrypt(
        message: ByteArray,
        messageLength: LongArray?,
        nsec: ByteArray?,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_xchacha20poly1305_ietf_encrypt_detached(
    //    unsigned char *c,
    //    unsigned char *mac,
    //    unsigned long long *maclen_p,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *nsec,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_xchacha20poly1305_ietf_encrypt_detached(
        ciphertext: ByteArray,
        mac: ByteArray,
        maclen: LongArray?,
        message: ByteArray,
        messageLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        nsec: ByteArray?,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_xchacha20poly1305_ietf_decrypt_detached(
    //    unsigned char *m,
    //    unsigned char *nsec,
    //    const unsigned char *c,
    //    unsigned long long clen,
    //    const unsigned char *mac,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_xchacha20poly1305_ietf_decrypt_detached(
        message: ByteArray,
        nsec: ByteArray?,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        mac: ByteArray,
        additionalData: ByteArray,
        additionalDataLength: Long,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    void crypto_aead_xchacha20poly1305_ietf_keygen(unsigned char k[crypto_aead_xchacha20poly1305_ietf_KEYBYTES])
    fun crypto_aead_xchacha20poly1305_ietf_keygen(key: ByteArray)

    //
    //    ChaCha20Poly1305Ietf
    //

    //    int crypto_aead_chacha20poly1305_ietf_encrypt(
    //    unsigned char *c,
    //    unsigned long long *clen_p,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *nsec,
    //    const unsigned char *npub,
    //    const unsigned char *k
    //    )
    fun crypto_aead_chacha20poly1305_ietf_encrypt(
        ciphertext: ByteArray,
        ciphertextLength: LongArray?,
        message: ByteArray,
        messageLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        nsec: ByteArray?,
        npub: ByteArray,
        key: ByteArray
    ) : Int

    //    int crypto_aead_chacha20poly1305_ietf_decrypt(
    //    unsigned char *m,
    //    unsigned long long *mlen_p,
    //    unsigned char *nsec,
    //    const unsigned char *c,
    //    unsigned long long clen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_chacha20poly1305_ietf_decrypt(
        message: ByteArray,
        messageLength: LongArray?,
        nsec: ByteArray?,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_chacha20poly1305_ietf_encrypt_detached(
    //    unsigned char *c,
    //    unsigned char *mac,
    //    unsigned long long *maclen_p,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *nsec,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_chacha20poly1305_ietf_encrypt_detached(
        ciphertext: ByteArray,
        mac: ByteArray,
        maclen: LongArray?,
        message: ByteArray,
        messageLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        nsec: ByteArray?,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_chacha20poly1305_ietf_decrypt_detached(
    //    unsigned char *m,
    //    unsigned char *nsec,
    //    const unsigned char *c,
    //    unsigned long long clen,
    //    const unsigned char *mac,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_chacha20poly1305_ietf_decrypt_detached(
        message: ByteArray,
        nsec: ByteArray?,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        mac: ByteArray,
        additionalData: ByteArray,
        additionalDataLength: Long,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    void crypto_aead_chacha20poly1305_ietf_keygen(unsigned char k[crypto_aead_xchacha20poly1305_ietf_KEYBYTES])
    fun crypto_aead_chacha20poly1305_ietf_keygen(key: ByteArray)

    //
    //    ChaCha20Poly1305
    //

    //
    //    //decrypt
    //    int crypto_aead_xchacha20poly1305_encrypt(
    //    unsigned char *c,
    //    unsigned long long *clen_p,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *nsec,
    //    const unsigned char *npub,
    //    const unsigned char *k
    //    )
    fun crypto_aead_chacha20poly1305_encrypt(
        ciphertext: ByteArray,
        ciphertextLength: LongArray?,
        message: ByteArray,
        messageLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        nsec: ByteArray?,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_xchacha20poly1305_decrypt(unsigned char *m,
    //    unsigned long long *mlen_p,
    //    unsigned char *nsec,
    //    const unsigned char *c,
    //    unsigned long long clen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_chacha20poly1305_decrypt(
        message: ByteArray,
        messageLength: LongArray?,
        nsec: ByteArray?,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_chacha20poly1305_encrypt_detached(
    //    unsigned char *c,
    //    unsigned char *mac,
    //    unsigned long long *maclen_p,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *nsec,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_chacha20poly1305_encrypt_detached(
        ciphertext: ByteArray,
        mac: ByteArray,
        maclen: LongArray?,
        message: ByteArray,
        messageLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        nsec: ByteArray?,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_aead_chacha20poly1305_decrypt_detached(
    //    unsigned char *m,
    //    unsigned char *nsec,
    //    const unsigned char *c,
    //    unsigned long long clen,
    //    const unsigned char *mac,
    //    const unsigned char *ad,
    //    unsigned long long adlen,
    //    const unsigned char *npub,
    //    const unsigned char *k)
    fun crypto_aead_chacha20poly1305_decrypt_detached(
        message: ByteArray,
        nsec: ByteArray?,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        mac: ByteArray,
        additionalData: ByteArray,
        additionalDataLength: Long,
        npub: ByteArray,
        key: ByteArray
    ): Int

    //    void crypto_aead_chacha20poly1305_keygen(unsigned char k[crypto_aead_xchacha20poly1305_ietf_KEYBYTES])
    fun crypto_aead_chacha20poly1305_keygen(key: ByteArray)


    // ---- AEAD end ----

    // ---- Secret stream -----

    //    crypto_secretstream_xchacha20poly1305_headerbytes
    fun crypto_secretstream_xchacha20poly1305_headerbytes(): Int

    //encrypt

    //    int crypto_secretstream_xchacha20poly1305_init_push
    //    (crypto_secretstream_xchacha20poly1305_state *state,
    //    unsigned char header[crypto_secretstream_xchacha20poly1305_HEADERBYTES],
    //    const unsigned char k[crypto_secretstream_xchacha20poly1305_KEYBYTES])
    fun crypto_secretstream_xchacha20poly1305_init_push(
        state: SecretStreamXChaCha20Poly1305State,
        header: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_secretstream_xchacha20poly1305_push
    //    (crypto_secretstream_xchacha20poly1305_state *state,
    //    unsigned char *c, unsigned long long *clen_p,
    //    const unsigned char *m, unsigned long long mlen,
    //    const unsigned char *ad, unsigned long long adlen, unsigned char tag)
    fun crypto_secretstream_xchacha20poly1305_push(
        state: SecretStreamXChaCha20Poly1305State,
        ciphertext: ByteArray,
        ciphertextLength: LongArray?,
        message: ByteArray,
        messageLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long,
        tag: Byte
    ): Int

    //  decrypt

    //    int crypto_secretstream_xchacha20poly1305_init_pull
    //    (crypto_secretstream_xchacha20poly1305_state *state,
    //    const unsigned char header[crypto_secretstream_xchacha20poly1305_HEADERBYTES],
    //    const unsigned char k[crypto_secretstream_xchacha20poly1305_KEYBYTES])
    fun crypto_secretstream_xchacha20poly1305_init_pull(
        state: SecretStreamXChaCha20Poly1305State,
        header: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_secretstream_xchacha20poly1305_pull
    //    (crypto_secretstream_xchacha20poly1305_state *state,
    //    unsigned char *m, unsigned long long *mlen_p, unsigned char *tag_p,
    //    const unsigned char *c, unsigned long long clen,
    //    const unsigned char *ad, unsigned long long adlen)
    fun crypto_secretstream_xchacha20poly1305_pull(
        state: SecretStreamXChaCha20Poly1305State,
        message: ByteArray,
        messageLength: LongArray?,
        tagAddress: ByteArray,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        additionalData: ByteArray,
        additionalDataLength: Long
    ): Int

    //keygen and rekey

    //    void crypto_secretstream_xchacha20poly1305_keygen
    //    (unsigned char k[crypto_secretstream_xchacha20poly1305_KEYBYTES])
    fun crypto_secretstream_xchacha20poly1305_keygen(key: ByteArray)

    //    void crypto_secretstream_xchacha20poly1305_rekey
    //    (crypto_secretstream_xchacha20poly1305_state *state)
    fun crypto_secretstream_xchacha20poly1305_rekey(state: SecretStreamXChaCha20Poly1305State)


    // ---- Secret stream end -----
//
//    // ---- SecretBox ----

    //    int crypto_secretbox_detached(
    //    unsigned char *c, unsigned char *mac,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_secretbox_detached(
        ciphertext: ByteArray,
        mac: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_secretbox_easy(
    //    unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen, const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_secretbox_easy(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ): Int

    //    void crypto_secretbox_keygen(unsigned char k[crypto_secretbox_KEYBYTES])
    fun crypto_secretbox_keygen(key: ByteArray)

    //    int crypto_secretbox_open_detached(
    //    unsigned char *m,
    //    const unsigned char *c,
    //    const unsigned char *mac,
    //    unsigned long long clen,
    //    const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_secretbox_open_detached(
        message: ByteArray,
        ciphertext: ByteArray,
        mac: ByteArray,
        ciphertextLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ): Int

    //    int crypto_secretbox_open_easy(
    //    unsigned char *m, const unsigned char *c,
    //    unsigned long long clen, const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_secretbox_open_easy(
        message: ByteArray,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ): Int

    // ---- SecretBox End ----

    // ---- Auth ----

    //    int crypto_auth(unsigned char *out, const unsigned char *in,
    //    unsigned long long inlen, const unsigned char *k)
    fun crypto_auth(
        out: ByteArray,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray
    ): Int

    //    void crypto_auth_keygen(unsigned char k[crypto_auth_KEYBYTES])
    fun crypto_auth_keygen(key: ByteArray)

    //    int crypto_auth_verify(const unsigned char *h, const unsigned char *in,
    //    unsigned long long inlen, const unsigned char *k)
    fun crypto_auth_verify(
        hash: ByteArray,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray
    ): Int

    //Same params as general variant
    fun crypto_auth_hmacsha256(
        out: ByteArray,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray
    ): Int

    //Same params as general variant
    fun crypto_auth_hmacsha256_keygen(key: ByteArray)

    //Same params as general variant
    fun crypto_auth_hmacsha256_verify(
        hash: ByteArray,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray
    ): Int

    //Same params as general variant
    fun crypto_auth_hmacsha512(
        out: ByteArray,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray
    ): Int

    //Same params as general variant
    fun crypto_auth_hmacsha512_keygen(key: ByteArray)

    //Same params as general variant
    fun crypto_auth_hmacsha512_verify(
        hash: ByteArray,
        input: ByteArray,
        inputLength: Long,
        key: ByteArray
    ): Int

    //
//    // ---- Auth end ----
//
//    // ---- Box ----
//
    //    int crypto_box_keypair(unsigned char *pk, unsigned char *sk)
    fun crypto_box_keypair(publicKey: ByteArray, secretKey: ByteArray): Int

    //    int crypto_box_seed_keypair(unsigned char *pk, unsigned char *sk,
    //    const unsigned char *seed)
    fun crypto_box_seed_keypair(
        publicKey: ByteArray,
        secretKey: ByteArray,
        seed: ByteArray
    ): Int

    //    int crypto_box_easy(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen, const unsigned char *n,
    //    const unsigned char *pk, const unsigned char *sk)
    fun crypto_box_easy(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        recipientPublicKey: ByteArray,
        senderSecretKey: ByteArray
    ): Int

    //    int crypto_box_open_easy(unsigned char *m, const unsigned char *c,
    //    unsigned long long clen, const unsigned char *n,
    //    const unsigned char *pk, const unsigned char *sk)
    fun crypto_box_open_easy(
        message: ByteArray,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        nonce: ByteArray,
        senderPublickKey: ByteArray,
        recipientSecretKey: ByteArray
    ): Int

    //    int crypto_box_detached(unsigned char *c, unsigned char *mac,
    //    const unsigned char *m, unsigned long long mlen,
    //    const unsigned char *n, const unsigned char *pk,
    //    const unsigned char *sk)
    fun crypto_box_detached(
        ciphertext: ByteArray,
        mac: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        recipientPublicKey: ByteArray,
        senderSecretKey: ByteArray
    ): Int

    //    int crypto_box_open_detached(
    //    unsigned char *m, const unsigned char *c,
    //    const unsigned char *mac,
    //    unsigned long long clen,
    //    const unsigned char *n,
    //    const unsigned char *pk,
    //    const unsigned char *sk)
    fun crypto_box_open_detached(
        message: ByteArray,
        ciphertext: ByteArray,
        mac: ByteArray,
        ciphertextLength: Long,
        nonce: ByteArray,
        senderPublickKey: ByteArray,
        recipientSecretKey: ByteArray
    ): Int

    //    int crypto_box_beforenm(unsigned char *k, const unsigned char *pk,
    //    const unsigned char *sk)
    fun crypto_box_beforenm(
        sessionKey: ByteArray,
        publicKey: ByteArray,
        secretKey: ByteArray
    ): Int

    //    int crypto_box_easy_afternm(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen, const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_box_easy_afternm(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        sessionKey: ByteArray
    ): Int

    //    int crypto_box_open_easy_afternm(unsigned char *m, const unsigned char *c,
    //    unsigned long long clen, const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_box_open_easy_afternm(
        message: ByteArray,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        nonce: ByteArray,
        sessionKey: ByteArray
    ): Int

    //    int crypto_box_seal(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen, const unsigned char *pk)
    fun crypto_box_seal(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        recipientPublicKey: ByteArray
    ): Int


    //    int crypto_box_seal_open(unsigned char *m, const unsigned char *c,
    //    unsigned long long clen,
    //    const unsigned char *pk, const unsigned char *sk)
    fun crypto_box_seal_open(
        message: ByteArray,
        ciphertext: ByteArray,
        ciphertextLength: Long,
        senderPublickKey: ByteArray,
        recipientSecretKey: ByteArray
    ): Int
//
//    // ---- Box end ----
//
//    // ---- Sign start ----

    //    int crypto_sign(
    //    unsigned char *sm, unsigned long long *smlen_p,
    //    const unsigned char *m, unsigned long long mlen,
    //    const unsigned char *sk)
    fun crypto_sign(
        signedMessage: ByteArray,
        signedMessageLength: LongArray?,
        message: ByteArray,
        messageLength: Long,
        secretKey: ByteArray
    ): Int

    //    int crypto_sign_open(
    //    unsigned char *m, unsigned long long *mlen_p,
    //    const unsigned char *sm, unsigned long long smlen,
    //    const unsigned char *pk)
    fun crypto_sign_open(
        message: ByteArray,
        messageLength: LongArray?,
        signedMessage: ByteArray,
        signedMessageLength: Long,
        publicKey: ByteArray
    ): Int

    //    int crypto_sign_detached(
    //    unsigned char *sig, unsigned long long *siglen_p,
    //    const unsigned char *m, unsigned long long mlen,
    //    const unsigned char *sk)
    fun crypto_sign_detached(
        signature: ByteArray,
        signatureLength: LongArray?,
        message: ByteArray,
        messageLength: Long,
        secretKey: ByteArray
    ): Int

    //    int crypto_sign_verify_detached(
    //    const unsigned char *sig,
    //    const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *pk)
    fun crypto_sign_verify_detached(
        signature: ByteArray,
        message: ByteArray,
        messageLength: Long,
        publicKey: ByteArray
    ): Int

    //    int crypto_sign_ed25519_pk_to_curve25519(
    //    unsigned char *curve25519_pk,
    //    const unsigned char *ed25519_pk)
    fun crypto_sign_ed25519_pk_to_curve25519(
        curve25519PublicKey: ByteArray,
        ed25519PublicKey: ByteArray
    ): Int

    //    int crypto_sign_ed25519_sk_to_curve25519(unsigned char *curve25519_sk,
    //    const unsigned char *ed25519_sk)
    fun crypto_sign_ed25519_sk_to_curve25519(
        curve25519SecretKey: ByteArray,
        ed25519SecretKey: ByteArray
    ): Int

    //    int crypto_sign_ed25519_sk_to_pk(unsigned char *pk, const unsigned char *sk)
    fun crypto_sign_ed25519_sk_to_pk(
        ed25519PublicKey: ByteArray,
        ed25519SecretKey: ByteArray
    ): Int

    //    int crypto_sign_ed25519_sk_to_seed(unsigned char *seed,
    //    const unsigned char *sk)
    fun crypto_sign_ed25519_sk_to_seed(
        seed: ByteArray,
        ed25519SecretKey: ByteArray
    ): Int

    //    int crypto_sign_init(crypto_sign_state *state);
    fun crypto_sign_init(state: Ed25519SignatureState)

    //    int crypto_sign_update(crypto_sign_state *state,
    //    const unsigned char *m, unsigned long long mlen)
    fun crypto_sign_update(
        state: Ed25519SignatureState,
        message: ByteArray,
        messageLength: Long
    ): Int

    //    int crypto_sign_final_create(crypto_sign_state *state, unsigned char *sig,
    //    unsigned long long *siglen_p,
    //    const unsigned char *sk)
    fun crypto_sign_final_create(
        state: Ed25519SignatureState,
        signature: ByteArray,
        signatureLength: LongArray?,
        secretKey: ByteArray
    ): Int

    //    int crypto_sign_final_verify(crypto_sign_state *state, const unsigned char *sig,
    //    const unsigned char *pk)
    fun crypto_sign_final_verify(
        state: Ed25519SignatureState,
        signature: ByteArray,
        publicKey: ByteArray
    ): Int

    //    int crypto_sign_keypair(unsigned char *pk, unsigned char *sk)
    fun crypto_sign_keypair(
        publicKey: ByteArray, secretKey: ByteArray
    ): Int

    //    int crypto_sign_seed_keypair(unsigned char *pk, unsigned char *sk,
    //    const unsigned char *seed)
    fun crypto_sign_seed_keypair(
        publicKey: ByteArray,
        secretKey: ByteArray,
        seed: ByteArray
    ): Int


//    // ---- Sign end ----
//
//


//    // ---- KDF ----
//

    //    int crypto_kdf_derive_from_key(unsigned char *subkey, size_t subkey_len,
    //    uint64_t subkey_id,
    //    const char ctx[crypto_kdf_CONTEXTBYTES],
    //    const unsigned char key[crypto_kdf_KEYBYTES])
    fun crypto_kdf_derive_from_key(
        subkey: ByteArray,
        subkeyLength: Int,
        subkeyId: Long,
        context: ByteArray,
        key: ByteArray
    ): Int

    //    void crypto_kdf_keygen(unsigned char k[crypto_kdf_KEYBYTES])
    fun crypto_kdf_keygen(
        key: ByteArray
    )
//
//    // ---- KDF end -----
//


    //    // ---- Password hashing ----
//
    //    int crypto_pwhash(unsigned char * const out, unsigned long long outlen,
    //    const char * const passwd, unsigned long long passwdlen,
    //    const unsigned char * const salt,
    //    unsigned long long opslimit, size_t memlimit, int alg)
    fun crypto_pwhash(
        output: ByteArray,
        outputLength: Long,
        password: String,
        passwordLength: Long,
        salt: ByteArray,
        opslimit: Long,
        memlimit: Long,
        algorithm : Int
    ) : Int

    //    int crypto_pwhash_str(char out[crypto_pwhash_STRBYTES],
    //    const char * const passwd, unsigned long long passwdlen,
    //    unsigned long long opslimit, size_t memlimit)
    fun crypto_pwhash_str(
        output: ByteArray,
        password: String,
        passwordLength: Long,
        opslimit: Long,
        memlimit: Long
    ) : Int

    //    int crypto_pwhash_str_needs_rehash(const char str[crypto_pwhash_STRBYTES],
    //    unsigned long long opslimit, size_t memlimit)
    fun crypto_pwhash_str_needs_rehash(
        output: String,
        opslimit: Long,
        memlimit: Long
    ) : Int
    //    int crypto_pwhash_str_verify(const char str[crypto_pwhash_STRBYTES],
    //    const char * const passwd,
    //    unsigned long long passwdlen)
    fun crypto_pwhash_str_verify(
        hash: String,
        password: String,
        passwordLength: Long
    ) : Int

//
//    // ---- Password hashing end ----
//



//    // ---- Key exchange ----


    //    int crypto_kx_keypair(unsigned char pk[crypto_kx_PUBLICKEYBYTES],
    //    unsigned char sk[crypto_kx_SECRETKEYBYTES])
    fun crypto_kx_keypair(
        publicKey: ByteArray,
        secretKey: ByteArray
    ): Int


    //    int crypto_kx_seed_keypair(unsigned char pk[crypto_kx_PUBLICKEYBYTES],
    //    unsigned char sk[crypto_kx_SECRETKEYBYTES],
    //    const unsigned char seed[crypto_kx_SEEDBYTES])
    fun crypto_kx_seed_keypair(
        publicKey: ByteArray,
        secretKey: ByteArray,
        seed: ByteArray
    ): Int
    //    int crypto_kx_client_session_keys(unsigned char rx[crypto_kx_SESSIONKEYBYTES],
    //    unsigned char tx[crypto_kx_SESSIONKEYBYTES],
    //    const unsigned char client_pk[crypto_kx_PUBLICKEYBYTES],
    //    const unsigned char client_sk[crypto_kx_SECRETKEYBYTES],
    //    const unsigned char server_pk[crypto_kx_PUBLICKEYBYTES])
    fun crypto_kx_client_session_keys(
        receiveKey : ByteArray,
        sendKey: ByteArray,
        clientPublicKey: ByteArray,
        clientSecretKey: ByteArray,
        serverPublicKey: ByteArray
    ): Int
    //    int crypto_kx_server_session_keys(unsigned char rx[crypto_kx_SESSIONKEYBYTES],
    //    unsigned char tx[crypto_kx_SESSIONKEYBYTES],
    //    const unsigned char server_pk[crypto_kx_PUBLICKEYBYTES],
    //    const unsigned char server_sk[crypto_kx_SECRETKEYBYTES],
    //    const unsigned char client_pk[crypto_kx_PUBLICKEYBYTES])
    fun crypto_kx_server_session_keys(
        receiveKey: ByteArray,
        sendKey: ByteArray,
        serverPublicKey: ByteArray,
        serverSecretKey: ByteArray,
        clientPublicKey: ByteArray
    ): Int

//
//    // ---- Key exchange end ----
//
//    // -- Stream ----

    //    int crypto_stream_chacha20(unsigned char *c, unsigned long long clen,
    //    const unsigned char *n, const unsigned char *k)
    fun crypto_stream_chacha20(
        stream: ByteArray,
        streamLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ) : Int
    //    int crypto_stream_chacha20_xor(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen, const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_stream_chacha20_xor(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ) : Int
    //    int crypto_stream_chacha20_xor_ic(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *n, uint64_t ic,
    //    const unsigned char *k)
    fun crypto_stream_chacha20_xor_ic(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        initialCounter : Long,
        key: ByteArray
    ) : Int

    //    int crypto_stream_chacha20_ietf(unsigned char *c, unsigned long long clen,
    //    const unsigned char *n, const unsigned char *k)
    fun crypto_stream_chacha20_ietf(
        stream: ByteArray,
        streamLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ) : Int

    //    int crypto_stream_chacha20_ietf_xor(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen, const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_stream_chacha20_ietf_xor(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ) : Int

    //    int crypto_stream_chacha20_ietf_xor_ic(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *n, uint32_t ic,
    //    const unsigned char *k)
    fun crypto_stream_chacha20_ietf_xor_ic(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        initialCounter : Int,
        key: ByteArray
    ) : Int

    //    void crypto_stream_chacha20_keygen(unsigned char k[crypto_stream_chacha20_KEYBYTES])
    fun crypto_stream_chacha20_keygen(key: ByteArray)

    //    int crypto_stream_xchacha20(unsigned char *c, unsigned long long clen,
    //    const unsigned char *n, const unsigned char *k)
    fun crypto_stream_xchacha20(
        stream: ByteArray,
        streamLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ) : Int

    //    int crypto_stream_xchacha20_xor(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen, const unsigned char *n,
    //    const unsigned char *k)
    fun crypto_stream_xchacha20_xor(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        key: ByteArray
    ) : Int

    //    int crypto_stream_xchacha20_xor_ic(unsigned char *c, const unsigned char *m,
    //    unsigned long long mlen,
    //    const unsigned char *n, uint64_t ic,
    //    const unsigned char *k)
    fun crypto_stream_xchacha20_xor_ic(
        ciphertext: ByteArray,
        message: ByteArray,
        messageLength: Long,
        nonce: ByteArray,
        initialCounter : Long,
        key: ByteArray
    ) : Int
    //    void crypto_stream_xchacha20_keygen(unsigned char k[crypto_stream_xchacha20_KEYBYTES])
    fun crypto_stream_xchacha20_keygen(key: ByteArray)


//
//    // ---- Stream end ----
//
//    // ---- Scalar multiplication ----
//
    //    int crypto_scalarmult(unsigned char *q, const unsigned char *n,
    //    const unsigned char *p)
    fun crypto_scalarmult(q: ByteArray, n: ByteArray, p: ByteArray): Int
    //    int crypto_scalarmult_base(unsigned char *q, const unsigned char *n)
    fun crypto_scalarmult_base(q: ByteArray, b: ByteArray): Int
//
//    // ---- Scalar multiplication end ----
}
