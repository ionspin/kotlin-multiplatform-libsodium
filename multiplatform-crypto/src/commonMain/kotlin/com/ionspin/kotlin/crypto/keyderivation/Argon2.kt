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

import com.ionspin.kotlin.crypto.hash.blake2b.Blake2b
import com.ionspin.kotlin.crypto.plus
import com.ionspin.kotlin.crypto.toLittleEndianUByteArray

/**
 * https://tools.ietf.org/html/draft-irtf-cfrg-argon2-03
 * https://en.wikipedia.org/wiki/Argon2
 * https://www.cryptolux.org/images/0/0d/Argon2.pdf
 *
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 08-Jan-2020
 *
 *
 *
 */
@ExperimentalUnsignedTypes
class Argon2 internal constructor(
    val password: Array<UByte>,
    val salt: Array<UByte>,
    val parallelism: UInt,
    val tagLength: UInt,
    val memorySize: UInt,
    val numberOfIterations: UInt,
    val versionNumber: UInt,
    val key: Array<UByte>,
    associatedData: Array<UByte>
    val type: ArgonType
) {
    enum class ArgonType {
        Argon2i, Argon2d, Argon2id
    }

    companion object {


        fun argonHash(input: Array<UByte>, length: UInt): Array<UByte> {
            if (length <= 64U) {
                return Blake2b.digest(length + input)
            }
            //We can cast to int because UInt even if MAX_VALUE divided by 32 is guaranteed not to overflow
            val numberOfBlocks = (1U + ((length - 1U) / 32U) - 1U).toInt() // equivalent  to ceil(length/32) - 1
            val v = Array<Array<UByte>>(numberOfBlocks) { emptyArray() }
            v[0] = Blake2b.digest(length + input)
            for (i in 1 until numberOfBlocks - 1) {
                v[i] = Blake2b.digest(v[i - 1])
            }
            val remainingPartOfInput = input.copyOfRange(input.size - numberOfBlocks * 32, input.size)
            val vLast = Blake2b.digest(remainingPartOfInput, hashLength = remainingPartOfInput.size)
            val concat =
                (v.map { it.copyOfRange(0, 32) })
                    .plus(listOf(vLast))
                    .foldRight(emptyArray<UByte>()) { arrayOfUBytes, acc -> arrayOfUBytes + acc }



            return concat
        }

        fun compressionFunctionG(x : Array<UByte>, y : Array<UByte>) : Array<Array<Array<UByte>>> {
            val r = Array<UByte>(1024) { 0U }
            x.forEachIndexed { index, it -> r[index] = it xor y[index] }
            // mix rounds
            return emptyArray() // TODO
        }

        // --------- Unmodified blake2b mixing
        /*
        internal fun mixRound(input: Array<ULong>, message: Array<ULong>, round: Int): Array<ULong> {
            var v = input
            val selectedSigma = sigma[round % 10]
            v = mix(v, 0, 4, 8, 12, message[selectedSigma[0]], message[selectedSigma[1]])
            v = mix(v, 1, 5, 9, 13, message[selectedSigma[2]], message[selectedSigma[3]])
            v = mix(v, 2, 6, 10, 14, message[selectedSigma[4]], message[selectedSigma[5]])
            v = mix(v, 3, 7, 11, 15, message[selectedSigma[6]], message[selectedSigma[7]])
            v = mix(v, 0, 5, 10, 15, message[selectedSigma[8]], message[selectedSigma[9]])
            v = mix(v, 1, 6, 11, 12, message[selectedSigma[10]], message[selectedSigma[11]])
            v = mix(v, 2, 7, 8, 13, message[selectedSigma[12]], message[selectedSigma[13]])
            v = mix(v, 3, 4, 9, 14, message[selectedSigma[14]], message[selectedSigma[15]])
            return v

        }

        private fun mix(v: Array<ULong>, a: Int, b: Int, c: Int, d: Int, x: ULong, y: ULong): Array<ULong> {
            v[a] = (v[a] + v[b] + x)
            v[d] = (v[d] xor v[a]) rotateRight R1
            v[c] = (v[c] + v[d])
            v[b] = (v[b] xor v[c]) rotateRight R2
            v[a] = (v[a] + v[b] + y)
            v[d] = (v[d] xor v[a]) rotateRight R3
            v[c] = (v[c] + v[d])
            v[b] = (v[b] xor v[c]) rotateRight R4
            return v
        }
         */

        internal fun derive(
            password: Array<UByte>,
            salt: Array<UByte>,
            parallelism: UInt,
            tagLength: UInt,
            memorySize: UInt,
            numberOfIterations: UInt,
            versionNumber: UInt,
            key: Array<UByte>,
            associatedData: Array<UByte>,
            type: ArgonType
        ): Array<UByte> {
            val h0 = Blake2b.digest(
                parallelism.toLittleEndianUByteArray() + tagLength.toLittleEndianUByteArray() + memorySize.toLittleEndianUByteArray() +
                        numberOfIterations.toLittleEndianUByteArray() + versionNumber.toLittleEndianUByteArray() +
                        password.size.toUInt().toLittleEndianUByteArray() + password +
                        salt.size.toUInt().toLittleEndianUByteArray() + salt +
                        key.size.toUInt().toLittleEndianUByteArray() + key +
                        associatedData.size.toUInt().toLittleEndianUByteArray() + associatedData
            )

            val blockCount = (memorySize / (4U * parallelism)) * (4U * parallelism) //
            val columnCount = blockCount / parallelism

            //Allocate memory as Array of parallelism rows and columnCount colums
            val matrix = Array(parallelism.toInt()) {
                Array(columnCount.toInt()) {
                    Array<UByte>(1024) { 0U }
                }
            }

            //Compute B[i][0]
            for (i in 0..parallelism.toInt()) {
                matrix[i][0] =
                    argonHash(h0 + 0.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(), 64U)
            }

            //Compute B[i][1]
            for (i in 0..parallelism.toInt()) {
                matrix[i][0] =
                    argonHash(h0 + 1.toUInt().toLittleEndianUByteArray() + i.toUInt().toLittleEndianUByteArray(), 64U)
            }



            return emptyArray()
        }

    }


}