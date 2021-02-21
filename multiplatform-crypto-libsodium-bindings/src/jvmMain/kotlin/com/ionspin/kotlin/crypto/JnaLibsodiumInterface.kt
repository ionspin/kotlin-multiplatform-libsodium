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
    val state = IntArray(8)

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

    // ---- Utils ----
    fun sodium_version_string(): String

    fun randombytes_buf(buffer: ByteArray, bufferSize: Int)

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
    )


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
    fun crypto_shorthash_keygen(key: ByteArray): Int

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
    )

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
