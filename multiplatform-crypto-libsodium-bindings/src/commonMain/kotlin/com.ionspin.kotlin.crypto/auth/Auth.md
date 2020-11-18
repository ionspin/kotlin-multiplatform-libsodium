# Package com.ionspin.kotlin.crypto.auth

## Authentication

Authentication is a process of generating authentication data (tag) for a certain message. Its purpose is to assure
that the data hasn't been corrupted or tampered with during the transport.

In general, it works like this:

Inputs:
- Message to authenticate
- Key to use for authentication

Sending side algorithm:
1. Apply MAC to message
1. Send the message + authentication data (tag)

Receiving side:
1. Apply the MAC to the received message
1. If the generated authenticated data (tag), and the received authentication data (received tag) match, proceed, otherwise sound the alarm and stop.
1. Return the message to the user
