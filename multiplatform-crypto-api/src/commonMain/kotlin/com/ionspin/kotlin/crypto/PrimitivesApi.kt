package com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bMultipart
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2bProperties
import com.ionspin.kotlin.crypto.hash.sha.Sha256
import com.ionspin.kotlin.crypto.hash.sha.Sha512Multipart
import com.ionspin.kotlin.crypto.keyderivation.ArgonResult

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 23-Jun-2020
 */
interface PrimitivesApi {
    fun hashBlake2bMultipart(key: UByteArray? = null, hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): Blake2bMultipart
    fun hashBlake2b(message: UByteArray, key: UByteArray = ubyteArrayOf(), hashLength: Int = Blake2bProperties.MAX_HASH_BYTES): UByteArray

    fun hashSha256Multipart(): Sha256
    fun hashSha256(message: UByteArray) : UByteArray

    fun hashSha512Multipart(): Sha512Multipart
    fun hashSha512(message: UByteArray) : UByteArray

    fun deriveKey(
        password: String,
        salt: String? = null,
        key: String,
        associatedData: String,
        parallelism: Int = 16,
        tagLength: Int = 64,
        memory: Int = 4096,
        numberOfIterations: Int = 10,
    ) : ArgonResult

}


