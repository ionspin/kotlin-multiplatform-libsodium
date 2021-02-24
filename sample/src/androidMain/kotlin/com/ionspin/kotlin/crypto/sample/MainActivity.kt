/*
 * Copyright (c) 2020 Ugljesa Jovanovic (ugljesa.jovanovic@ionspin.com)
 */

package com.ionspin.kotlin.crypto.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ionspin.kotlin.crypto.LibsodiumInitializer.sodiumJna
import com.ionspin.kotlin.crypto.TmpAccessor
import com.ionspin.kotlin.crypto.box.Box
import com.ionspin.kotlin.crypto.box.BoxCorruptedOrTamperedDataException
import com.ionspin.kotlin.crypto.box.crypto_box_NONCEBYTES
import com.ionspin.kotlin.crypto.hash.Hash
import com.ionspin.kotlin.crypto.util.decodeFromUByteArray
import com.ionspin.kotlin.crypto.util.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.toHexString
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder
import kotlin.random.Random
import kotlin.random.nextUBytes

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val hash = Hash.sha512("123".encodeToUByteArray())

        val message = "Message message message".encodeToUByteArray()
        val senderKeypair = Box.keypair()
        val recipientKeypair = Box.keypair()
        val messageNonce = Random(0).nextUBytes(crypto_box_NONCEBYTES)
        val builder = StringBuilder()
        val encrypted = Box.easy(message, messageNonce, recipientKeypair.publicKey, senderKeypair.secretKey)
        builder.appendLine("Encrypted: ${encrypted.toHexString()}")
        val decrypted = Box.openEasy(encrypted, messageNonce, senderKeypair.publicKey, recipientKeypair.secretKey)

        builder.appendLine("Decrypted: ${decrypted.decodeFromUByteArray()}")
        try {
            val tampered = encrypted.copyOf()
            tampered[1] = 0U
            Box.openEasy(tampered, messageNonce, senderKeypair.publicKey, recipientKeypair.secretKey)
        } catch (exception : Exception) {
            builder.appendLine("And caught tamper")
        }
        helloWorldTextView.setText(builder.toString())
    }

}
