package com.promode.android17hardening.certtransparency

/** Outcome of an HTTPS connection attempt, classified for item 3 (Certificate Transparency). */
sealed interface TlsCheckResult {
    data object Idle : TlsCheckResult

    /**
     * The TLS handshake and certificate validation (including CT compliance evaluation on
     * CT-enforcing platforms, i.e. apps targeting API 37+) succeeded.
     */
    data class Success(
        val cipherSuite: String,
        val peerPrincipal: String,
        val ctEnforcedByPlatform: Boolean
    ) : TlsCheckResult

    /**
     * The handshake failed during certificate/chain validation. On a CT-enforcing platform this
     * is the failure mode a non-CT-compliant certificate would surface as; the public API gives
     * no distinct exception type or field to separately confirm "failed because of CT" versus
     * "failed for another chain-validation reason", so this sample reports it as a validation
     * failure rather than asserting a CT-specific cause it cannot verify.
     */
    data class HandshakeValidationFailure(val message: String) : TlsCheckResult

    /** DNS/socket/timeout failure unrelated to certificate validation. */
    data class NetworkFailure(val message: String) : TlsCheckResult
}
