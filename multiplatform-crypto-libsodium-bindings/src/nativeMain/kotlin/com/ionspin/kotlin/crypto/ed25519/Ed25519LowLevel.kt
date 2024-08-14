package com.ionspin.kotlin.crypto.ed25519

import com.ionspin.kotlin.crypto.GeneralLibsodiumException.Companion.ensureLibsodiumSuccess
import com.ionspin.kotlin.crypto.util.toPtr
import kotlinx.cinterop.usePinned
import libsodium.crypto_core_ed25519_add
import libsodium.crypto_core_ed25519_from_uniform
import libsodium.crypto_core_ed25519_is_valid_point
import libsodium.crypto_core_ed25519_random
import libsodium.crypto_core_ed25519_scalar_add
import libsodium.crypto_core_ed25519_scalar_complement
import libsodium.crypto_core_ed25519_scalar_invert
import libsodium.crypto_core_ed25519_scalar_mul
import libsodium.crypto_core_ed25519_scalar_negate
import libsodium.crypto_core_ed25519_scalar_random
import libsodium.crypto_core_ed25519_scalar_reduce
import libsodium.crypto_core_ed25519_scalar_sub
import libsodium.crypto_core_ed25519_sub
import libsodium.crypto_scalarmult_ed25519
import libsodium.crypto_scalarmult_ed25519_base
import libsodium.crypto_scalarmult_ed25519_base_noclamp
import libsodium.crypto_scalarmult_ed25519_noclamp


actual abstract class Ed25519LowLevel actual constructor() {
    actual fun isValidPoint(encoded: UByteArray): Boolean {
        return encoded.usePinned { crypto_core_ed25519_is_valid_point(it.toPtr()) == 1 }
    }

    actual fun addPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        result.usePinned { resultPinned ->
            p.usePinned { pPinned ->
                q.usePinned { qPinned ->
                    crypto_core_ed25519_add(resultPinned.toPtr(), pPinned.toPtr(), qPinned.toPtr())
                        .ensureLibsodiumSuccess()
                }
            }
        }

        return result
    }

    actual fun subtractPoints(p: UByteArray, q: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        result.usePinned { resultPinned ->
            p.usePinned { pPinned ->
                q.usePinned { qPinned ->
                    crypto_core_ed25519_sub(resultPinned.toPtr(), pPinned.toPtr(), qPinned.toPtr())
                        .ensureLibsodiumSuccess()
                }
            }
        }

        return result
    }

    actual fun encodedPointFromUniform(uniform: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        result.usePinned { resultPinned ->
            uniform.usePinned { uniformPinned ->
                crypto_core_ed25519_from_uniform(resultPinned.toPtr(), uniformPinned.toPtr())
                    .ensureLibsodiumSuccess()
            }
        }

        return result
    }

    actual fun randomEncodedPoint(): UByteArray = UByteArray(crypto_core_ed25519_BYTES).apply {
        usePinned { crypto_core_ed25519_random(it.toPtr()) }
    }

    actual fun randomEncodedScalar(): UByteArray = UByteArray(crypto_core_ed25519_SCALARBYTES).apply {
        usePinned { crypto_core_ed25519_scalar_random(it.toPtr()) }
    }

    actual fun invertScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        result.usePinned { resultPinned ->
            scalar.usePinned { scalarPinned ->
                crypto_core_ed25519_scalar_invert(resultPinned.toPtr(), scalarPinned.toPtr()).ensureLibsodiumSuccess()
            }
        }


        return result
    }

    actual fun negateScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        result.usePinned { resultPinned ->
            scalar.usePinned { scalarPinned ->
                crypto_core_ed25519_scalar_negate(resultPinned.toPtr(), scalarPinned.toPtr())
            }
        }


        return result
    }

    actual fun complementScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        result.usePinned { resultPinned ->
            scalar.usePinned { scalarPinned ->
                crypto_core_ed25519_scalar_complement(resultPinned.toPtr(), scalarPinned.toPtr())
            }
        }

        return result
    }

    actual fun addScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        result.usePinned { resultPinned ->
            x.usePinned { xPinned ->
                y.usePinned { yPinned ->
                    crypto_core_ed25519_scalar_add(resultPinned.toPtr(), xPinned.toPtr(), yPinned.toPtr())
                }
            }
        }

        return result
    }

    actual fun subtractScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        result.usePinned { resultPinned ->
            x.usePinned { xPinned ->
                y.usePinned { yPinned ->
                    crypto_core_ed25519_scalar_sub(resultPinned.toPtr(), xPinned.toPtr(), yPinned.toPtr())
                }
            }
        }

        return result
    }

    actual fun multiplyScalars(x: UByteArray, y: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        result.usePinned { resultPinned ->
            x.usePinned { xPinned ->
                y.usePinned { yPinned ->
                    crypto_core_ed25519_scalar_mul(resultPinned.toPtr(), xPinned.toPtr(), yPinned.toPtr())
                }
            }
        }

        return result
    }

    actual fun reduceScalar(scalar: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_SCALARBYTES)

        result.usePinned { resultPinned ->
            scalar.usePinned { scalarPinned ->
                crypto_core_ed25519_scalar_reduce(resultPinned.toPtr(), scalarPinned.toPtr())
            }
        }

        return result
    }

    actual fun scalarMultiplication(n: UByteArray, p: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        result.usePinned { resultPinned ->
            n.usePinned { nPinned ->
                p.usePinned { pPinned ->
                    crypto_scalarmult_ed25519(resultPinned.toPtr(), nPinned.toPtr(), pPinned.toPtr())
                        .ensureLibsodiumSuccess()
                }
            }
        }


        return result
    }

    actual fun scalarMultiplicationNoClamp(n: UByteArray, p: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        result.usePinned { resultPinned ->
            n.usePinned { nPinned ->
                p.usePinned { pPinned ->
                    crypto_scalarmult_ed25519_noclamp(resultPinned.toPtr(), nPinned.toPtr(), pPinned.toPtr())
                        .ensureLibsodiumSuccess()
                }
            }
        }


        return result
    }

    actual fun scalarMultiplicationBase(n: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        result.usePinned { resultPinned ->
            n.usePinned { nPinned ->
                crypto_scalarmult_ed25519_base(resultPinned.toPtr(), nPinned.toPtr())
                    .ensureLibsodiumSuccess()
            }
        }

        return result
    }

    actual fun scalarMultiplicationBaseNoClamp(n: UByteArray): UByteArray {
        val result = UByteArray(crypto_core_ed25519_BYTES)

        result.usePinned { resultPinned ->
            n.usePinned { nPinned ->
                crypto_scalarmult_ed25519_base_noclamp(resultPinned.toPtr(), nPinned.toPtr())
                    .ensureLibsodiumSuccess()
            }
        }

        return result
    }
}