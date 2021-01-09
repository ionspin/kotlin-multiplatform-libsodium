# Package com.ionspin.kotlin.crypto.box

## Box API - Asymmetric/Public-key authenticated encryption

Public key encryption is a system that relies on a pair of keys to establish secure communication.

A simplified overview of communication between Bob and Alice using public-key encryption:
#### Key exchange
1. Alice creates 2 keys, one public, one private (public key is actually calculated from the private key)
1. Bob creates 2 keys, one public, one private
1. Alice sends her **public** key to Bob
1. Bob does the same and sends his **public** key to Alice

#### Encryption 

Alice wants to establish a secure communication channel with Bob, they already exchanged public keys in previous steps.

1. Alice uses Bobs **private** key to encrypt a *secret value* (Usually just a key for symmetric encryption)
1. Alice sends encrypted data to Bob
1. Bob is the only one who has the matching private key, and can decrypt the data
1. Bob and Alice now posses the same *secret value* and can start communicating (i.e. by using XChaCha20Poly1305 
symmetric encryption and using the *secret value* as the symmetric key)

Bob would do the same if he wanted to initiate the secure communication.

This set of functions also contains another use case called `sealed boxes` in libsodium. 
Sealed box is designed to anonymously send a message to a recipient. Libsodium documentation explains it as follows:
> A message is encrypted using an ephemeral key pair, whose secret part is destroyed right after the encryption process.
  Without knowing the secret key used for a given message, the sender cannot decrypt its own message later. And without additional data, a message cannot be correlated with the identity of its sender.


