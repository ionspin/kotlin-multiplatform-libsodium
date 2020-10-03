package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-Sep-2020
 */
val randombytes_SEEDBYTES = 32

expect object LibsodiumRandom {

    /**
     * The randombytes_buf() function fills size bytes starting at buf with an unpredictable sequence of bytes.
     */
    fun buf(size: Int) : UByteArray

    /**
     * The randombytes_buf_deterministic function stores size bytes into buf indistinguishable from random bytes without knowing seed.
     * For a given seed, this function will always output the same sequence. size can be up to 2^31 (~8GB) because we use kotlin arrays
     * and they are limited by Int primitive type
     * seed is randombytes_SEEDBYTES bytes long.
     * This function is mainly useful for writing tests, and was introduced in libsodium 1.0.12. Under the hood, it uses the ChaCha20 stream cipher.
     *
     */
    fun bufDeterministic(size: Int, seed: UByteArray) : UByteArray

    /**
     * The randombytes_random() function returns an unpredictable value between 0 and 0xffffffff (included).
     */
    fun random() : UInt

    /**
     * The randombytes_uniform() function returns an unpredictable value between 0 and upper_bound (excluded). Unlike r
     * andombytes_random() % upper_bound, it guarantees a uniform distribution of the possible output values even when
     * upper_bound is not a power of 2. Note that an upper_bound < 2 leaves only a single element to be chosen, namely 0
     */
    fun uniform(upperBound : UInt) : UInt
}
