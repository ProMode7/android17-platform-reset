package com.promode.android17hardening.localnetwork

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

/**
 * Item 2: ACCESS_LOCAL_NETWORK, the new Android 17 (API 37) runtime permission.
 *
 * Per https://developer.android.com/about/versions/17/behavior-changes-17 :
 * "Android 17 introduces the ACCESS_LOCAL_NETWORK runtime permission to protect users from
 * unauthorized local network access. [...] Apps targeting Android 17 (API level 37) or higher
 * now have two paths to maintain communication with LAN devices: Adopt system-mediated,
 * privacy-preserving device pickers to skip the permission prompt, or explicitly request this
 * new permission at runtime."
 *
 * It gates raw sockets, mDNS/SSDP, and framework APIs such as [android.net.nsd.NsdManager] used
 * against local-network (RFC 1918 / link-local) addresses. It is part of the existing
 * NEARBY_DEVICES permission group, so a user who already granted a Bluetooth "nearby devices"
 * permission is not re-prompted. The permission has no effect for apps whose targetSdk is 36 or
 * below: on those, local network access is implicitly covered by INTERNET, matching platform
 * behavior verified against android-37.0's api-versions.xml (`ACCESS_LOCAL_NETWORK since="37.0"`).
 */
object LocalNetworkPermission {

    const val PERMISSION: String = Manifest.permission.ACCESS_LOCAL_NETWORK

    /** Outcome of a gating check performed before starting local-network I/O. */
    sealed interface GateResult {
        /** Permission is granted (or the running platform predates API 37, where it's a no-op). */
        data object Granted : GateResult

        /** Permission must be requested from the user before proceeding. */
        data object ShouldRequest : GateResult

        /** The user has denied the permission; local network traffic will be blocked. */
        data object Denied : GateResult
    }

    /**
     * Pure decision function, unit-testable without a device: given whether the platform
     * enforces the permission at all and the current [PackageManager] grant state, decides
     * what the caller should do next.
     */
    fun decide(platformEnforcesPermission: Boolean, isGranted: Boolean): GateResult = when {
        !platformEnforcesPermission -> GateResult.Granted
        isGranted -> GateResult.Granted
        else -> GateResult.ShouldRequest
    }

    /** True only on API 37+, where the platform actually enforces this permission. */
    fun platformEnforcesPermission(): Boolean = Build.VERSION.SDK_INT >= 37

    /** Real (non-pure) check against the current process's granted permissions. */
    fun currentGateResult(context: Context): GateResult {
        val granted = ContextCompat.checkSelfPermission(
            context,
            PERMISSION
        ) == PackageManager.PERMISSION_GRANTED
        return decide(platformEnforcesPermission(), granted)
    }
}
