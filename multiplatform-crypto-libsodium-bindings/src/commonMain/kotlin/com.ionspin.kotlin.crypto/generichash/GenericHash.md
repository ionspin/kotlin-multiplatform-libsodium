# Package com.ionspin.kotlin.crypto.generichash

## Generic hash

Generic hash package provides a easy to use hashing API that computes fixed-length fingerprint for an arbitrary long message.

In this case hashing is a process of mapping a set of input bytes to a fixed length (32 bytes) output. Loosely speaking
hash function should be practically irreversible and resistant to collisions (a case where two different inputs result in a same output)

Some examples of hash function usage:
- Verifying data integrity, i.e. downloading a file and it's hash and then recalculating the hash of the downloaded
file to verify that it hasn't changed
- Creating unique identifiers to index long data
- Password verification, i.e. server stores just the hash of the users password and then when user wants to log in, they send
the password, which server then hashes and compares to the stored hash. This way in case of breach of server security cleartext
passwords are not revealed. With that said **DONT USE GENERIC HASH FOR PASSWORD HASHING**. Use PasswordHash funcitons.

Underneath this set of functions uses BLAKE2b secure hash function, Here is what Libsodium documentation says about it
>The crypto_generichash_* function set is implemented using BLAKE2b, a simple, standardized (RFC 7693) secure hash 
>function that is as strong as SHA-3 but faster than SHA-1 and MD5.
>Unlike MD5, SHA-1 and SHA-256, this function is safe against hash length extension attacks.
>BLAKE2b is not suitable for hashing passwords. For this purpose, use the crypto_pwhash API documented 
>in the Password Hashing section.
