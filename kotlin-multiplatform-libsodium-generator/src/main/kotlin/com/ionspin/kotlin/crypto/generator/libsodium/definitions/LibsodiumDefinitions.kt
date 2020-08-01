package com.ionspin.kotlin.crypto.generator.libsodium.definitions

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 01-Aug-2020
 */
object LibSodiumDefinitions {
    val testKotlinFile = fileDef("DebugTest") {
        +classDef("Crypto") {

            /*
            --------------- SHA256
             */
            +innerClassDef(
                "Sha256State",
                "com.goterl.lazycode.lazysodium.interfaces.Hash.State256",
                "Sha256State",
                "crypto_hash_sha256_state"
            )
            +funcDef(
                "crypto_hash_sha256_init",
                TypeDefinition.INT
            ) {
                +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha256State"))))
            }

            +funcDef("crypto_hash_sha256_update", TypeDefinition.UNIT) {
                +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha256State"))))
                +ParameterDefinition("input", TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE)
            }

            +funcDef("crypto_hash_sha256_final", TypeDefinition.UNIT) {
                +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha256State"))))
                +ParameterDefinition("out", TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE)
            }

            /*
           --------------- SHA512
            */
            +innerClassDef(
                "Sha512State",
                "com.goterl.lazycode.lazysodium.interfaces.Hash.State512",
                "Sha512State",
                "crypto_hash_sha512_state"
            )
            +funcDef(
                "crypto_hash_sha512_init",
                TypeDefinition.INT
            ) {
                +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha512State"))))
            }

            +funcDef("crypto_hash_sha512_update", TypeDefinition.UNIT) {
                +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha512State"))))
                +ParameterDefinition("input", TypeDefinition.ARRAY_OF_UBYTES_LONG_SIZE)
            }

            +funcDef("crypto_hash_sha512_final", TypeDefinition.UNIT) {
                +ParameterDefinition("state", CustomTypeDefinition((withPackageName("Sha512State"))))
                +ParameterDefinition("out", TypeDefinition.ARRAY_OF_UBYTES_NO_SIZE)
            }

            /*
             * ------------- GENERIC HASH (BLAKE2B)
             */

            +innerClassDef(
                "GenericHashState",
                "ByteArray",
                "Uint8Array",
                "crypto_generichash_blake2b_state"
            )

            +funcDef(
                "crypto_generichash_init",
                TypeDefinition.INT
            ) {
                +ParameterDefinition("state", CustomTypeDefinition((withPackageName("GenericHashState"))))
                +ParameterDefinition("key", TypeDefinition.ARRAY_OF_UBYTES)
                +ParameterDefinition("outlen", TypeDefinition.INT, modifiesReturn = true)
            }


        }


    }
}
