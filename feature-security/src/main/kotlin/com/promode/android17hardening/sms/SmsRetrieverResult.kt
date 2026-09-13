package com.promode.android17hardening.sms

/** Outcome of a single SMS Retriever listening session. */
sealed interface SmsRetrieverResult {
    /** No listening session has been started yet. */
    data object Idle : SmsRetrieverResult

    /** [com.google.android.gms.auth.api.phone.SmsRetrieverClient.startSmsRetriever] is armed. */
    data object Listening : SmsRetrieverResult

    /** A matching SMS arrived and a one-time code was extracted from its body. */
    data class CodeReceived(val code: String, val rawMessage: String) : SmsRetrieverResult

    /** A matching SMS arrived but no numeric code could be parsed out of it. */
    data class MessageReceivedNoCode(val rawMessage: String) : SmsRetrieverResult

    /** No matching SMS arrived before the ~5 minute window elapsed. */
    data object Timeout : SmsRetrieverResult

    /** Starting the retriever, or the broadcast itself, reported a non-timeout failure. */
    data class Error(val reason: String) : SmsRetrieverResult
}
