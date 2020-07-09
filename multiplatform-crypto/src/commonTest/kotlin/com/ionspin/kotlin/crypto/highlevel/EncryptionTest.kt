package com.ionspin.kotlin.crypto.highlevel

import com.ionspin.kotlin.crypto.Crypto
import com.ionspin.kotlin.crypto.SymmetricKey
import com.ionspin.kotlin.crypto.hash.decodeToString
import com.ionspin.kotlin.crypto.hash.encodeToUByteArray
import com.ionspin.kotlin.crypto.util.hexColumsPrint
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 09-Jul-2020
 */
class EncryptionTest {
    @Test
    fun testMultipartEncryption() {
        val plaintext = ("Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                "Vestibulum maximus tincidunt urna. " +
                "Nullam sit amet erat id arcu porttitor varius ut at metus. " +
                "Nunc sit amet felis vel velit ornare gravida. " +
                "Curabitur tellus lacus, pulvinar a diam at tincidunt.").encodeToUByteArray()
        plaintext.hexColumsPrint()
        val additionalData = "Additional data 1".encodeToUByteArray()
//        val additionalData = ubyteArrayOf()
        val keyValue = UByteArray(32) { it.toUByte() }
        val key = SymmetricKey(keyValue)
        val encryptor = Crypto.Encryption.createMultipartEncryptor(key)
        val header = encryptor.startEncryption()
        val ciphertext1 = encryptor.encryptPartialData(plaintext.sliceArray(0 until 100), additionalData)
        val ciphertext2 = encryptor.encryptPartialData(plaintext.sliceArray(100 until 200))
        val ciphertext3 = encryptor.encryptPartialData(plaintext.sliceArray(200 until 250))
        //decrypt
        val decryptor = Crypto.Encryption.createMultipartDecryptor(key, header)
        println("Initialized")
        val plaintext1 = decryptor.decryptPartialData(ciphertext1, additionalData)
        val plaintext2 = decryptor.decryptPartialData(ciphertext2)
        val plaintext3 = decryptor.decryptPartialData(ciphertext3)

        val combinedPlaintext = plaintext1.data + plaintext2.data + plaintext3.data
        println("---- Plaintext -----")
        plaintext.hexColumsPrint()
        println("---- Plaintext result -----")
        combinedPlaintext.hexColumsPrint()
        assertTrue {
            plaintext.contentEquals(combinedPlaintext)
        }



    }
}
