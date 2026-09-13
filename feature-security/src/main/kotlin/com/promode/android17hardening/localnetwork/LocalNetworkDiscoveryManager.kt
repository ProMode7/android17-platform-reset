package com.promode.android17hardening.localnetwork

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.core.content.getSystemService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private const val TAG = "LocalNetworkDiscovery"
private const val SERVICE_TYPE = "_http._tcp"

/** Outcome of a local network service discovery attempt. */
sealed interface DiscoveryState {
    data object Idle : DiscoveryState
    data object PermissionDenied : DiscoveryState
    data object Discovering : DiscoveryState
    data class ServiceFound(val serviceName: String) : DiscoveryState
    data class Failed(val errorCode: Int) : DiscoveryState
}

/**
 * Item 2: gates [NsdManager] service discovery -- the framework API named explicitly in the
 * official ACCESS_LOCAL_NETWORK documentation -- behind the runtime permission check, with a
 * graceful denied path instead of letting discoverServices() fail with a SecurityException.
 */
class LocalNetworkDiscoveryManager(private val context: Context) {

    private val _state = MutableStateFlow<DiscoveryState>(DiscoveryState.Idle)
    val state: StateFlow<DiscoveryState> = _state.asStateFlow()

    private var listener: NsdManager.DiscoveryListener? = null

    /**
     * Starts NSD service discovery only if [LocalNetworkPermission] is granted (or the running
     * platform doesn't enforce it). Otherwise publishes [DiscoveryState.PermissionDenied] and
     * returns without touching NsdManager at all -- the graceful denied path.
     */
    fun startDiscovery() {
        when (LocalNetworkPermission.currentGateResult(context)) {
            LocalNetworkPermission.GateResult.Denied,
            LocalNetworkPermission.GateResult.ShouldRequest -> {
                _state.value = DiscoveryState.PermissionDenied
                return
            }

            LocalNetworkPermission.GateResult.Granted -> Unit
        }

        val nsdManager = context.getSystemService<NsdManager>() ?: run {
            _state.value = DiscoveryState.Failed(NsdManager.FAILURE_INTERNAL_ERROR)
            return
        }

        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                _state.value = DiscoveryState.Discovering
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                _state.value = DiscoveryState.ServiceFound(serviceInfo.serviceName)
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                Log.i(TAG, "Service lost: ${serviceInfo.serviceName}")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                _state.value = DiscoveryState.Idle
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                _state.value = DiscoveryState.Failed(errorCode)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                _state.value = DiscoveryState.Failed(errorCode)
            }
        }
        listener = discoveryListener

        try {
            @Suppress("DEPRECATION")
            nsdManager.discoverServices(
                SERVICE_TYPE,
                NsdManager.PROTOCOL_DNS_SD,
                discoveryListener
            )
        } catch (e: SecurityException) {
            // Defensive: covers a permission revoked between the check above and this call.
            Log.w(TAG, "discoverServices denied at the platform level", e)
            _state.value = DiscoveryState.PermissionDenied
        }
    }

    fun stopDiscovery() {
        val nsdManager = context.getSystemService<NsdManager>() ?: return
        listener?.let {
            try {
                nsdManager.stopServiceDiscovery(it)
            } catch (e: IllegalArgumentException) {
                // Listener was never successfully registered; nothing to stop.
                Log.d(TAG, "stopServiceDiscovery: listener not registered", e)
            }
        }
        listener = null
    }
}
