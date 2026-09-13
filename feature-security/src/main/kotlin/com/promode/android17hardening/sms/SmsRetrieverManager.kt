package com.promode.android17hardening.sms

import android.content.Context
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Item 1: starts a Google SMS Retriever listening session.
 *
 * [SmsRetriever.getClient] / [com.google.android.gms.auth.api.phone.SmsRetrieverClient.startSmsRetriever]
 * is the entire client-side API surface -- it never touches android.permission.READ_SMS or
 * android.permission.RECEIVE_SMS. Google Play Services matches exactly one incoming SMS whose
 * body ends with an 11-character app hash (see
 * https://developer.android.com/identity/sms-retriever#computing_your_apps_hash_string) against
 * this app's signing certificate, then delivers it via the signature-protected broadcast that
 * [SmsRetrievedReceiver] listens for. No other SMS on the device is ever visible to the app.
 */
object SmsRetrieverManager {

    private val _state = MutableStateFlow<SmsRetrieverResult>(SmsRetrieverResult.Idle)
    val state: StateFlow<SmsRetrieverResult> = _state.asStateFlow()

    /** Called by [SmsRetrievedReceiver] when the broadcast arrives; not for app code to call. */
    internal fun publish(result: SmsRetrieverResult) {
        _state.value = result
    }

    /**
     * Starts a single SMS Retriever session (~5 minute window per the documented API contract).
     * Publishes [SmsRetrieverResult.Listening] to [state] once Play Services has armed the
     * listener, or [SmsRetrieverResult.Error] if the Task itself fails (e.g. Play Services
     * unavailable). The eventual matching-SMS result arrives later via [SmsRetrievedReceiver].
     */
    fun startListening(context: Context) {
        publish(SmsRetrieverResult.Idle)
        val client = SmsRetriever.getClient(context.applicationContext)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            publish(SmsRetrieverResult.Listening)
        }
        task.addOnFailureListener { exception ->
            publish(
                SmsRetrieverResult.Error(
                    exception.message ?: "Failed to start SmsRetriever (code unknown)"
                )
            )
        }
    }

    /** True if a Play Services status code represents a benign session timeout. */
    fun isTimeoutStatus(statusCode: Int): Boolean = statusCode == CommonStatusCodes.TIMEOUT
}
