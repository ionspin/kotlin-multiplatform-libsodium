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
        val plaintext = ("pUoR4JVXJUeMKNkt6ZGGzEdTo33ajNGXwXpivBKA0XKs8toGRYI9Eul4bELRDkaQDNhd4vZseEFU" +
                "ojsAn3c9zIifIrMnydSivHVZ2pBtpAQwYoJhYmEsfE0tROGnOwFWyB9K6LRSv1gB3YqKR9VyM8mpRoUM3UCRRjyiX7bnKdCE1" +
                "EiX0myiwcY1nUKTgB3keERWtMU07hX7bCtao5nRvDofSj3o3IInHRQh6opltr5asQwn4m1qn029QF").encodeToUByteArray()
        plaintext.hexColumsPrint()
        val associatedData = "Additional data 1".encodeToUByteArray()
//        val associatedData = ubyteArrayOf()
        val keyValue = UByteArray(32) { it.toUByte() }
        val key = SymmetricKey(keyValue)
        val encryptor = Crypto.Encryption.createMultipartEncryptor(key)
        val header = encryptor.startEncryption()
        val ciphertext1 = encryptor.encryptPartialData(plaintext.sliceArray(0 until 100), associatedData)
        val ciphertext2 = encryptor.encryptPartialData(plaintext.sliceArray(100 until 200))
        val ciphertext3 = encryptor.encryptPartialData(plaintext.sliceArray(200 until 250))
        //decrypt
        val decryptor = Crypto.Encryption.createMultipartDecryptor(key, header)
        val plaintext1 = decryptor.decryptPartialData(ciphertext1, associatedData)
        val plaintext2 = decryptor.decryptPartialData(ciphertext2)
        val plaintext3 = decryptor.decryptPartialData(ciphertext3)

        val combinedPlaintext = plaintext1.data + plaintext2.data + plaintext3.data

        combinedPlaintext.hexColumsPrint()
        assertTrue {
            plaintext.contentEquals(combinedPlaintext)
        }



    }
}
