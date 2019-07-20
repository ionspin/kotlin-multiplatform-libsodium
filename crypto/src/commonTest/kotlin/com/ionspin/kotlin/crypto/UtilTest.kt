/*
 * Copyright (c) 2019. Ugljesa Jovanovic
 */

package com.ionspin.crypto

import com.ionspin.crypto.blake2b.chunked
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 17-Jul-2019
 */
class UtilTest {

    @Test
    fun testSlicer() {
        val array = arrayOf(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17)
        val chunked = array.chunked(2)
        assertTrue {
            chunked.size == 9 && chunked[8][0] == 17
        }
    }
}