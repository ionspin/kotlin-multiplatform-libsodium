package ext.libsodium.com.ionspin.kotlin.crypto

import org.khronos.webgl.Uint8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
interface JsSodiumInterface {

    fun crypto_hash_sha256(message: Uint8Array) : Uint8Array

    fun crypto_generichash(hashLength: Int, inputMessage: Uint8Array) : Uint8Array

    fun randombytes_buf(numberOfBytes: Int) : Uint8Array


}