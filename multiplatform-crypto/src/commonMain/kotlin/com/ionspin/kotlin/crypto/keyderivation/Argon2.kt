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

package com.ionspin.kotlin.crypto.keyderivation

import com.ionspin.kotlin.crypto.chunked
import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.plus

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 08-Jan-2020
 */
@ExperimentalUnsignedTypes
class Argon2 internal constructor(
    val password : Array<UByte>,
    val salt : Array<UByte>,
    val parallelism : UInt,
    val tagLength : UInt,
    val memorySize : UInt,
    val numberOfIterations : UInt,
    val versionNumber : UInt,
    val key : Array<UByte>,
    val type : ArgonType
){
    enum class ArgonType {
        Argon2i, Argon2d, Argon2id
    }
    companion object {


        fun hash(input : Array<UByte>, length : UInt) : Array<UByte> {
            if (length <= 64U) {
                return Blake2b.digest(length + input)
            }
            //We can cast to int because UInt even if MAX_VALUE divided by 32 is guaranteed not to overflow
            val numberOfBlocks = (1U + ((length - 1U) / 32U) - 1U).toInt() // equivalent  to ceil(length/32) - 1
            val v = Array<Array<UByte>>(numberOfBlocks) { emptyArray() }
            v[0] = Blake2b.digest(length + input)
            for (i in 1 until numberOfBlocks - 1) {
                v[i] = Blake2b.digest(v[i-1])
            }
            val remainingPartOfInput = input.copyOfRange(input.size - numberOfBlocks * 32, input.size)
            v[numberOfBlocks] = Blake2b.digest(remainingPartOfInput, hashLength = remainingPartOfInput.size)
            v.chunked(8)



            return emptyArray()
        }

    }







}