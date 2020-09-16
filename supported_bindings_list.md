|Function name| Implemented |
|-------------|-------------|
| add | |
| memcmp | | 
| memzero | |    
| output_formats | | 
| pad | |    
| unpad | |  
| symbols | |    
| to_base64 | |  
| to_hex | | 
| to_string | |  
| crypto_aead_chacha20poly1305_decrypt | :heavy_check_mark: |   
| crypto_aead_chacha20poly1305_decrypt_detached | :heavy_check_mark: |  
| crypto_aead_chacha20poly1305_encrypt | :heavy_check_mark: |   
| crypto_aead_chacha20poly1305_encrypt_detached | :heavy_check_mark: |  
| crypto_aead_chacha20poly1305_ietf_decrypt | :heavy_check_mark: |  
| crypto_aead_chacha20poly1305_ietf_decrypt_detached | :heavy_check_mark: | 
| crypto_aead_chacha20poly1305_ietf_encrypt | :heavy_check_mark: |  
| crypto_aead_chacha20poly1305_ietf_encrypt_detached | :heavy_check_mark: | 
| crypto_aead_chacha20poly1305_ietf_keygen | :heavy_check_mark: |   
| crypto_aead_chacha20poly1305_keygen | :heavy_check_mark: |    
| crypto_aead_xchacha20poly1305_ietf_decrypt | :heavy_check_mark: | 
| crypto_aead_xchacha20poly1305_ietf_decrypt_detached | :heavy_check_mark: |    
| crypto_aead_xchacha20poly1305_ietf_encrypt | :heavy_check_mark: | 
| crypto_aead_xchacha20poly1305_ietf_encrypt_detached | :heavy_check_mark: |    
| crypto_aead_xchacha20poly1305_ietf_keygen | :heavy_check_mark: |  
| crypto_auth | :heavy_check_mark: |    
| crypto_auth_hmacsha256 | :heavy_check_mark: | 
| crypto_auth_hmacsha256_keygen | :heavy_check_mark: |  
| crypto_auth_hmacsha256_verify | :heavy_check_mark: |  
| crypto_auth_hmacsha512 | :heavy_check_mark: | 
| crypto_auth_hmacsha512_keygen | :heavy_check_mark: |  
| crypto_auth_hmacsha512_verify | :heavy_check_mark: |  
| crypto_auth_keygen | :heavy_check_mark: | 
| crypto_auth_verify | :heavy_check_mark: | 
| crypto_box_beforenm | :heavy_check_mark: |    
| crypto_box_curve25519xchacha20poly1305_keypair | not present in LazySodium | 
| crypto_box_curve25519xchacha20poly1305_seal | not present in LazySodium |    
| crypto_box_curve25519xchacha20poly1305_seal_open |not present in LazySodium  |   
| crypto_box_detached | :heavy_check_mark: |    
| crypto_box_easy | :heavy_check_mark: |    
| crypto_box_easy_afternm | :heavy_check_mark: |    
| crypto_box_keypair | :heavy_check_mark: | 
| crypto_box_open_detached | :heavy_check_mark: |   
| crypto_box_open_easy | :heavy_check_mark: |   
| crypto_box_open_easy_afternm | :heavy_check_mark: |   
| crypto_box_seal | :heavy_check_mark: |    
| crypto_box_seal_open | :heavy_check_mark: |   
| crypto_box_seed_keypair | :heavy_check_mark: |    
| crypto_core_ristretto255_add | not present in LazySodium |   
| crypto_core_ristretto255_from_hash | not present in LazySodium | 
| crypto_core_ristretto255_is_valid_point | not present in LazySodium |    
| crypto_core_ristretto255_random | not present in LazySodium |    
| crypto_core_ristretto255_scalar_add | not present in LazySodium |    
| crypto_core_ristretto255_scalar_complement | not present in LazySodium | 
| crypto_core_ristretto255_scalar_invert | not present in LazySodium | 
| crypto_core_ristretto255_scalar_mul | not present in LazySodium |    
| crypto_core_ristretto255_scalar_negate | not present in LazySodium | 
| crypto_core_ristretto255_scalar_random | not present in LazySodium | 
| crypto_core_ristretto255_scalar_reduce | not present in LazySodium | 
| crypto_core_ristretto255_scalar_sub | not present in LazySodium |    
| crypto_core_ristretto255_sub | not present in LazySodium |   
| crypto_generichash | :heavy_check_mark:  |                                       
| crypto_generichash_blake2b_salt_personal | |   
| crypto_generichash_final | :heavy_check_mark:  |                                   
| crypto_generichash_init | :heavy_check_mark:  |                                     
| crypto_generichash_keygen | :heavy_check_mark: |  
| crypto_generichash_update | :heavy_check_mark: |                                 
| crypto_hash | |    
| crypto_hash_sha256 | :heavy_check_mark: | 
| crypto_hash_sha256_final | :heavy_check_mark: |   
| crypto_hash_sha256_init | :heavy_check_mark: |    
| crypto_hash_sha256_update | :heavy_check_mark: |  
| crypto_hash_sha512 | :heavy_check_mark: | 
| crypto_hash_sha512_final | :heavy_check_mark: |   
| crypto_hash_sha512_init | :heavy_check_mark: |    
| crypto_hash_sha512_update | :heavy_check_mark: |  
| crypto_kdf_derive_from_key | :heavy_check_mark: | 
| crypto_kdf_keygen | :heavy_check_mark: |  
| crypto_kx_client_session_keys | |  
| crypto_kx_keypair | |  
| crypto_kx_seed_keypair | | 
| crypto_kx_server_session_keys | |  
| crypto_onetimeauth | not present in LazySodium | 
| crypto_onetimeauth_final | not present in LazySodium |   
| crypto_onetimeauth_init | not present in LazySodium |    
| crypto_onetimeauth_keygen | not present in LazySodium |  
| crypto_onetimeauth_update | not present in LazySodium |  
| crypto_onetimeauth_verify | not present in LazySodium |  
| crypto_pwhash | |  
| crypto_pwhash_scryptsalsa208sha256 | not present in LazySodium for Android | 
| crypto_pwhash_scryptsalsa208sha256_ll | not present in LazySodium for Android  |  
| crypto_pwhash_scryptsalsa208sha256_str | not present in LazySodium for Android  | 
| crypto_pwhash_scryptsalsa208sha256_str_verify | not present in LazySodium for Android  |  
| crypto_pwhash_str | |  
| crypto_pwhash_str_needs_rehash | | 
| crypto_pwhash_str_verify | |   
| crypto_scalarmult | |  
| crypto_scalarmult_base | | 
| crypto_scalarmult_ristretto255 | | 
| crypto_scalarmult_ristretto255_base | |    
| crypto_secretbox_detached | :heavy_check_mark: |  
| crypto_secretbox_easy | :heavy_check_mark: |  
| crypto_secretbox_keygen | :heavy_check_mark: |    
| crypto_secretbox_open_detached | :heavy_check_mark: | 
| crypto_secretbox_open_easy | :heavy_check_mark: | 
| crypto_secretstream_xchacha20poly1305_init_pull | :heavy_check_mark: |             
| crypto_secretstream_xchacha20poly1305_init_push | :heavy_check_mark: |             
| crypto_secretstream_xchacha20poly1305_keygen | :heavy_check_mark: |               
| crypto_secretstream_xchacha20poly1305_pull | :heavy_check_mark: |               
| crypto_secretstream_xchacha20poly1305_push | :heavy_check_mark: |               
| crypto_secretstream_xchacha20poly1305_rekey | :heavy_check_mark: |                 
| crypto_shorthash |:heavy_check_mark:  |                                           
| crypto_shorthash_keygen | :heavy_check_mark: |                                     
| crypto_shorthash_siphashx24 | |    
| crypto_sign | :heavy_check_mark: |    
| crypto_sign_detached | :heavy_check_mark: |   
| crypto_sign_ed25519_pk_to_curve25519 | :heavy_check_mark: |   
| crypto_sign_ed25519_sk_to_curve25519 | :heavy_check_mark: |   
| crypto_sign_ed25519_sk_to_pk | :heavy_check_mark: |   
| crypto_sign_ed25519_sk_to_seed | :heavy_check_mark: | 
| crypto_sign_final_create | :heavy_check_mark: |   
| crypto_sign_final_verify | :heavy_check_mark: |   
| crypto_sign_init | :heavy_check_mark: |   
| crypto_sign_keypair | :heavy_check_mark: |    
| crypto_sign_open | :heavy_check_mark: |   
| crypto_sign_seed_keypair | :heavy_check_mark: |   
| crypto_sign_update | :heavy_check_mark: | 
| crypto_sign_verify_detached | :heavy_check_mark: |    
| crypto_stream_chacha20 | | 
| crypto_stream_chacha20_ietf_xor | |    
| crypto_stream_chacha20_ietf_xor_ic | | 
| crypto_stream_chacha20_keygen | |  
| crypto_stream_chacha20_xor | | 
| crypto_stream_chacha20_xor_ic | |  
| crypto_stream_keygen | |   
| crypto_stream_xchacha20_keygen | not present in LazySodium Android | 
| crypto_stream_xchacha20_xor | not present in LazySodium Android|    
| crypto_stream_xchacha20_xor_ic | not present in LazySodium Android | 
| randombytes_buf | |    
| randombytes_buf_deterministic | |  
| randombytes_close | |  
| randombytes_random | | 
| randombytes_stir | |   
| randombytes_uniform | |    
| sodium_version_string | |  
| SODIUM_LIBRARY_VERSION_MAJOR | |   
| SODIUM_LIBRARY_VERSION_MINOR | |   
| crypto_aead_chacha20poly1305_ABYTES | |    
| crypto_aead_chacha20poly1305_IETF_ABYTES | :heavy_check_mark:  |   
| crypto_aead_chacha20poly1305_IETF_KEYBYTES | :heavy_check_mark:  | 
| crypto_aead_chacha20poly1305_IETF_MESSAGEBYTES_MAX | | 
| crypto_aead_chacha20poly1305_IETF_NPUBBYTES | :heavy_check_mark:  |    
| crypto_aead_chacha20poly1305_IETF_NSECBYTES | |    
| crypto_aead_chacha20poly1305_KEYBYTES | |  
| crypto_aead_chacha20poly1305_MESSAGEBYTES_MAX | |  
| crypto_aead_chacha20poly1305_NPUBBYTES | | 
| crypto_aead_chacha20poly1305_NSECBYTES | | 
| crypto_aead_chacha20poly1305_ietf_ABYTES | :heavy_check_mark:  |   
| crypto_aead_chacha20poly1305_ietf_KEYBYTES | :heavy_check_mark:  | 
| crypto_aead_chacha20poly1305_ietf_MESSAGEBYTES_MAX | | 
| crypto_aead_chacha20poly1305_ietf_NPUBBYTES | :heavy_check_mark:  |    
| crypto_aead_chacha20poly1305_ietf_NSECBYTES | |    
| crypto_aead_xchacha20poly1305_IETF_ABYTES | |  
| crypto_aead_xchacha20poly1305_IETF_KEYBYTES | |    
| crypto_aead_xchacha20poly1305_IETF_MESSAGEBYTES_MAX | |    
| crypto_aead_xchacha20poly1305_IETF_NPUBBYTES | |   
| crypto_aead_xchacha20poly1305_IETF_NSECBYTES | |   
| crypto_aead_xchacha20poly1305_ietf_ABYTES | :heavy_check_mark:  |  
| crypto_aead_xchacha20poly1305_ietf_KEYBYTES | :heavy_check_mark:  |    
| crypto_aead_xchacha20poly1305_ietf_MESSAGEBYTES_MAX | |    
| crypto_aead_xchacha20poly1305_ietf_NPUBBYTES | :heavy_check_mark:  |   
| crypto_aead_xchacha20poly1305_ietf_NSECBYTES | |   
| crypto_auth_BYTES | :heavy_check_mark: |  
| crypto_auth_KEYBYTES | :heavy_check_mark: |   
| crypto_auth_hmacsha256_BYTES | :heavy_check_mark: |   
| crypto_auth_hmacsha256_KEYBYTES | :heavy_check_mark: |    
| crypto_auth_hmacsha512256_BYTES | :heavy_check_mark: |    
| crypto_auth_hmacsha512256_KEYBYTES | :heavy_check_mark: | 
| crypto_auth_hmacsha512_BYTES | :heavy_check_mark: |   
| crypto_auth_hmacsha512_KEYBYTES | :heavy_check_mark: |    
| crypto_box_BEFORENMBYTES | |   
| crypto_box_MACBYTES | |    
| crypto_box_MESSAGEBYTES_MAX | |    
| crypto_box_NONCEBYTES | |  
| crypto_box_PUBLICKEYBYTES | |  
| crypto_box_SEALBYTES | |   
| crypto_box_SECRETKEYBYTES | |  
| crypto_box_SEEDBYTES | |   
| crypto_box_curve25519xchacha20poly1305_BEFORENMBYTES | |   
| crypto_box_curve25519xchacha20poly1305_MACBYTES | |    
| crypto_box_curve25519xchacha20poly1305_MESSAGEBYTES_MAX | |    
| crypto_box_curve25519xchacha20poly1305_NONCEBYTES | |  
| crypto_box_curve25519xchacha20poly1305_PUBLICKEYBYTES | |  
| crypto_box_curve25519xchacha20poly1305_SEALBYTES | |   
| crypto_box_curve25519xchacha20poly1305_SECRETKEYBYTES | |  
| crypto_box_curve25519xchacha20poly1305_SEEDBYTES | |   
| crypto_box_curve25519xsalsa20poly1305_BEFORENMBYTES | |    
| crypto_box_curve25519xsalsa20poly1305_MACBYTES | | 
| crypto_box_curve25519xsalsa20poly1305_MESSAGEBYTES_MAX | | 
| crypto_box_curve25519xsalsa20poly1305_NONCEBYTES | |   
| crypto_box_curve25519xsalsa20poly1305_PUBLICKEYBYTES | |   
| crypto_box_curve25519xsalsa20poly1305_SECRETKEYBYTES | |   
| crypto_box_curve25519xsalsa20poly1305_SEEDBYTES | |    
| crypto_core_ed25519_BYTES | |  
| crypto_core_ed25519_HASHBYTES | |  
| crypto_core_ed25519_NONREDUCEDSCALARBYTES | |  
| crypto_core_ed25519_SCALARBYTES | |    
| crypto_core_ed25519_UNIFORMBYTES | |   
| crypto_core_hchacha20_CONSTBYTES | |   
| crypto_core_hchacha20_INPUTBYTES | |   
| crypto_core_hchacha20_KEYBYTES | | 
| crypto_core_hchacha20_OUTPUTBYTES | |  
| crypto_core_hsalsa20_CONSTBYTES | |    
| crypto_core_hsalsa20_INPUTBYTES | |    
| crypto_core_hsalsa20_KEYBYTES | |  
| crypto_core_hsalsa20_OUTPUTBYTES | |   
| crypto_core_ristretto255_BYTES | | 
| crypto_core_ristretto255_HASHBYTES | | 
| crypto_core_ristretto255_NONREDUCEDSCALARBYTES | | 
| crypto_core_ristretto255_SCALARBYTES | |   
| crypto_core_salsa2012_CONSTBYTES | |   
| crypto_core_salsa2012_INPUTBYTES | |   
| crypto_core_salsa2012_KEYBYTES | | 
| crypto_core_salsa2012_OUTPUTBYTES | |  
| crypto_core_salsa20_CONSTBYTES | | 
| crypto_core_salsa20_INPUTBYTES | | 
| crypto_core_salsa20_KEYBYTES | |   
| crypto_core_salsa20_OUTPUTBYTES | |    
| crypto_generichash_BYTES | |   
| crypto_generichash_BYTES_MAX | |   
| crypto_generichash_BYTES_MIN | |   
| crypto_generichash_KEYBYTES | |    
| crypto_generichash_KEYBYTES_MAX | |    
| crypto_generichash_KEYBYTES_MIN | |    
| crypto_generichash_blake2b_BYTES | |   
| crypto_generichash_blake2b_BYTES_MAX | |   
| crypto_generichash_blake2b_BYTES_MIN | |   
| crypto_generichash_blake2b_KEYBYTES | |    
| crypto_generichash_blake2b_KEYBYTES_MAX | |    
| crypto_generichash_blake2b_KEYBYTES_MIN | |    
| crypto_generichash_blake2b_PERSONALBYTES | |   
| crypto_generichash_blake2b_SALTBYTES | |   
| crypto_hash_BYTES | |  
| crypto_hash_sha256_BYTES | :heavy_check_mark: |   
| crypto_hash_sha512_BYTES | :heavy_check_mark: |   
| crypto_kdf_BYTES_MAX | |   
| crypto_kdf_BYTES_MIN | |   
| crypto_kdf_CONTEXTBYTES | |    
| crypto_kdf_KEYBYTES | |    
| crypto_kdf_blake2b_BYTES_MAX | |   
| crypto_kdf_blake2b_BYTES_MIN | |   
| crypto_kdf_blake2b_CONTEXTBYTES | |    
| crypto_kdf_blake2b_KEYBYTES | |    
| crypto_kx_PUBLICKEYBYTES | |   
| crypto_kx_SECRETKEYBYTES | |   
| crypto_kx_SEEDBYTES | |    
| crypto_kx_SESSIONKEYBYTES | |  
| crypto_onetimeauth_BYTES | |   
| crypto_onetimeauth_KEYBYTES | |    
| crypto_onetimeauth_poly1305_BYTES | |  
| crypto_onetimeauth_poly1305_KEYBYTES | |   
| crypto_pwhash_ALG_ARGON2I13 | |    
| crypto_pwhash_ALG_ARGON2ID13 | |   
| crypto_pwhash_ALG_DEFAULT | |  
| crypto_pwhash_BYTES_MAX | |    
| crypto_pwhash_BYTES_MIN | |    
| crypto_pwhash_MEMLIMIT_INTERACTIVE | | 
| crypto_pwhash_MEMLIMIT_MAX | | 
| crypto_pwhash_MEMLIMIT_MIN | | 
| crypto_pwhash_MEMLIMIT_MODERATE | |    
| crypto_pwhash_MEMLIMIT_SENSITIVE | |   
| crypto_pwhash_OPSLIMIT_INTERACTIVE | | 
| crypto_pwhash_OPSLIMIT_MAX | | 
| crypto_pwhash_OPSLIMIT_MIN | | 
| crypto_pwhash_OPSLIMIT_MODERATE | |    
| crypto_pwhash_OPSLIMIT_SENSITIVE | |   
| crypto_pwhash_PASSWD_MAX | |   
| crypto_pwhash_PASSWD_MIN | |   
| crypto_pwhash_SALTBYTES | |    
| crypto_pwhash_STRBYTES | | 
| crypto_pwhash_argon2i_BYTES_MAX | |    
| crypto_pwhash_argon2i_BYTES_MIN | |    
| crypto_pwhash_argon2i_SALTBYTES | |    
| crypto_pwhash_argon2i_STRBYTES | | 
| crypto_pwhash_argon2id_BYTES_MAX | |   
| crypto_pwhash_argon2id_BYTES_MIN | |   
| crypto_pwhash_argon2id_SALTBYTES | |   
| crypto_pwhash_argon2id_STRBYTES | |    
| crypto_pwhash_scryptsalsa208sha256_BYTES_MAX | |   
| crypto_pwhash_scryptsalsa208sha256_BYTES_MIN | |   
| crypto_pwhash_scryptsalsa208sha256_MEMLIMIT_INTERACTIVE | |    
| crypto_pwhash_scryptsalsa208sha256_MEMLIMIT_MAX | |    
| crypto_pwhash_scryptsalsa208sha256_MEMLIMIT_MIN | |    
| crypto_pwhash_scryptsalsa208sha256_MEMLIMIT_SENSITIVE | |  
| crypto_pwhash_scryptsalsa208sha256_OPSLIMIT_INTERACTIVE | |    
| crypto_pwhash_scryptsalsa208sha256_OPSLIMIT_MAX | |    
| crypto_pwhash_scryptsalsa208sha256_OPSLIMIT_MIN | |    
| crypto_pwhash_scryptsalsa208sha256_OPSLIMIT_SENSITIVE | |  
| crypto_pwhash_scryptsalsa208sha256_SALTBYTES | |   
| crypto_pwhash_scryptsalsa208sha256_STRBYTES | |    
| crypto_scalarmult_BYTES | |    
| crypto_scalarmult_SCALARBYTES | |  
| crypto_scalarmult_curve25519_BYTES | :heavy_check_mark: | 
| crypto_scalarmult_curve25519_SCALARBYTES | |   
| crypto_scalarmult_ed25519_BYTES | |    
| crypto_scalarmult_ed25519_SCALARBYTES | |  
| crypto_scalarmult_ristretto255_BYTES | |   
| crypto_scalarmult_ristretto255_SCALARBYTES | | 
| crypto_secretbox_KEYBYTES |  :heavy_check_mark:  |  
| crypto_secretbox_MACBYTES |  :heavy_check_mark:  |  
| crypto_secretbox_MESSAGEBYTES_MAX | |  
| crypto_secretbox_NONCEBYTES | :heavy_check_mark:  |    
| crypto_secretbox_xchacha20poly1305_KEYBYTES | |    
| crypto_secretbox_xchacha20poly1305_MACBYTES | |    
| crypto_secretbox_xchacha20poly1305_MESSAGEBYTES_MAX | |    
| crypto_secretbox_xchacha20poly1305_NONCEBYTES | |  
| crypto_secretbox_xsalsa20poly1305_KEYBYTES | | 
| crypto_secretbox_xsalsa20poly1305_MACBYTES | | 
| crypto_secretbox_xsalsa20poly1305_MESSAGEBYTES_MAX | | 
| crypto_secretbox_xsalsa20poly1305_NONCEBYTES | |   
| crypto_secretstream_xchacha20poly1305_ABYTES |  :heavy_check_mark:  |   
| crypto_secretstream_xchacha20poly1305_HEADERBYTES |  :heavy_check_mark:  |  
| crypto_secretstream_xchacha20poly1305_KEYBYTES |  :heavy_check_mark:  | 
| crypto_secretstream_xchacha20poly1305_MESSAGEBYTES_MAX | | 
| crypto_secretstream_xchacha20poly1305_TAG_FINAL |  :heavy_check_mark:  |    
| crypto_secretstream_xchacha20poly1305_TAG_MESSAGE |  :heavy_check_mark:  |  
| crypto_secretstream_xchacha20poly1305_TAG_PUSH |  :heavy_check_mark:  | 
| crypto_secretstream_xchacha20poly1305_TAG_REKEY |  :heavy_check_mark:  |    
| crypto_shorthash_BYTES | | 
| crypto_shorthash_KEYBYTES | |  
| crypto_shorthash_siphash24_BYTES | |   
| crypto_shorthash_siphash24_KEYBYTES | |    
| crypto_shorthash_siphashx24_BYTES | |  
| crypto_shorthash_siphashx24_KEYBYTES | |   
| crypto_sign_BYTES | :heavy_check_mark: |  
| crypto_sign_MESSAGEBYTES_MAX | |   
| crypto_sign_PUBLICKEYBYTES | :heavy_check_mark: | 
| crypto_sign_SECRETKEYBYTES | :heavy_check_mark: | 
| crypto_sign_SEEDBYTES | :heavy_check_mark: |  
| crypto_sign_ed25519_BYTES | |  
| crypto_sign_ed25519_MESSAGEBYTES_MAX | |   
| crypto_sign_ed25519_PUBLICKEYBYTES | | 
| crypto_sign_ed25519_SECRETKEYBYTES | | 
| crypto_sign_ed25519_SEEDBYTES | |  
| crypto_stream_KEYBYTES | | 
| crypto_stream_MESSAGEBYTES_MAX | | 
| crypto_stream_NONCEBYTES | |   
| crypto_stream_chacha20_IETF_KEYBYTES | |   
| crypto_stream_chacha20_IETF_MESSAGEBYTES_MAX | |   
| crypto_stream_chacha20_IETF_NONCEBYTES | | 
| crypto_stream_chacha20_KEYBYTES | |    
| crypto_stream_chacha20_MESSAGEBYTES_MAX | |    
| crypto_stream_chacha20_NONCEBYTES | |  
| crypto_stream_chacha20_ietf_KEYBYTES | |   
| crypto_stream_chacha20_ietf_MESSAGEBYTES_MAX | |   
| crypto_stream_chacha20_ietf_NONCEBYTES | | 
| crypto_stream_salsa2012_KEYBYTES | |   
| crypto_stream_salsa2012_MESSAGEBYTES_MAX | |   
| crypto_stream_salsa2012_NONCEBYTES | | 
| crypto_stream_salsa208_KEYBYTES | |    
| crypto_stream_salsa208_MESSAGEBYTES_MAX | |    
| crypto_stream_salsa208_NONCEBYTES | |  
| crypto_stream_salsa20_KEYBYTES | | 
| crypto_stream_salsa20_MESSAGEBYTES_MAX | | 
| crypto_stream_salsa20_NONCEBYTES | |   
| crypto_stream_xchacha20_KEYBYTES | |   
| crypto_stream_xchacha20_MESSAGEBYTES_MAX | |   
| crypto_stream_xchacha20_NONCEBYTES | | 
| crypto_stream_xsalsa20_KEYBYTES | |    
| crypto_stream_xsalsa20_MESSAGEBYTES_MAX | |    
| crypto_stream_xsalsa20_NONCEBYTES | |  
| crypto_verify_16_BYTES | | 
| crypto_verify_32_BYTES | | 
| crypto_verify_64_BYTES | | 
| SODIUM_VERSION_STRING | |  
| crypto_pwhash_STRPREFIX | |    
| crypto_pwhash_scryptsalsa208sha256_STRPREFIX | |   
