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

@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package com.ionspin.kotlin.crypto.keyderivation.argon2

/**
 * Represents a pointer to a Argon2 Block, this abstracts what the backing structure is, a Argon 2 Matrix or
 * or a UByteArray block
 */
interface ArgonBlockPointer {
    val blockStartPosition: Int

    fun pointerArithmetic(block : (Int, Int) -> Int) : ArgonBlockPointer

    infix fun xorBlocksAndGetPointerToNewBlock(other: ArgonBlockPointer) : ArgonBlock

    operator fun get(blockPosition: Int) : UByte

    operator fun set(blockPosition: Int, value: UByte)

    infix fun xorInplaceWith(other: ArgonBlockPointer) : ArgonBlockPointer {
        (0 until 1024).forEach {
            this[it] = this[it] xor other[it]
        }
        return this //For chaining
    }

    fun asInt() : Int

    fun getAsUByteArray() : UByteArray
}

fun ArgonBlockPointer.getRowOfULongsForMixing(rowIndex: Int) : ULongArray {
    // Each row has 16 unsigned longs (16 ulongs * 8 bytes = 128 bytes) -- Argon2 considers this as 2 word unsigned
    // numbers, so strictly speaking argon representation is 8 * 8 matrix of 2 word unsigned numbers (registers
    val ulongArray = ULongArray(16)
    for (columnIndex in 0 until 16) {
        var ulong = 0UL
        //Now we create the ulong
        for (bytePosition in 0 until 8) {
            ulong = ulong or (this[rowIndex * 128 + columnIndex * 8 + bytePosition].toULong() shl (bytePosition * 8))
        }
        ulongArray[columnIndex] = ulong
    }
    return ulongArray
}

fun ArgonBlockPointer.setRowFromMixedULongs(rowIndex: Int, ulongs: ULongArray) {
    // Each row has 16 unsigned longs (16 ulongs * 8 bytes = 128 bytes) -- Argon2 considers this as 2 word unsigned
    // numbers, so strictly speaking argon representation is 8 * 8 matrix of 2 word unsigned numbers (registers
    for (columnIndex in 0 until 16) {
        val ulongToConvert = ulongs[columnIndex]
        for (bytePosition in 0 until 8) {
            this[rowIndex * 128 + columnIndex * 8 + bytePosition] = ((ulongToConvert shr (bytePosition * 8)) and 0xFFU).toUByte()
        }
    }
}

fun ArgonBlockPointer.getColumnOfULongsForMixing(columnIndex: Int) : ULongArray {
    //In Argon2 representation there are 8 double word registers (numbers, but we work with 16 single word ulongs
    val ulongArray = ULongArray(16)
    //There are 8 rows that consist of 2 words (registers, in our case ulongs) each
    for (rowIndex in 0 until 8) {
        var ulong = 0UL
        //Now we create the ulong
        for (bytePosition in 0 until 8) {
            ulong = ulong or (this[rowIndex * 128 + columnIndex * 16 + bytePosition].toULong() shl (bytePosition * 8))
        }
        ulongArray[rowIndex * 2] = ulong
        ulong = 0UL
        // But unlike in columns where we can directly iterate and get all TWO WORD registers, here we also need to grab
        // the next word
        for (bytePosition in 8 until 16) {
            ulong = ulong or (this[rowIndex * 128 + columnIndex * 16 + bytePosition].toULong() shl (bytePosition * 8))
        }
        ulongArray[rowIndex * 2 + 1] = ulong
    }
    return ulongArray
}

fun ArgonBlockPointer.setColumnFromMixedULongs(columnIndex: Int, ulongs: ULongArray)  {
    //In Argon2 representation there are 8 double word registers (numbers, but we work with 16 single word ulongs
    //There are 8 rows that consist of 2 words (registers, in our case ulongs) each
    var ulongToConvert = 0UL
    for (rowIndex in 0 until 8) {
        ulongToConvert = ulongs[rowIndex * 2]
        //Now we create the ulong
        for (bytePosition in 0 until 8) {
            this[rowIndex * 128 + columnIndex * 16 + bytePosition] = ((ulongToConvert shr (bytePosition * 8)) and 0xFFU).toUByte()
        }
        // But unlike in columns where we can directly iterate and get all TWO WORD registers, here we also need to set
        // the next word
        ulongToConvert = ulongs[rowIndex * 2 + 1]
        for (bytePosition in 8 until 16) {
            this[rowIndex * 128 + columnIndex * 16 + bytePosition] = ((ulongToConvert shr (bytePosition * 8)) and 0xFFU).toUByte()
        }
    }
}

