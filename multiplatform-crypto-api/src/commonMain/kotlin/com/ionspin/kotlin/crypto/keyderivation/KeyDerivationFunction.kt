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

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-May-2020
 */
interface KeyDerivationFunction {
    fun derive() : UByteArray
}

data class ArgonResult(
    val hashBytes: UByteArray,
    val salt: UByteArray
) {
    val hashString by lazy { hashBytes.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "") }
    val saltString by lazy { salt.map { it.toString(16).padStart(2, '0') }.joinToString(separator = "") }

}
