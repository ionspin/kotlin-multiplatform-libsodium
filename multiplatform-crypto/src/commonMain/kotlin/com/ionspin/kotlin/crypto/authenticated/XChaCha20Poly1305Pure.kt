package com.ionspin.kotlin.crypto.authenticated

import com.ionspin.kotlin.bignum.Endianness
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.crypto.symmetric.ChaCha20Pure
import com.ionspin.kotlin.crypto.symmetric.XChaCha20Pure
import com.ionspin.kotlin.crypto.util.fromLittleEndianArrayToUIntWithPosition
import com.ionspin.kotlin.crypto.util.hexColumsPrint

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jun-2020
 */
class XChaCha20Poly1305Pure {
    companion object {

        fun encrypt(key: UByteArray, nonce: UByteArray, message: UByteArray, additionalData: UByteArray) : UByteArray {
            val oneTimeKey = XChaCha20Pure.hChacha(key, ubyteArrayOf(0U, 0U, 0U, 0U) + nonce.sliceArray(0 until 16))
//            val cipherText = XChaCha20Pure.encrypt(key, nonce, message, 1U)
            oneTimeKey.hexColumsPrint()
//            println("ciphertext")
//            cipherText.hexColumsPrint()
            return ubyteArrayOf()
        }
    }
}