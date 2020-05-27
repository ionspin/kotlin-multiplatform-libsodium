package ext.libsodium.com.ionspin.kotlin.crypto

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
interface JsSodiumInterface {
    fun crypto_generichash(hashLength: Int, inputMessage: String) : String
}