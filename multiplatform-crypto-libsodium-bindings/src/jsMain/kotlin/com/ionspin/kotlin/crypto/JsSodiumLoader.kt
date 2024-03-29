package ext.libsodium.com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.*
import ext.libsodium.*
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

    suspend fun load() = suspendCoroutine<Unit> { continuation ->
        if (!getSodiumLoaded()) {
            _libsodiumPromise.then<dynamic> {
                sodium_init()
                sodiumLoaded = true
                continuation.resumeWith(Result.success(Unit))
            }.catch { e ->
                continuation.resumeWith(Result.failure(e))
            }
        } else {
            continuation.resumeWith(Result.success(Unit))
        }
    }

    fun loadWithCallback(doneCallback: () -> (Unit)) {
        if (!getSodiumLoaded()) {
            _libsodiumPromise.then<dynamic> {
                sodium_init()
                sodiumLoaded = true
                doneCallback.invoke()
            }
        } else {
            doneCallback.invoke()
        }
    }
}
