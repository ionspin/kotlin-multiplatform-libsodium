package com.ionspin.kotlin.crypto.hash.sha

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha256Delegated actual constructor(key: UByteArray?, hashLength: Int) : Sha256 {

    override fun update(data: UByteArray) {
        TODO("not implemented yet")
    }

    override fun update(data: String) {
        TODO("not implemented yet")
    }

    override fun digest(): UByteArray {
        TODO("not implemented yet")
    }




}
actual object Sha256StatelessDelegated : StatelessSha256 {
    override fun digest(inputString: String, key: String?, hashLength: Int): UByteArray {
        TODO("not implemented yet")
    }

    override fun digest(inputMessage: UByteArray, key: UByteArray, hashLength: Int): UByteArray {
        TODO("not implemented yet")
    }

}