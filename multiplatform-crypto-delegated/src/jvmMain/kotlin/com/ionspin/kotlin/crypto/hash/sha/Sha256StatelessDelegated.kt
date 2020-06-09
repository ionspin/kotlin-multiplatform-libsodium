package com.ionspin.kotlin.crypto.hash.sha

import java.security.MessageDigest


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */


actual class Sha256Delegated actual constructor(key: UByteArray?, hashLength: Int) : Sha256 {

    override fun update(data: UByteArray) {
        TODO("not implemented yet")
    }

    override fun digest(): UByteArray {
        TODO("not implemented yet")
    }

}

actual object Sha256StatelessDelegated : StatelessSha256 {

    override fun digest(inputMessage: UByteArray): UByteArray {
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(inputMessage.toByteArray())
        return messageDigest.digest().toUByteArray()
    }
}