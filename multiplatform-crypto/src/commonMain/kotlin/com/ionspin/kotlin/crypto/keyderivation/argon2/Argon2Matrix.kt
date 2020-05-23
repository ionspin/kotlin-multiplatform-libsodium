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
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-May-2020
 */
 class Argon2Matrix(val columnCount: Int, val rowCount: Int) {

    internal val storage: UByteArray = UByteArray(columnCount * rowCount * 1024)

    operator fun get(rowPosition: Int, columnPosition: Int, inBlockPosition: Int) : UByte {
        if (rowPosition > rowCount - 1) {
            throw RuntimeException("Invalid row (lane) requested: $rowPosition, rowCount: $rowCount")
        }
        if (columnPosition > columnCount - 1) {
            throw RuntimeException("Invalid column requested: $columnPosition, columnCount: $columnCount")
        }
        return storage[getBlockStartPositionPointer(rowPosition, columnPosition).intStorage + inBlockPosition]
    }

    operator fun get(absolutePosition: Int) : UByte {
        return storage[absolutePosition]
    }

    operator fun get(absolutePosition: BlockPointer) : UByte {
        return storage[absolutePosition.intStorage]
    }

    operator fun set(rowPosition: Int, columnPosition: Int, inBlockPosition: Int, value: UByte) {
        storage[getBlockStartPositionPointer(rowPosition, columnPosition).intStorage + inBlockPosition] = value
    }

    fun getBlockStartAndEndPositions(rowPosition: Int, columnPosition: Int) : Pair<BlockPointer, BlockPointer> {
        val start = getBlockStartPositionPointer(rowPosition, columnPosition)
        return Pair(
            start,
            start + 1024
        )
    }

    fun sliceArray(indices: IntRange): UByteArray {
        return storage.sliceArray(indices)
    }

    fun getBlockAt(rowPosition: Int, columnPosition: Int) : UByteArray {
                println("Expensive get")
        return storage.copyOfRange(
            getBlockStartPositionPointer(rowPosition, columnPosition).intStorage,
            getBlockStartPositionPointer(rowPosition, columnPosition).intStorage + 1024
        )
    }

    fun setBlockAt(rowPosition: Int, columnPosition: Int, blockValue: UByteArray) {
        println("Expensive set")
        blockValue.copyInto(
            storage,
            getBlockStartPositionPointer(rowPosition, columnPosition).intStorage
            )
    }

    private inline fun getBlockStartPositionPointer(rowPosition: Int, columnPosition: Int) : BlockPointer {
        return BlockPointer(rowPosition * columnCount * 1024 + columnPosition * 1024, this)
    }

//    operator fun get(rowPosition: Int, columnPosition: Int) : UByteArray {
//        println("Expensive.")
//        return storage.copyOfRange(
//            rowPosition * (columnCount - 1) * 1024 + columnPosition * 1024,
//            rowPosition * (columnCount - 1) * 1024 + columnPosition * 1024 + 1024
//        )
//    }
//


    internal fun clearMatrix() {
        for( index in storage.indices) { storage[index] = 0U }
    }
}
//TODO Decide: inline class without matrix reference?
class BlockPointer(val intStorage: Int, val matrix: Argon2Matrix) {
    operator fun rangeTo(other: BlockPointer): IntRange {
        return intStorage.rangeTo(other.intStorage)
    }

    infix fun until(to: BlockPointer): IntRange {
        if (to.intStorage <= Int.MIN_VALUE) return IntRange.EMPTY
        return this .. BlockPointer(to.intStorage - 1, matrix)
    }

    inline operator fun plus(other: BlockPointer) : BlockPointer {
        return BlockPointer(intStorage.plus(other.intStorage), matrix)
    }
    inline operator fun plus(other: Int) : BlockPointer {
        return BlockPointer(intStorage.plus(other), matrix)
    }

    inline operator fun minus(other: BlockPointer) : BlockPointer {
        return BlockPointer(intStorage.minus(other.intStorage), matrix)
    }
    inline operator fun times(other: BlockPointer) : BlockPointer {
        return BlockPointer(intStorage.times(other.intStorage), matrix)
    }
    inline operator fun div(other: BlockPointer) : BlockPointer {
        return BlockPointer(intStorage.div(other.intStorage), matrix)
    }
    inline operator fun rem(other: BlockPointer) : BlockPointer {
        return BlockPointer(intStorage.rem(other.intStorage), matrix)
    }

