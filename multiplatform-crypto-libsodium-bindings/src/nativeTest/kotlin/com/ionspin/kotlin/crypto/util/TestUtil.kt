package com.ionspin.kotlin.crypto.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

actual typealias TestResult = Unit

actual fun runTest(block: suspend (scope : CoroutineScope) -> Unit): TestResult = runBlocking { block(this) }
