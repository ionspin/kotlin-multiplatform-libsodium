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

package com.ionspin.kotlin.crypto.keyderivation.argon2

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 16-May-2020
 */
class Argon2TagTooShort(tagLength: UInt) : RuntimeException("Too short tag (output) requested. Requested: $tagLength")
class Argon2TagTooLong(tagLength: UInt) : RuntimeException("Too long tag (output) requested. Requested: $tagLength")
class Argon2TimeTooShort(iterations: UInt) : RuntimeException("Too short time parameter (Too few iterations). Requested iterations: $iterations")
class Argon2TimeTooLong(iterations: UInt) : RuntimeException("Too long time parameter (Too many iterations). Requested iterations: $iterations")
class Argon2MemoryTooLitlle(requestedMemorySize: UInt) : RuntimeException("Requested memory size must be larger than 8 * parallelism. Requested size: $requestedMemorySize")
class Argon2MemoryTooMuch(requestedMemorySize: UInt) : RuntimeException("Requested memory size too large. Requested size: $requestedMemorySize")
class Argon2LanesTooFew(parallelism: Int) : RuntimeException("Too few, or invalid number of threads requested $parallelism")
class Argon2LanesTooMany(parallelism: Int) : RuntimeException("Too many threads requested (parallelism). Requested: $parallelism")