package com.ionspin.kotlin.crypto.jnitest

import org.junit.Test
import kotlin.test.assertEquals

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 20-Feb-2021
 */
class JniTestTest {

    @Test
    fun testJniTest() {
        val result = JniTest.work()
        assertEquals("1.0.18", result)
    }
}
