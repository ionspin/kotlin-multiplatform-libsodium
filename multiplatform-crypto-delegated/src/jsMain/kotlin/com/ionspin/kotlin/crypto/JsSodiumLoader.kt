package ext.libsodium.com.ionspin.kotlin.crypto

import com.ionspin.kotlin.crypto.getSodiumLoaded
import com.ionspin.kotlin.crypto.getSodiumPointer
import com.ionspin.kotlin.crypto.setSodiumPointer
import com.ionspin.kotlin.crypto.sodiumLoaded
import ext.libsodium._libsodiumPromise
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Ugljesa Jovanovic
 * ugljesa.jovanovic@ionspin.com
 * on 27-May-2020
 */
object JsSodiumLoader {

    fun storeSodium(promisedSodium: dynamic, continuation: Continuation<JsSodiumInterface>) {
        setSodiumPointer(promisedSodium)
        sodiumLoaded = true
        continuation.resumeWith(Result.success(getSodiumPointer()))
    }

    suspend fun load(): JsSodiumInterface = suspendCoroutine { continuation ->
        console.log(getSodiumLoaded())
        if (!getSodiumLoaded()) {
            val libsodiumModule = js("\$module\$libsodium_wrappers_sumo")
            _libsodiumPromise.then<dynamic> { storeSodium(libsodiumModule, continuation) }
        } else {
            continuation.resumeWith(Result.success(getSodiumPointer()))
        }
    }
}