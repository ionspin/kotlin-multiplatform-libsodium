//We'll handle SRNG through libsodium
///*
// *    Copyright 2019 Ugljesa Jovanovic
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.ionspin.kotlin.crypto
//
//import kotlinx.cinterop.*
//import platform.windows.*
//
///**
// * Created by Ugljesa Jovanovic
// * ugljesa.jovanovic@ionspin.com
// * on 21-Sep-2019
// */
//actual object SRNG {
//    private val advapi by lazy { LoadLibraryA("ADVAPI32.DLL")}
//
//    private val advapiRandom by lazy {
//        GetProcAddress(advapi, "SystemFunction036")?.reinterpret<CFunction<Function2<CPointer<ByteVar>, ULong, Int>>>() ?: error("Failed getting advapi random")
//    }
//
//    @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
//    actual fun getRandomBytes(amount: Int): UByteArray {
//        memScoped {
//            val randArray = allocArray<ByteVar>(amount)
//            val pointer = randArray.getPointer(this)
//            val status = advapiRandom(pointer.reinterpret(), amount.convert())
//            return UByteArray(amount) { pointer[it].toUByte() }
//        }
//    }
//}