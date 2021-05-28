package ext.libsodium.com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.getSodiumLoaded
import com.ionspin.kotlin.crypto.setSodiumPointer
import com.ionspin.kotlin.crypto.sodiumLoaded
import com.ionspin.kotlin.crypto.sodiumPointer
import ext.libsodium.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
object JsSodiumLoader {

    class _EmitJsSodiumFunction {
        init {
            println(::crypto_generichash)
            println(::crypto_hash_sha256)
            println(::crypto_hash_sha512)
            println(::crypto_hash_sha256_init)
        }

    }

    fun storeSodium(promisedSodium: dynamic, continuation: Continuation<Unit>) {
        setSodiumPointer(promisedSodium)
        sodiumLoaded = true
        continuation.resumeWith(Result.success(Unit))
    }

    suspend fun load() = suspendCoroutine<Unit> { continuation ->
        console.log(getSodiumLoaded())
        if (!getSodiumLoaded()) {
            val libsodiumModule = js("\$module\$libsodium_wrappers_sumo")
            _libsodiumPromise.then<dynamic> {
                storeSodium(libsodiumModule, continuation)
            }
        } else {
            continuation.resumeWith(Result.success(Unit))
        }
    }

    fun loadWithCallback(doneCallback: () -> (Unit)) {
        console.log(getSodiumLoaded())
        if (!getSodiumLoaded()) {
            val libsodiumModule = js("\$module\$libsodium_wrappers_sumo")
            _libsodiumPromise.then<dynamic> {
                setSodiumPointer(libsodiumModule)
                sodiumPointer.sodium_init()
                sodiumLoaded = true
                doneCallback.invoke()
            }
        } else {
            doneCallback.invoke()
        }
    }
}
