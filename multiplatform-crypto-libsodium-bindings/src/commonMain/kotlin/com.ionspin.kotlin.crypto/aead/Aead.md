# Package com.ionspin.kotlin.crypto.aead

## Authenticated encryption with associated data

This is a form of symmetric encryption, that assures both confidentiality and authenticity of the data to be encrypted as well
as associated data that will not be encrypted. 

In general, it works like this:

Inputs:
- Message to encrypt and authenticate
- Key to use for encryption
- **Unique** nonce
- Additional data that is not encrypted but also authenticated

Simplified encryption algorithm:
1. Encrypt a message with key and nonce
1. Apply MAC algorithm to encrypted message + unencrypted associated data to generate authentication data (tag)
1. Send the encrypted data + associated data + authentication data + nonce

Simplified decryption algorithm:
1. Apply MAC algorithm to encrypted message + unencrypted associated data to generate authentication data
1. If the generated authenticated data, and the received authentication data match, proceed, otherwise sound the alarm and stop.
1. Decrypt the encrypted data
1. Return the decrypted data and associated data to the user



