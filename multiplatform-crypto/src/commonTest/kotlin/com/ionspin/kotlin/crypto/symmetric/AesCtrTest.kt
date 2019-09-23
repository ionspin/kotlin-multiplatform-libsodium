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

import com.ionspin.kotlin.crypto.hexStringToUByteArray
import com.ionspin.kotlin.crypto.toHexString
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Sep-2019
 */
@ExperimentalUnsignedTypes
class AesCtrTest {

    @Test
    fun testCtrEncryption() {
        assertTrue {
            val key = "2b7e151628aed2a6abf7158809cf4f3c"
            val ic = "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff"
            val plaintext =
                "6bc1bee22e409f96e93d7e117393172aae2d8a571e03ac9c9eb76fac45af8e5130c81c46a35ce411e5fbc1191a0a52eff69f2445df4f9b17ad2b417be66c3710"
            val expectedCipherText =
                "874d6191b620e3261bef6864990db6ce9806f66b7970fdff8617187bb9fffdff5ae4df3edbd5d35e5b4f09020db03eab1e031dda2fbe03d1792170a0f3009cee"
            val aesCtr = AesCtr(AesKey.Aes128Key(key), mode = Mode.ENCRYPT, initialCounter = ic.hexStringToUByteArray())
            aesCtr.addData(
                plaintext.hexStringToUByteArray()
            )
            val encrypted = aesCtr.encrypt()
            println("Encrypted: ${encrypted.encryptedData.toHexString()}")

            expectedCipherText == encrypted.encryptedData.toHexString() &&
                    ic == encrypted.initialCounter.toHexString()
        }

        assertTrue {
            val keyString = "4278b840fb44aaa757c1bf04acbe1a3e"
            val key = AesKey.Aes128Key(keyString)
            val plainText = "6bc1bee22e409f96e93d7e117393172aae2d8a571e03ac9c9eb76fac45af8e5130c81c46a35ce411e5fbc1191a0a52eff69f2445df4f9b17ad2b417be66c3710"

            val encryptedDataAndInitializationVector = AesCtr.encrypt(key, plainText.hexStringToUByteArray())
            val decrypted = AesCtr.decrypt(
                key,
                encryptedDataAndInitializationVector.encryptedData,
                encryptedDataAndInitializationVector.initialCounter
            )
            plainText == decrypted.toHexString()
        }

    }

    @Test
    fun testCtrDecryption() {
        assertTrue {
            val key = "2b7e151628aed2a6abf7158809cf4f3c"
            val ic = "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff"
            val cipherText =
                "874d6191b620e3261bef6864990db6ce9806f66b7970fdff8617187bb9fffdff5ae4df3edbd5d35e5b4f09020db03eab1e031dda2fbe03d1792170a0f3009cee"
            val expectedPlainText =
                "6bc1bee22e409f96e93d7e117393172aae2d8a571e03ac9c9eb76fac45af8e5130c81c46a35ce411e5fbc1191a0a52eff69f2445df4f9b17ad2b417be66c3710"
            val aesCtr = AesCtr(AesKey.Aes128Key(key), mode = Mode.DECRYPT, initialCounter = ic.hexStringToUByteArray())
            aesCtr.addData(cipherText.hexStringToUByteArray())
            val decrypted = aesCtr.decrypt()
            println("Decrypted: ${decrypted.toHexString()}")
            expectedPlainText == decrypted.toHexString()
        }

        assertTrue {
            val key = "2b7e151628aed2a6abf7158809cf4f3c"
            val ic = "f0f1f2f3f4f5f6f7f8f9fafbfcfdfeff"
            val cipherText =
                "874d6191b620e3261bef6864990db6ce9806f66b7970fdff8617187bb9fffdff5ae4df3edbd5d35e5b4f09020db03eab1e031dda2fbe03d1792170a0f3009cee"
            val expectedPlainText =
                "6bc1bee22e409f96e93d7e117393172aae2d8a571e03ac9c9eb76fac45af8e5130c81c46a35ce411e5fbc1191a0a52eff69f2445df4f9b17ad2b417be66c3710"
            val decrypted = AesCtr.decrypt(AesKey.Aes128Key(key), cipherText.hexStringToUByteArray(), ic.hexStringToUByteArray())
            println("Decrypted: ${decrypted.toHexString()}")
            expectedPlainText == decrypted.toHexString()
        }

    }
}