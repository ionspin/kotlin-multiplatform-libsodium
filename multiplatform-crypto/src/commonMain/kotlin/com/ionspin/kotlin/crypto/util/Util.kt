/*
 *    Copyright 2019 Ugljesa Jovanovic
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.ionspin.kotlin.crypto.util

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 15-Jul-2019
 */
fun Array<Byte>.hexColumsPrint() {
    val printout = this.map { it.toString(16) }.chunked(16)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

fun Array<UByte>.hexColumsPrint() {
    val printout = this.map { it.toString(16) }.chunked(16)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

fun Array<ULong>.hexColumsPrint() {
    val printout = this.map { it.toString(16) }.chunked(3)
    printout.forEach { println(it.joinToString(separator = " ") { it.toUpperCase() }) }
}

inline fun <reified T> Array<T>.chunked(sliceSize: Int): Array<Array<T>> {
    val last = this.size % sliceSize
    val hasLast = last != 0
    val numberOfSlices = this.size / sliceSize


    val result : MutableList<List<T>> = MutableList<List<T>>(0) { emptyList() }

    for (i in 0 until numberOfSlices) {
        result.add(this.slice(i * sliceSize until (i + 1) * sliceSize))
    }
    if (hasLast) {
        result.add(this.slice(numberOfSlices * sliceSize until this.size))
    }

    return result.map { it.toTypedArray() }.toTypedArray()

}

@ExperimentalUnsignedTypes
infix fun UInt.rotateRight(places: Int): UInt {
    return (this shr places) xor (this shl (32 - places))
}

@ExperimentalUnsignedTypes
infix fun ULong.rotateRight(places: Int): ULong {
    return (this shr places) xor (this shl (64 - places))
}

@ExperimentalUnsignedTypes
infix fun Array<UByte>.xor(other : Array<UByte>) : Array<UByte> {
    if (this.size != other.size) {
        throw RuntimeException("Operands of different sizes are not supported yet")
    }
    return this.copyOf().mapIndexed { index, it -> it xor other[index] }.toTypedArray()
}

@ExperimentalUnsignedTypes
fun String.hexStringToUByteArray() : Array<UByte> {
    return this.chunked(2).map { it.toUByte(16) }.toTypedArray()
}

@ExperimentalUnsignedTypes
fun Array<UByte>.toHexString() : String {
    return this.joinToString(separator = "") {
        if (it <= 0x0FU) {
            "0${it.toString(16)}"
        } else {
            it.toString(16)
        }
    }
}

// UInt / Array utils
@ExperimentalUnsignedTypes
fun UInt.toBigEndianUByteArray() : Array<UByte> {
    return Array<UByte> (4) {
        ((this shr (24 - (it * 8))) and 0xFFU).toUByte()
    }
}
@ExperimentalUnsignedTypes
fun UInt.toLittleEndianUByteArray() : Array<UByte> {
    return Array<UByte> (4) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}

// UInt / Array utils
@ExperimentalUnsignedTypes
fun ULong.toBigEndianUByteArray() : Array<UByte> {
    return Array<UByte> (8) {
        ((this shr (56 - (it * 8))) and 0xFFU).toUByte()
    }
}
@ExperimentalUnsignedTypes
fun ULong.toLittleEndianUByteArray() : Array<UByte> {
    return Array<UByte> (8) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}
@ExperimentalUnsignedTypes
fun Array<UByte>.fromLittleEndianArrayToULong() : ULong {
    if (this.size > 8) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var ulong = this.foldIndexed(0UL) { index, acc, uByte -> acc or (uByte.toULong() shl (index * 8))}
    return ulong
}


@ExperimentalUnsignedTypes
fun Array<UByte>.fromBigEndianArrayToULong() : ULong {
    if (this.size > 8) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var ulong = this.foldIndexed(0UL) {
            index, acc, uByte ->
        val res = acc or (uByte.toULong() shl (56 - (index * 8)))
        res

    }
    return ulong
}

@ExperimentalUnsignedTypes
operator fun UInt.plus(other : Array<UByte>) : Array<UByte> {
    return this.toLittleEndianUByteArray() + other
}