    infix fun xorBlocks(other: BlockPointer) : Block {
        return Block(UByteArray(1024){
            matrix[this.intStorage + it] xor other.matrix[other.intStorage + it]
        })
    }

}
@Suppress("NON_PUBLIC_PRIMARY_CONSTRUCTOR_OF_INLINE_CLASS")
inline class Block internal constructor(val storage: UByteArray) : Collection<UByte> {
    constructor() : this(UByteArray(1024))
    operator fun get(index: Int) : UByte {
        return storage.get(index)
    }
    operator fun set(index: Int, value: UByte) {
        storage.set(index, value)
    }

    override val size: Int get() = storage.size

    override operator fun iterator(): UByteIterator = Iterator(storage.toByteArray())

    private class Iterator(private val array: ByteArray) : UByteIterator() {
        private var index = 0
        override fun hasNext() = index < array.size
        override fun nextUByte() = if (index < array.size) array[index++].toUByte() else throw NoSuchElementException(index.toString())
    }
    //Taken from UByteArray implementation
    override fun contains(element: UByte): Boolean {
        // TODO: Eliminate this check after KT-30016 gets fixed.
        // Currently JS BE does not generate special bridge method for this method.
        if ((element as Any?) !is UByte) return false

        return storage.contains(element.toByte())
    }

    override fun containsAll(elements: Collection<UByte>): Boolean {
        return (elements as Collection<*>).all { it is UByte && storage.contains(it.toByte()) }
    }

    override fun isEmpty(): Boolean = this.storage.size == 0

    fun getRowOfULongsForMixing(rowIndex: Int) : ULongArray {
        val startOfRow = (rowIndex * 8 * 16)

        // Each row has 16 unsigned longs (16 ulongs * 8 bytes = 128 bytes) -- Argon2 considers this as 2 word unsigned
        // numbers, so strictly speaking argon representation is 8 * 8 matrix of 2 word unsigned numbers (registers
        val ulongArray = ULongArray(16)
        for (i in 0 until 16) {
            var ulong = 0UL
            //Now we create the ulong
            for (j in 0 until 8) {
                ulong = ulong or (storage[startOfRow + i * 8 + j].toULong() shl (j * 8))
            }
            ulongArray[i] = ulong
        }
        return ulongArray
    }

    /*
    @ExperimentalUnsignedTypes
        fun ULong.toLittleEndianUByteArray() :UByteArray {
            return UByteArray (8) {
                ((this shr (it * 8)) and 0xFFU).toUByte()
            }
        }
    //copy into gblock column
        for (i in 0..7) {
            val column = columnData.copyOfRange(i * 16, i * 16 + 16)
            column.copyInto(gBlock, i * 128 + columnPosition * 16)
        }
     */

    fun setRowFromMixedULongs(rowIndex: Int, ulongs: ULongArray) {
        val startOfRow = (rowIndex * 8 * 16)
        // Each row has 16 unsigned longs (16 ulongs * 8 bytes = 128 bytes) -- Argon2 considers this as 2 word unsigned
        // numbers, so strictly speaking argon representation is 8 * 8 matrix of 2 word unsigned numbers (registers
        for (i in 0 until 16) {
            val ulongToConvert = ulongs[i]
            for (j in 0 until 8) {
                storage[startOfRow + i * 8 + j] = ((ulongToConvert shr (j * 8)) and 0xFFU).toUByte()
            }
        }
    }
    /*
    val result = UByteArray(128) { 0U }
        for (i in 0..7) {
            gBlock.copyOfRange(i * 128 + (columnPosition * 16), i * 128 + (columnPosition * 16) + 16)
                .copyInto(result, i * 16)
        }
        return result
     */
    fun getColumnOfULongsForMixing(columnIndex: Int) : ULongArray {
        val ulongArray = ULongArray(16)

        for (i in 0 until 16) {
            var ulong = 0UL
            //Now we create the ulong
            for (j in 0 until 8) {
                ulong = ulong or (storage[i * 128 + columnIndex * 16 + j].toULong() shl (j * 8))
            }
        }
        return ulongArray
    }

}