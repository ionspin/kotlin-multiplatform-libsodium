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

package com.ionspin.kotlin.crypto

import kotlin.browser.window

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 21-Sep-2019
 */
actual object SRNG {
    var counter = 0
    @ExperimentalUnsignedTypes
    actual fun getRandomBytes(amount: Int): UByteArray {
        val runningOnNode = jsTypeOf(window) == "undefined"
        val randomBytes = if (runningOnNode) {
            println("Running on node")
            js("require('crypto')").randomBytes(amount).toJSON().data
        } else {
            println("Running in browser")
            js(
            """
                var randomArray = new Uint8Array(amount);
                var crypto = (self.crypto || self.msCrypto);        
                crypto.getRandomValues(randomArray);
            """
            )
            var randomArrayResult = js("Array.prototype.slice.call(randomArray);")
            randomArrayResult
        }
        println("Random bytes: $randomBytes")
        print("Byte at ${randomBytes[0]}")
        val randomBytesUByteArray = UByteArray(amount) {
            0U
        }
        for (i in 0 until amount) {
            js("""
               randomBytesUByteArray[i] = randomBytes[i]  
            """)
        }
        return randomBytesUByteArray
    }


}