package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Jun-2020
 */
fun Array<Byte>.hexColumsPrint() {
    val printout = this.map { it.toString(16) }.chunked(16)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

fun Array<UByte>.hexColumsPrint(chunk : Int = 16) {
    val printout = this.map { it.toString(16).padStart(2, '0') }.chunked(chunk)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

fun UByteArray.hexColumsPrint(chunk : Int = 16) {
    val printout = this.map { it.toString(16).padStart(2, '0') }.chunked(chunk)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

fun Array<ULong>.hexColumsPrint(chunk: Int = 3) {
    val printout = this.map { it.toString(16) }.chunked(chunk)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

fun String.hexStringToTypedUByteArray() : Array<UByte> {
    return this.chunked(2).map { it.toUByte(16) }.toTypedArray()
}


fun String.hexStringToUByteArray() : UByteArray {
    return this.chunked(2).map { it.toUByte(16) }.toUByteArray()
}

fun Array<UByte>.toHexString() : String {
    return this.joinToString(separator = "") {
        if (it <= 0x0FU) {
            "0${it.toString(16)}"
        } else {
            it.toString(16)
        }
    }
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
