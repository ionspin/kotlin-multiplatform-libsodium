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

package com.ionspin.kotlin.crypto.hash


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Jul-2019
 */
interface Hash {
    val MAX_HASH_BYTES : Int
}

@ExperimentalUnsignedTypes
interface UpdateableHash : Hash {
    fun update(data : Array<UByte>)

    fun update(data : String)

    fun digest() : Array<UByte>

    fun digestString() : String
}

@ExperimentalUnsignedTypes
interface StatelessHash : Hash {
    fun digest(inputString: String, key: String? = null, hashLength: Int = MAX_HASH_BYTES): Array<UByte>

    fun digest(
        inputMessage: Array<UByte> = emptyArray(),
        key: Array<UByte> = emptyArray(),
        hashLength: Int = MAX_HASH_BYTES
    ): Array<UByte>
}

