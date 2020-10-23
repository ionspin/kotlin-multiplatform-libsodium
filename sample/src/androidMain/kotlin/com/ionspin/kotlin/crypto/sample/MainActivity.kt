/*
 * Copyright (c) 2020 Ugljesa Jovanovic (ugljesa.jovanovic@ionspin.com)
 */

package com.ionspin.kotlin.crypto.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val hash = Hash.sha512("123".encodeToUByteArray())
        helloWorldTextView.setText("Hash (SHA512) of 123: ${hash.toHexString()}")


    }

}
