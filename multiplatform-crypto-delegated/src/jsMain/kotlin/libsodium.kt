@file:JsModule("libsodium-wrappers-sumo")
@file:JsNonModule
package ext.libsodium

import org.khronos.webgl.Uint8Array
import kotlin.js.Promise


/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-May-2020
 */

@JsName("ready")
external val _libsodiumPromise : Promise<dynamic>

external fun crypto_generichash(hashLength: Int, inputMessage: Uint8Array) : Uint8Array

external fun crypto_hash_sha256(message: Uint8Array) : Uint8Array
external fun crypto_hash_sha512(message: Uint8Array) : Uint8Array

external fun crypto_hash_sha256_init(): dynamic





