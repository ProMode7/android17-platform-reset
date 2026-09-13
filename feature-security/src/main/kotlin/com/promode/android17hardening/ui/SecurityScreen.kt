package com.promode.android17hardening.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pramodpatel.platformreset.design.CodeSnippetBlock
import com.pramodpatel.platformreset.design.DetailBody
import com.pramodpatel.platformreset.design.DetailBreadcrumb
import com.pramodpatel.platformreset.design.DetailHeadline
import com.pramodpatel.platformreset.design.VerifiedRow
import com.pramodpatel.platformreset.design.VerifiedSectionLabel

/**
 * The Security Hardening feature screen: this app's shared detail chrome wrapped around the real,
 * unmodified [HardeningScreen] (ported verbatim from `android17-security-hardening`), which wires
 * up all four checks -- SMS Retriever, ACCESS_LOCAL_NETWORK, Certificate Transparency, Bluetooth
 * RFCOMM `-1` handling -- against real platform APIs.
 */
@Composable
fun SecurityScreen(onBack: (() -> Unit)? = null) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Bounded and independently scrollable -- see HandoffScreen's identical pattern for why:
        // this chrome can outgrow the room left for the real HardeningScreen below it.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 260.dp)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            if (onBack != null) {
                DetailBreadcrumb(breadcrumb = "Android 17 / Security Hardening", onBack = onBack)
            }
            DetailHeadline(
                title = "Security Hardening",
                versionLine = "4 checks · SMS · LAN · TLS · Bluetooth · API 37+",
            )
            DetailBody(
                "Four Android 17 migrations: the SMS Retriever API replacing direct SMS " +
                    "reading, the ACCESS_LOCAL_NETWORK runtime permission gating NsdManager and " +
                    "raw sockets, Certificate Transparency enforced by default at targetSdk 37, " +
                    "and RFCOMM BluetoothSocket read() now returning -1 on disconnect.",
            )
            CodeSnippetBlock(
                code = "<uses-permission\n    android:name=\"android.permission.ACCESS_LOCAL_NETWORK\" />",
            )
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                VerifiedSectionLabel()
                VerifiedRow("ACCESS_LOCAL_NETWORK confirmed via android-37.0's api-versions.xml")
                VerifiedRow("RFCOMM read() == -1 behavior change has its own section on developer.android.com/about/versions/17")
                VerifiedRow("No READ_SMS / RECEIVE_SMS permission declared anywhere in this app's manifest")
            }
            VerifiedSectionLabel(text = "Live demo")
        }

        Box(modifier = Modifier.weight(1f)) {
            HardeningScreen()
        }
    }
}
