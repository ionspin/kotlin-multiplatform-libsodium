package com.ionspin.kotlin.crypto.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise

@JsName("Promise")
actual external class TestResult

actual fun runTest(block: suspend (scope : CoroutineScope) -> Unit): TestResult =
    GlobalScope.promise { block(this) }.unsafeCast<TestResult>()
