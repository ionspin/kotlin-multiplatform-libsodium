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

@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.ionspin.kotlin.crypto.util

import com.ionspin.kotlin.crypto.keyderivation.argon2.ArgonBlockPointer

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 15-Jul-2019
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


infix fun UInt.rotateRight(places: Int): UInt {
    return (this shr places) xor (this shl (32 - places))
}


infix fun ULong.rotateRight(places: Int): ULong {
    return (this shr places) xor (this shl (64 - places))
}

infix fun UInt.rotateLeft(places: Int): UInt {
    return (this shl places) xor (this shr (32 - places))
}


infix fun ULong.rotateLeft(places: Int): ULong {
    return (this shl places) xor (this shr (64 - places))
}

infix fun Array<UByte>.xor(other : Array<UByte>) : Array<UByte> {
    if (this.size != other.size) {
        throw RuntimeException("Operands of different sizes are not supported yet")
    }
    return Array(this.size) { this[it] xor other[it] }
}


infix fun UByteArray.xor(other : UByteArray) : UByteArray {
    if (this.size != other.size) {
        throw RuntimeException("Operands of different sizes are not supported yet")
    }
    return UByteArray(this.size) { this[it] xor other[it] }
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

// UInt / Array utils

fun UInt.toBigEndianUByteArray() : Array<UByte> {
    return Array<UByte> (4) {
        ((this shr (24 - (it * 8))) and 0xFFU).toUByte()
    }
}

fun UInt.toLittleEndianTypedUByteArray() : Array<UByte> {
    return Array<UByte> (4) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}


fun UInt.toLittleEndianUByteArray() : UByteArray {
    return UByteArray (4) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}

// UInt / Array utils

fun ULong.toBigEndianUByteArray() : Array<UByte> {
    return Array<UByte> (8) {
        ((this shr (56 - (it * 8))) and 0xFFU).toUByte()
    }
}

fun ULong.toLittleEndianTypedUByteArray() : Array<UByte> {
    return Array<UByte> (8) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}


fun ULong.toLittleEndianUByteArray() :UByteArray {
    return UByteArray (8) {
        ((this shr (it * 8)) and 0xFFU).toUByte()
    }
}


fun Array<UByte>.fromLittleEndianArrayToULong() : ULong {
    if (this.size > 8) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var ulong = this.foldIndexed(0UL) { index, acc, uByte -> acc or (uByte.toULong() shl (index * 8))}
    return ulong
}


fun UByteArray.fromLittleEndianArrayToULong() : ULong {
    if (this.size > 8) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var ulong = this.foldIndexed(0UL) { index, acc, uByte -> acc or (uByte.toULong() shl (index * 8))}
    return ulong
}

fun UByteArray.arrayChunked(sliceSize: Int): List<UByteArray> {
    val last = this.size % sliceSize
    val hasLast = last != 0
    val numberOfSlices = this.size / sliceSize


    val result : MutableList<UByteArray> = MutableList<UByteArray>(0) { ubyteArrayOf() }

    for (i in 0 until numberOfSlices) {
        result.add(this.sliceArray(i * sliceSize until (i + 1) * sliceSize))
    }
    if (hasLast) {
        result.add(this.sliceArray(numberOfSlices * sliceSize until this.size))
    }

    return result
}



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


fun Array<UByte>.fromLittleEndianArrayToUInt() : UInt {
    if (this.size > 4) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var uint = this.foldIndexed(0U) { index, acc, uByte -> acc or (uByte.toUInt() shl (index * 8))}
    return uint
}


fun UByteArray.fromLittleEndianArrayToUInt() : UInt {
    if (this.size > 4) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var uint = this.foldIndexed(0U) { index, acc, uByte -> acc or (uByte.toUInt() shl (index * 8))}
    return uint
}

fun UByteArray.fromLittleEndianArrayToUintWithPosition(position: Int) : UInt{
    var uint = 0U
    for (i in 0 until 4) {
        uint = uint or (this[position + i].toUInt() shl (i * 8))
    }
    return uint
}

fun UByteArray.fromBigEndianArrayToUintWithPosition(position: Int) : UInt{
    var uint = 0U
    for (i in 0 until 4) {
        uint = uint shl 8 or (this[position + i].toUInt())
    }
    return uint
}

fun UByteArray.insertUIntAtPositionAsLittleEndian(position: Int, value: UInt) {
    for (i in position until position + 4) {
        this[i] = ((value shr (i * 8)) and 0xFFU).toUByte()
    }
}

fun UByteArray.insertUIntAtPositionAsBigEndian(position: Int, value: UInt) {
    for (i in position until position + 4) {
        this[i] = ((value shr (24 - i * 8)) and 0xFFU).toUByte()
    }
}

fun Array<UByte>.fromBigEndianArrayToUInt() : UInt {
    if (this.size > 4) {
        throw RuntimeException("ore than 8 bytes in input, potential overflow")
    }
    var uint = this.foldIndexed(0U) { index, acc, uByte -> acc or (uByte.toUInt() shl (24 - (index * 8))) }
    return uint
}


operator fun UInt.plus(other : UByteArray) : UByteArray {
    return this.toLittleEndianUByteArray() + other
}

//AES Flatten
fun Collection<UByteArray>.flattenToUByteArray(): UByteArray {
    val result = UByteArray(sumBy { it.size })
    var position = 0
    for (element in this) {
        element.forEach { uByte ->
            result[position] = uByte
            position++
        }
    }
    return result
}