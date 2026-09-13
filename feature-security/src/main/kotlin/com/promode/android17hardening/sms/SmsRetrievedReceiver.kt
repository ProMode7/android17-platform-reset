package com.promode.android17hardening.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

/**
 * Item 1: receives the SMS Retriever broadcast.
 *
 * Registered in AndroidManifest.xml with `android:permission
 * ="com.google.android.gms.auth.api.phone.permission.SEND"`, which only Google Play Services
 * holds. That permission check -- not android.permission.RECEIVE_SMS -- is what makes this
 * receiver safe to export: no other app on the device can forge the broadcast, and this app
 * never declares (or needs) the READ_SMS/RECEIVE_SMS permissions at all.
 */
class SmsRetrievedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != SmsRetriever.SMS_RETRIEVED_ACTION) return

        val extras = intent.extras ?: return
        @Suppress("DEPRECATION")
        val status = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return

        when (status.statusCode) {
            CommonStatusCodes.SUCCESS -> {
                val message = extras.getString(SmsRetriever.EXTRA_SMS_MESSAGE).orEmpty()
                val code = OtpMessageParser.extractCode(message)
                SmsRetrieverManager.publish(
                    if (code != null) {
                        SmsRetrieverResult.CodeReceived(code, message)
                    } else {
                        SmsRetrieverResult.MessageReceivedNoCode(message)
                    }
                )
            }

            CommonStatusCodes.TIMEOUT -> {
                SmsRetrieverManager.publish(SmsRetrieverResult.Timeout)
            }

            else -> {
                SmsRetrieverManager.publish(
                    SmsRetrieverResult.Error("SMS Retriever status code ${status.statusCode}")
                )
            }
        }
    }
}
