package com.ionspin.kotlin.crypto.hash.sha

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha512Delegated actual constructor(key: UByteArray?, hashLength: Int) : Sha512 {
    override fun update(data: UByteArray) {
        TODO("not implemented yet")
    }

    override fun digest(): UByteArray {
        TODO("not implemented yet")
    }

}

actual object Sha512StatelessDelegated : StatelessSha512 {

    override fun digest(inputMessage: UByteArray): UByteArray {
        TODO("not implemented yet")
    }
}