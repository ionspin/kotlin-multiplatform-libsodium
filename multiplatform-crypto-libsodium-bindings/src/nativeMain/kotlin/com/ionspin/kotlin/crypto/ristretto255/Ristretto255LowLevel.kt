package com.ionspin.kotlin.crypto.ristretto255

import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.usePinned
import libsodium.crypto_core_ristretto255_add
import libsodium.crypto_core_ristretto255_from_hash
import libsodium.crypto_core_ristretto255_is_valid_point
import libsodium.crypto_core_ristretto255_random
import libsodium.crypto_core_ristretto255_scalar_add
import libsodium.crypto_core_ristretto255_scalar_complement
import libsodium.crypto_core_ristretto255_scalar_invert
import libsodium.crypto_core_ristretto255_scalar_mul
import libsodium.crypto_core_ristretto255_scalar_negate
import libsodium.crypto_core_ristretto255_scalar_random
import libsodium.crypto_core_ristretto255_scalar_reduce
import libsodium.crypto_core_ristretto255_scalar_sub
import libsodium.crypto_core_ristretto255_sub
import libsodium.crypto_scalarmult_ristretto255
import libsodium.crypto_scalarmult_ristretto255_base


actual abstract class Ristretto255LowLevel actual constructor() {
  actual fun isValidPoint(encoded: UByteArray): Boolean {
    return encoded.usePinned { crypto_core_ristretto255_is_valid_point(it.toPtr()) == 1 }
  }

  actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    result.usePinned { resultPinned ->
      p.usePinned { pPinned ->
        q.usePinned { qPinned ->
          crypto_core_ristretto255_add(resultPinned.toPtr(), pPinned.toPtr(), qPinned.toPtr())
            .ensureLibsodiumSuccess()
        }
      }
    }

    return result
  }

  actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    result.usePinned { resultPinned ->
      p.usePinned { pPinned ->
        q.usePinned { qPinned ->
          crypto_core_ristretto255_sub(resultPinned.toPtr(), pPinned.toPtr(), qPinned.toPtr())
            .ensureLibsodiumSuccess()
        }
      }
    }

    return result
  }

  actual fun encodedPointFromHash(hash: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    result.usePinned { resultPinned ->
      hash.usePinned { hashPinned ->
        crypto_core_ristretto255_from_hash(resultPinned.toPtr(), hashPinned.toPtr())
      }
    }

    return result
  }

  actual fun randomEncodedPoint(): UByteArray = UByteArray(crypto_core_ristretto255_BYTES).apply {
    usePinned { crypto_core_ristretto255_random(it.toPtr()) }
  }

  actual fun randomEncodedScalar(): UByteArray = UByteArray(crypto_core_ristretto255_SCALARBYTES).apply {
    usePinned { crypto_core_ristretto255_scalar_random(it.toPtr()) }
  }

  actual fun invert(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    result.usePinned { resultPinned ->
      scalar.usePinned { scalarPinned ->
        crypto_core_ristretto255_scalar_invert(resultPinned.toPtr(), scalarPinned.toPtr()).ensureLibsodiumSuccess()
      }
    }


    return result
  }

  actual fun negate(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    result.usePinned { resultPinned ->
      scalar.usePinned { scalarPinned ->
        crypto_core_ristretto255_scalar_negate(resultPinned.toPtr(), scalarPinned.toPtr())
      }
    }


    return result
  }

  actual fun complement(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    result.usePinned { resultPinned ->
      scalar.usePinned { scalarPinned ->
        crypto_core_ristretto255_scalar_complement(resultPinned.toPtr(), scalarPinned.toPtr())
      }
    }

    return result
  }

  actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    result.usePinned { resultPinned ->
      x.usePinned { xPinned ->
        y.usePinned { yPinned ->
          crypto_core_ristretto255_scalar_add(resultPinned.toPtr(), xPinned.toPtr(), yPinned.toPtr())
        }
      }
    }

    return result
  }

  actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    result.usePinned { resultPinned ->
      x.usePinned { xPinned ->
        y.usePinned { yPinned ->
          crypto_core_ristretto255_scalar_sub(resultPinned.toPtr(), xPinned.toPtr(), yPinned.toPtr())
        }
      }
    }

    return result
  }

  actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    result.usePinned { resultPinned ->
      x.usePinned { xPinned ->
        y.usePinned { yPinned ->
          crypto_core_ristretto255_scalar_mul(resultPinned.toPtr(), xPinned.toPtr(), yPinned.toPtr())
        }
      }
    }

    return result
  }

  actual fun reduce(scalar: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_SCALARBYTES)

    result.usePinned { resultPinned ->
      scalar.usePinned { scalarPinned ->
        crypto_core_ristretto255_scalar_reduce(resultPinned.toPtr(), scalarPinned.toPtr())
      }
    }

    return result
  }

  actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    result.usePinned { resultPinned ->
      n.usePinned { nPinned ->
        p.usePinned { pPinned ->
          crypto_scalarmult_ristretto255(resultPinned.toPtr(), nPinned.toPtr(), pPinned.toPtr())
        }
      }
    }


    return result
  }

  actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
    val result = UByteArray(crypto_core_ristretto255_BYTES)

    result.usePinned { resultPinned ->
      n.usePinned { nPinned ->
        crypto_scalarmult_ristretto255_base(resultPinned.toPtr(), nPinned.toPtr())
      }
    }

    return result
  }
}