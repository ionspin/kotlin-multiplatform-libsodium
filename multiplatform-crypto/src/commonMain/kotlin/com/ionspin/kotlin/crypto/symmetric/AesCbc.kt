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

package com.ionspin.kotlin.crypto.symmetric

import com.ionspin.kotlin.crypto.SRNG
import com.ionspin.kotlin.crypto.xor

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Sep-2019
 */
@ExperimentalUnsignedTypes
class AesCbc (val aesKey: AesKey) {

    companion object {
        val BLOCK_BYTES = 16
    }

    var currentOutput : Array<UByte> = arrayOf()
    val iv = SRNG.getRandomBytes(16)

    val output = MutableList<Array<UByte>>(0) { arrayOf() }

    val buffer : Array<UByte> = UByteArray(16) { 0U }.toTypedArray()
    var bufferCounter = 0


//    fun addData(data : UByteArray) {
//        //Padding
//        when {
//            bufferCounter + data.size < BLOCK_BYTES -> appendToBuffer(data, bufferCounter)
//            bufferCounter + data.size >= BLOCK_BYTES -> {
//                val chunked = data.chunked(BLOCK_BYTES)
//                chunked.forEach { chunk ->
//                    if (bufferCounter + chunk.size < BLOCK_BYTES) {
//                        appendToBuffer(chunk, bufferCounter)
//                    } else {
//                        chunk.copyInto(
//                            destination = buffer,
//                            destinationOffset = bufferCounter,
//                            startIndex = 0,
//                            endIndex = BLOCK_BYTES - bufferCounter
//                        )
//                        counter += BLOCK_BYTES
//                        consumeBlock(buffer)
//                        buffer = Array<UByte>(BLOCK_BYTES) {
//                            when (it) {
//                                in (0 until (chunk.size - (BLOCK_BYTES - bufferCounter))) -> {
//                                    chunk[it + (BLOCK_BYTES - bufferCounter)]
//                                }
//                                else -> {
//                                    0U
//                                }
//                            }
//
//                        }
//                        bufferCounter = chunk.size - (BLOCK_BYTES - bufferCounter)
//                    }
//                }
//
//            }
//            data.size < 16 -> {
//                val paddingSize = 16 - data.size
//                val padding = UByteArray(16 - data.size) { paddingSize.toUByte() }
//                output += processBlock(data + padding)
//            }
//            data.size == 16 -> {
//
//            }
//            data.size > 16 -> {
//
//            }
//        }
//
//        if (data.size < 16) {
//
//        }
//
//    }

    private fun appendToBuffer(array: Array<UByte>, start: Int) {
        array.copyInto(destination = buffer, destinationOffset = start, startIndex = 0, endIndex = array.size)
        bufferCounter += array.size
    }

    private fun processBlock(data : Array<UByte>) : Array<UByte> {
        if (currentOutput.isEmpty()) {
            currentOutput = Aes.encrypt(aesKey, data xor iv)
        } else {
            currentOutput = Aes.encrypt(aesKey, data xor currentOutput)
        }
        return currentOutput
    }

}