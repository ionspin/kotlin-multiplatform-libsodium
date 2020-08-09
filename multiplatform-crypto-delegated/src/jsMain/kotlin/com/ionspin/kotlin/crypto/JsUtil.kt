package ext.libsodium.com.ionspin.kotlin.crypto

import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-Jun-2020
 *
 * TODO investigate using unsafe cast
 */
fun UByteArray.toUInt8Array() : Uint8Array {
    val uint8Result = Uint8Array(toByteArray().toTypedArray())
    return uint8Result
}


fun Uint8Array.toUByteArray() : UByteArray {
    val result = UByteArray(length)
    for (i in 0 until length) {
        result[i] = get(i).toUByte()
    }

    return result
}
