import org.khronos.webgl.Uint8Array

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 25-May-2020
 */
@JsModule("libsodium")
@JsNonModule
external fun crypto_generichash(hashLength: Int, inputMessage: Uint8Array) : Uint8Array