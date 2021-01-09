package com.ionspin.kotlin.crypto.sample

import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexStringToUByteArray

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 30-Oct-2020
 */
interface DataPackage {
    fun getContentAsUByteArray() : UByteArray
    fun getStringRepresentation() : String
}

data class Utf8StringData(val content: String) : DataPackage {
    override fun getContentAsUByteArray(): UByteArray {
        return content.encodeToUByteArray()
    }

    override fun getStringRepresentation(): String {
        return content
    }
}

data class HexadecimalStringData(val content: String) : DataPackage {
    override fun getContentAsUByteArray(): UByteArray {
        return content.hexStringToUByteArray()
    }

    override fun getStringRepresentation(): String {
        return content
    }
}

//data class FileData(val filePath: )
