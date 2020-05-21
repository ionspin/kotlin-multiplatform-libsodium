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

package com.ionspin.kotlin.crypto.keyderivation.argon2

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-May-2020
 */
 class Argon2Matrix(val columnCount: Int, val rowCount: Int) {

    internal val storage: UByteArray = UByteArray(columnCount * rowCount * 1024)

    operator fun get(rowPosition: Int, columnPosition: Int, inBlockPosition: Int) : UByte {
        return storage[rowPosition * (columnCount - 1) * 1024 + columnPosition * 1024 + inBlockPosition]
    }

    operator fun get(rowPosition: Int, columnPosition: Int) : UByteArray {
        println("Expensive.")
        return storage.copyOfRange(
            rowPosition * (columnCount - 1) * 1024 + columnPosition * 1024,
            rowPosition * (columnCount - 1) * 1024 + columnPosition * 1024 + 1024
        )
    }

    operator fun get(rowPosition: Int) : Array<UByteArray> {
        return Array(columnCount) {
            this.get(rowPosition, it)
        }

    }
}