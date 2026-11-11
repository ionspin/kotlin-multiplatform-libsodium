package ext.libsodium.com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.getSodiumLoaded
import com.ionspin.kotlin.crypto.sodiumLoaded
import com.ionspin.kotlin.crypto.sodiumModule
import kotlin.coroutines.suspendCoroutine
import kotlin.js.Promise

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
object JsSodiumLoader {

    private fun importSodium(): Promise<JsSodiumInterface> {
        val modulePromise = js("import('libsodium-wrappers-sumo')").unsafeCast<Promise<dynamic>>()
        return modulePromise.then<JsSodiumInterface> { module ->
            val sodium = (module.default ?: module)
            val sodiumWhenReady: dynamic = sodium.ready.then { sodium }
            sodiumWhenReady

        }.unsafeCast<Promise<JsSodiumInterface>>()
    }


    suspend fun load() = suspendCoroutine { continuation ->
        if (!getSodiumLoaded()) {
            importSodium().then<dynamic> { sodium ->
                sodiumModule = sodium
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
            importSodium().then<dynamic> { sodium ->
                sodiumModule = sodium
                sodiumLoaded = true
                doneCallback.invoke()
            }
        } else {
            doneCallback.invoke()
        }
    }
}
