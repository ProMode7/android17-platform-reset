package com.promode.android17hardening.localnetwork

import com.promode.android17hardening.localnetwork.LocalNetworkPermission.GateResult
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalNetworkPermissionTest {

    @Test
    fun `platform below API 37 does not enforce the permission even if not granted`() {
        assertEquals(
            GateResult.Granted,
            LocalNetworkPermission.decide(platformEnforcesPermission = false, isGranted = false)
        )
    }

    @Test
    fun `platform below API 37 is granted regardless of the flag value`() {
        assertEquals(
            GateResult.Granted,
            LocalNetworkPermission.decide(platformEnforcesPermission = false, isGranted = true)
        )
    }

    @Test
    fun `API 37+ with grant returns Granted`() {
        assertEquals(
            GateResult.Granted,
            LocalNetworkPermission.decide(platformEnforcesPermission = true, isGranted = true)
        )
    }

    @Test
    fun `API 37+ without grant returns ShouldRequest, never silently proceeds`() {
        assertEquals(
            GateResult.ShouldRequest,
            LocalNetworkPermission.decide(platformEnforcesPermission = true, isGranted = false)
        )
    }
}
