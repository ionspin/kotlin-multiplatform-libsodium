package com.ionspin.kotlin.crypto.generichash

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Aug-2020
 */

data class GenericHashState(val hashLength: Int, val state: GenericHashStateInternal)

expect class GenericHashStateInternal

expect object GenericHashing {




    fun genericHash(message : UByteArray, requestedHashLength: Int, key : UByteArray? = null) : UByteArray

    fun genericHashInit(requestedHashLength: Int, key : UByteArray? = null) : GenericHashState
    fun genericHashUpdate(state: GenericHashState, messagePart : UByteArray)
    fun genericHashFinal(state : GenericHashState) : UByteArray

}


