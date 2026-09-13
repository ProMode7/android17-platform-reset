package com.promode.android17hardening.sms

/**
 * Item 1: SMS OTP autofill via Google's SMS Retriever API.
 *
 * This object has nothing to do with reading the device's SMS inbox: it only knows how
 * to pull a one-time-code out of the plain-text message body that Play Services hands the
 * app's [SmsRetrievedReceiver] after a *single*, signature-hash-matched SMS arrives. See
 * https://developer.android.com/identity/sms-retriever for the full mechanism.
 *
 * Kept as a pure function so the parsing logic is unit-testable without Play Services,
 * a device, or a live SMS.
 */
object OtpMessageParser {

    /** Matches the first run of 4 to 8 digits in the message. */
    private val OTP_REGEX = Regex("\\b(\\d{4,8})\\b")

    /**
     * Extracts a one-time code from an SMS Retriever message body, or null if none is found.
     */
    fun extractCode(messageBody: String?): String? {
        if (messageBody.isNullOrBlank()) return null
        return OTP_REGEX.find(messageBody)?.groupValues?.get(1)
    }
}
