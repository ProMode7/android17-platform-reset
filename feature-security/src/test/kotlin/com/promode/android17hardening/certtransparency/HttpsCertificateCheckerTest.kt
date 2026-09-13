package com.promode.android17hardening.certtransparency

import javax.net.ssl.SSLHandshakeException
import org.junit.Assert.assertEquals
import org.junit.Test

class HttpsCertificateCheckerTest {

    @Test
    fun `classifies a handshake exception with its message preserved`() {
        val result = HttpsCertificateChecker.classifyHandshakeFailure(
            SSLHandshakeException("Chain validation failed")
        )
        assertEquals(
            TlsCheckResult.HandshakeValidationFailure("Chain validation failed"),
            result
        )
    }

    @Test
    fun `falls back to the exception class name when the message is null`() {
        val result = HttpsCertificateChecker.classifyHandshakeFailure(SSLHandshakeException(null))
        assertEquals("SSLHandshakeException", result.message)
    }
}
