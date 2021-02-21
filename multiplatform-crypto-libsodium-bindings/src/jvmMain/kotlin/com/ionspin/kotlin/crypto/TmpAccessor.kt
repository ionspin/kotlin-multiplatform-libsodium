package com.ionspin.kotlin.crypto

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Feb-2021
 */
object TmpAccessor {
    fun getVersion() : String = LibsodiumInitializer.sodiumJna.sodium_version_string()
}