fun ArgonBlockPointer.getUIntFromPosition(positionInBlock: Int) : UInt {
    var uint = 0U
    for (i in 0 until 4) {
        uint = uint or (this[positionInBlock + i].toUInt() shl (i * 8))
    }
    return uint
}

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-May-2020
 */
 class ArgonMatrix(val columnCount: Int, val rowCount: Int) {

    internal val storage: UByteArray = UByteArray(columnCount * rowCount * 1024)

    operator fun get(rowPosition: Int, columnPosition: Int, inBlockPosition: Int) : UByte {
        if (rowPosition > rowCount - 1) {
            throw RuntimeException("Invalid row (lane) requested: $rowPosition, rowCount: $rowCount")
        }
        if (columnPosition > columnCount - 1) {
            throw RuntimeException("Invalid column requested: $columnPosition, columnCount: $columnCount")
        }
        return storage[getBlockStartPositionPointer(rowPosition, columnPosition) + inBlockPosition]
    }

    val size = storage.size

    operator fun get(absolutePosition: Int) : UByte {
        return storage[absolutePosition]
    }

    operator fun set(rowPosition: Int, columnPosition: Int, inBlockPosition: Int, value: UByte) {
        storage[getBlockStartPositionPointer(rowPosition, columnPosition) + inBlockPosition] = value
    }

    operator fun set(absolutePosition: Int, value: UByte) {
        storage[absolutePosition] = value
    }

    fun getBlockPointer(rowPosition: Int, columnPosition: Int) : ArgonBlockPointer {
        return ArgonBlockPointerWithMatrix(getBlockStartPositionPointer(rowPosition, columnPosition), this)
    }


    fun sliceArray(indices: IntRange): UByteArray {
        return storage.sliceArray(indices)
    }

    fun getBlockAt(rowPosition: Int, columnPosition: Int) : UByteArray {
                println("Expensive get")
        return storage.copyOfRange(
            getBlockStartPositionPointer(rowPosition, columnPosition),
            getBlockStartPositionPointer(rowPosition, columnPosition) + 1024
        )
    }

    fun setBlockAt(rowPosition: Int, columnPosition: Int, blockValue: UByteArray) {
        println("Expensive set")
        blockValue.copyInto(
            storage,
            getBlockStartPositionPointer(rowPosition, columnPosition)
            )
    }

    private inline fun getBlockStartPositionPointer(rowPosition: Int, columnPosition: Int) : Int {
        return rowPosition * columnCount * 1024 + columnPosition * 1024
    }

    internal fun clearMatrix() {
        for( index in storage.indices) { storage[index] = 0U }
    }

    private class ArgonBlockPointerWithMatrix constructor(override val blockStartPosition: Int, val matrix: ArgonMatrix) : ArgonBlockPointer {

        override fun pointerArithmetic(block: (Int, Int) -> Int): ArgonBlockPointer {
            return ArgonBlockPointerWithMatrix(block(blockStartPosition, matrix.size), matrix)
        }

        override fun asInt(): Int {
            return blockStartPosition
        }

        override operator fun get(blockPosition: Int) : UByte {
            return matrix[blockStartPosition + blockPosition]
        }

        override fun set(blockPosition: Int, value: UByte) {
            matrix[blockStartPosition + blockPosition] = value
        }

        override infix fun xorBlocksAndGetPointerToNewBlock(other: ArgonBlockPointer) : ArgonBlock {
            return ArgonBlock(UByteArray(1024){
                matrix[blockStartPosition + it] xor other[it]
            })
        }

        override fun getAsUByteArray(): UByteArray {
            return matrix.storage.slice(blockStartPosition until blockStartPosition + 1024).toUByteArray()
        }
    }
}




@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class ArgonBlock internal constructor(internal val storage: UByteArray) {
    constructor() : this(UByteArray(1024))
    operator fun get(index: Int) : UByte {
        return storage.get(index)
    }
    operator fun set(index: Int, value: UByte) {
        storage.set(index, value)
    }

    val size: Int get() = storage.size

    internal fun getAsUByteArray() : UByteArray = storage

    fun getBlockPointer() : ArgonBlockPointer{
        return ArgonBlockPointerWithBlock( this)
    }

    infix fun xorInplaceWith(other: ArgonBlock) : ArgonBlock {
        storage.indices.forEach {
            this[it] = this[it] xor other[it]
        }
        return this //For chaining
    }



    private class ArgonBlockPointerWithBlock constructor(val storageBlock: ArgonBlock) : ArgonBlockPointer {
        override val blockStartPosition: Int = 0

        override fun pointerArithmetic(block: (Int, Int) -> Int): ArgonBlockPointer {
            throw RuntimeException("Haven't really tought out pointer arithmetic with blocks")
        }

        override fun asInt(): Int {
            return blockStartPosition
        }
        override operator fun get(blockPosition: Int) : UByte {
            return storageBlock[blockPosition]
        }

        override fun set(blockPosition: Int, value: UByte) {
            storageBlock[blockPosition] = value
        }

        override infix fun xorBlocksAndGetPointerToNewBlock(other: ArgonBlockPointer) : ArgonBlock {
            return ArgonBlock(UByteArray(1024){
                storageBlock[it] xor other[it]
            })
        }

        override fun getAsUByteArray(): UByteArray {
            return storageBlock.storage.slice(blockStartPosition until blockStartPosition + 1024).toUByteArray()
        }

    }

}