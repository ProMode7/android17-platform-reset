package com.promode.android17hardening.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.promode.android17hardening.bluetooth.BluetoothReadOutcome
import com.promode.android17hardening.bluetooth.interpretRfcommRead
import com.promode.android17hardening.certtransparency.HttpsCertificateChecker
import com.promode.android17hardening.certtransparency.TlsCheckResult
import com.promode.android17hardening.localnetwork.DiscoveryState
import com.promode.android17hardening.localnetwork.LocalNetworkDiscoveryManager
import com.promode.android17hardening.localnetwork.LocalNetworkPermission
import com.promode.android17hardening.sms.SmsRetrieverManager
import com.promode.android17hardening.sms.SmsRetrieverResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private data class CheckItem(
    val title: String,
    val subtitle: String,
    val status: CheckStatus,
    val detail: String,
    val actionLabel: String,
    val onAction: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HardeningScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // --- Item 1: SMS Retriever ---
    val smsState by SmsRetrieverManager.state.collectAsStateWithLifecycle()

    // --- Item 2: ACCESS_LOCAL_NETWORK ---
    val discoveryManager = remember { LocalNetworkDiscoveryManager(context) }
    val discoveryState by discoveryManager.state.collectAsStateWithLifecycle()
    val localNetworkPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) discoveryManager.startDiscovery()
    }

    // --- Item 3: Certificate Transparency ---
    var tlsResult by remember { mutableStateOf<TlsCheckResult>(TlsCheckResult.Idle) }

    // --- Item 4: Bluetooth -1 sentinel handling ---
    var bluetoothDemoOutcome by remember { mutableStateOf<BluetoothReadOutcome?>(null) }
    var bluetoothDemoStep by remember { mutableStateOf(0) }
    val bluetoothDemoSequence = remember { listOf(12, 0, -1) }

    val items = listOf(
        CheckItem(
            title = "1. SMS Retriever autofill",
            subtitle = "No READ_SMS / RECEIVE_SMS permission declared",
            status = when (smsState) {
                is SmsRetrieverResult.Idle -> CheckStatus.UNKNOWN
                is SmsRetrieverResult.Listening -> CheckStatus.RUNNING
                is SmsRetrieverResult.CodeReceived -> CheckStatus.PASS
                is SmsRetrieverResult.MessageReceivedNoCode -> CheckStatus.FAIL
                is SmsRetrieverResult.Timeout -> CheckStatus.FAIL
                is SmsRetrieverResult.Error -> CheckStatus.FAIL
            },
            detail = when (val s = smsState) {
                is SmsRetrieverResult.Idle -> "Tap start; requires a real SMS matching this app's hash to complete."
                is SmsRetrieverResult.Listening -> "Listening for a matching SMS (~5 min window)."
                is SmsRetrieverResult.CodeReceived -> "Code extracted: ${s.code}"
                is SmsRetrieverResult.MessageReceivedNoCode -> "Message received but no numeric code found."
                is SmsRetrieverResult.Timeout -> "Timed out waiting for a matching SMS."
                is SmsRetrieverResult.Error -> "Error: ${s.reason}"
            },
            actionLabel = "Start SMS Retriever",
            onAction = { SmsRetrieverManager.startListening(context) }
        ),
        CheckItem(
            title = "2. ACCESS_LOCAL_NETWORK",
            subtitle = "Android 17 runtime permission gating LAN access (NsdManager)",
            status = when (discoveryState) {
                DiscoveryState.Idle -> CheckStatus.UNKNOWN
                DiscoveryState.PermissionDenied -> CheckStatus.DENIED
                DiscoveryState.Discovering -> CheckStatus.RUNNING
                is DiscoveryState.ServiceFound -> CheckStatus.PASS
                is DiscoveryState.Failed -> CheckStatus.FAIL
            },
            detail = when (val s = discoveryState) {
                DiscoveryState.Idle -> "Tap start; requests ACCESS_LOCAL_NETWORK, then discovers _http._tcp services."
                DiscoveryState.PermissionDenied -> "Permission denied -- local network traffic is blocked (graceful path)."
                DiscoveryState.Discovering -> "Discovering local network services..."
                is DiscoveryState.ServiceFound -> "Found service: ${s.serviceName}"
                is DiscoveryState.Failed -> "NSD error code ${s.errorCode}"
            },
            actionLabel = "Request permission & discover",
            onAction = {
                when (LocalNetworkPermission.currentGateResult(context)) {
                    LocalNetworkPermission.GateResult.Granted -> discoveryManager.startDiscovery()
                    else -> localNetworkPermissionLauncher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
                }
            }
        ),
        CheckItem(
            title = "3. Certificate Transparency",
            subtitle = "HTTPS request; CT is enforced by default at targetSdk 37",
            status = when (tlsResult) {
                TlsCheckResult.Idle -> CheckStatus.UNKNOWN
                is TlsCheckResult.Success -> CheckStatus.PASS
                is TlsCheckResult.HandshakeValidationFailure -> CheckStatus.FAIL
                is TlsCheckResult.NetworkFailure -> CheckStatus.FAIL
            },
            detail = when (val r = tlsResult) {
                TlsCheckResult.Idle -> "Tap start; requires live network access."
                is TlsCheckResult.Success ->
                    "TLS OK, cipher=${r.cipherSuite}, CT enforced by platform=${r.ctEnforcedByPlatform}"
                is TlsCheckResult.HandshakeValidationFailure -> "Handshake/validation failed: ${r.message}"
                is TlsCheckResult.NetworkFailure -> "Network failure: ${r.message}"
            },
            actionLabel = "Test HTTPS connection",
            onAction = {
                scope.launch {
                    tlsResult = withContext(Dispatchers.IO) {
                        HttpsCertificateChecker.check("https://example.com")
                    }
                }
            }
        ),
        CheckItem(
            title = "4. Bluetooth read() sentinel",
            subtitle = "RFCOMM InputStream.read() == -1 handling (Android 17 behavior change)",
            status = when (bluetoothDemoOutcome) {
                null -> CheckStatus.UNKNOWN
                is BluetoothReadOutcome.EndOfStream -> CheckStatus.PASS
                is BluetoothReadOutcome.DataRead,
                is BluetoothReadOutcome.ZeroByteRead -> CheckStatus.RUNNING
                is BluetoothReadOutcome.UnexpectedNegativeValue -> CheckStatus.FAIL
            },
            detail = when (val o = bluetoothDemoOutcome) {
                null -> "Tap start; steps through a simulated read() sequence (12, 0, -1) via interpretRfcommRead()."
                is BluetoothReadOutcome.DataRead -> "read() -> ${o.numBytes}: data, loop continues."
                BluetoothReadOutcome.ZeroByteRead -> "read() -> 0: no documented meaning, logged, loop continues."
                BluetoothReadOutcome.EndOfStream -> "read() -> -1: end of stream, loop terminates correctly."
                is BluetoothReadOutcome.UnexpectedNegativeValue -> "read() -> ${o.numBytes}: undocumented, treated as a failure."
            },
            actionLabel = "Step simulated read()",
            onAction = {
                val value = bluetoothDemoSequence[bluetoothDemoStep % bluetoothDemoSequence.size]
                bluetoothDemoOutcome = interpretRfcommRead(value)
                bluetoothDemoStep++
            }
        )
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Android 17 Hardening") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item -> CheckCard(item) }
        }
    }
}

@Composable
private fun CheckCard(item: CheckItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(item.title, style = MaterialTheme.typography.titleMedium)
                Text(item.status.label, style = MaterialTheme.typography.labelLarge)
            }
            Text(item.subtitle, style = MaterialTheme.typography.bodySmall)
            Text(item.detail, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = item.onAction, modifier = Modifier.padding(top = 8.dp)) {
                Text(item.actionLabel)
            }
        }
    }
}
