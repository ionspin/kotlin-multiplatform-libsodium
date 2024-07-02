package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 28-Aug-2020
 */
fun String.hexStringToUByteArray() : UByteArray {
    return this.chunked(2).map { it.toUByte(16) }.toUByteArray()
}

fun String.encodeToUByteArray() : UByteArray{
    return encodeToByteArray().asUByteArray()
}

fun UByteArray.decodeFromUByteArray() : String {
    return asByteArray().decodeToString()
}

fun UByteArray.toHexString() : String {
    return this.joinToString(separator = "") {
        if (it <= 0x0FU) {
            "0${it.toString(16)}"
        } else {
            it.toString(16)
        }
    }
}

fun Array<UByte>.hexColumnsPrint(chunk: Int = 16) {
    val printout = this.map { it.toString(16).padStart(2, '0') }.chunked(chunk)
    printout.forEach { println(it.joinToString(separator = " ") { it.uppercase() }) }
}

fun UByteArray.hexColumnsPrint(chunk: Int = 16) {
    val printout = this.map { it.toString(16).padStart(2, '0') }.chunked(chunk)
    printout.forEach { println(it.joinToString(separator = " ") { it.uppercase() }) }
}

/**
 * Functions returning an int return 0 on success and -1 to indicate an error.
 *
 * This will throw an [IllegalStateException] if the return code is invalid/unknown.
 */
fun Int.isLibsodiumSuccessCode(): Boolean {
    return when (this) {
        0 -> true
        -1 -> false
        else -> throw IllegalStateException("Libsodium returned an unexpected return code $this")
    }
}
