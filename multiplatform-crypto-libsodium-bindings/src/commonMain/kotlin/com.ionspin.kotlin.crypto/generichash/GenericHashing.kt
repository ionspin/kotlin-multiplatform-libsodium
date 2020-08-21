package com.ionspin.kotlin.crypto.generichash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */
expect object GenericHashing {
    fun genericHash(message : UByteArray, requestedHashLength: Int, key : UByteArray? = null) : UByteArray
}
