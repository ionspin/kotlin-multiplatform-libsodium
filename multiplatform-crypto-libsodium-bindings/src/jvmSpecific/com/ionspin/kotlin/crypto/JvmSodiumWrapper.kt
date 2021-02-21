package com.ionspin.kotlin.crypto

import com.goterl.lazycode.lazysodium.LazySodiumJava
import com.goterl.lazycode.lazysodium.SodiumJava
import com.ionspin.kotlin.crypto.jnitest.JniTest
import com.ionspin.kotlin.crypto.jnitest.SodiumVersion

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 22-Aug-2020
 */
typealias SodiumWrapper = SodiumJava
typealias JniTestWrapper = SodiumVersion
