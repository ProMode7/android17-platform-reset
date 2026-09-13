package com.pramodpatel.notesfunctions

import android.content.Intent
import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Verifies that the AppFunctions entry point declared in AndroidManifest.xml is
 * actually present and discoverable through the standard Android package
 * manager, the same mechanism the platform's AppFunctions indexer relies on
 * (an exported service with an `android.app.appfunctions.AppFunctionService`
 * intent-filter action).
 *
 * This deliberately does not depend on androidx.appfunctions-testing's
 * [androidx.appfunctions.testing.AppFunctionTestRule], whose metadata-population
 * API is still evolving in the 1.0.0-alpha11 release; PackageManager resolution
 * is the stable, documented contract a service must satisfy to be found at all.
 */
@RunWith(AndroidJUnit4::class)
class AppFunctionServiceDiscoveryTest {

    @Test
    fun notesAppFunctionService_isDeclaredAndExported() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent("android.app.appfunctions.AppFunctionService")
            .setPackage(context.packageName)

        val resolved = context.packageManager.queryIntentServices(
            intent,
            PackageManager.ResolveInfoFlags.of(0L),
        )

        assertTrue(
            "Expected an AppFunctionService declared for ${context.packageName}, found none. " +
                "Check the <service> entry and intent-filter in AndroidManifest.xml.",
            resolved.isNotEmpty(),
        )

        val serviceInfo = resolved.first().serviceInfo
        assertEquals(
            "com.pramodpatel.notesfunctions.appfunctions.NotesAppFunctionService",
            serviceInfo.name,
        )
        assertTrue("AppFunctionService must be exported", serviceInfo.exported)
        assertEquals(
            "android.permission.BIND_APP_FUNCTION_SERVICE",
            serviceInfo.permission,
        )
    }
}
