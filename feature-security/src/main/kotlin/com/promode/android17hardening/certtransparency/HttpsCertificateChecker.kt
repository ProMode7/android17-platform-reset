package com.promode.android17hardening.certtransparency

import android.os.Build
import android.util.Log
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

private const val TAG = "CertTransparency"

/**
 * Item 3: Certificate Transparency (CT) enforcement.
 *
 * Per https://developer.android.com/about/versions/17/behavior-changes-17 :
 * "If an app targets Android 17 (API level 37) or higher, certificate transparency (CT) is
 * enabled by default. (On Android 16, CT is available but apps had to opt in.)"
 *
 * CT enforcement itself is entirely automatic, inside the platform's default TrustManager /
 * Conscrypt TLS stack -- there is no app-facing API to query "was this specific connection CT
 * compliant". What this class demonstrates is the correct app-level pattern: perform a normal
 * HTTPS request, do not swallow or downgrade TLS validation failures, and log the negotiated
 * session parameters on success versus a structured failure otherwise. See
 * res/xml/network_security_config.xml for the documented per-domain opt-out knob
 * (`<certificateTransparency enabled="false"/>`), which this app does not use.
 *
 * Must be called off the main thread (e.g. from Dispatchers.IO); it performs blocking I/O.
 */
object HttpsCertificateChecker {

    /** True on platforms where the OS enforces CT by default for a targetSdk 37 app. */
    fun ctEnforcedByPlatform(): Boolean = Build.VERSION.SDK_INT >= 37

    fun check(urlString: String): TlsCheckResult {
        var connection: HttpsURLConnection? = null
        return try {
            connection = (URL(urlString).openConnection() as HttpsURLConnection).apply {
                connectTimeout = 10_000
                readTimeout = 10_000
                requestMethod = "HEAD"
            }
            connection.connect()

            val result = TlsCheckResult.Success(
                cipherSuite = connection.cipherSuite.orEmpty(),
                peerPrincipal = connection.serverCertificates
                    .firstOrNull()
                    ?.let { (it as? java.security.cert.X509Certificate)?.subjectX500Principal?.name }
                    .orEmpty(),
                ctEnforcedByPlatform = ctEnforcedByPlatform()
            )
            Log.i(TAG, "TLS handshake succeeded: $result")
            result
        } catch (e: SSLHandshakeException) {
            Log.w(TAG, "TLS handshake/certificate validation failed", e)
            classifyHandshakeFailure(e)
        } catch (e: SSLException) {
            Log.w(TAG, "TLS session failed", e)
            classifyHandshakeFailure(e)
        } catch (e: IOException) {
            Log.w(TAG, "Network I/O failure before/without a TLS result", e)
            TlsCheckResult.NetworkFailure(e.message ?: e.javaClass.simpleName)
        } finally {
            connection?.disconnect()
        }
    }

    /** Pure classification, unit-testable without a live connection. */
    fun classifyHandshakeFailure(e: Exception): TlsCheckResult.HandshakeValidationFailure =
        TlsCheckResult.HandshakeValidationFailure(e.message ?: e.javaClass.simpleName)
}
