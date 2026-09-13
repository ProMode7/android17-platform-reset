package com.pramodpatel.platformreset

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import com.pramodpatel.notesfunctions.ui.NotesScreen
import com.pramodpatel.platformreset.design.PlatformResetColors
import com.promode.android17hardening.ui.SecurityScreen
import com.promode.npu.ui.NpuScreen
import dev.promode7.adaptive17.ui.AdaptiveFeatureScreen
import dev.promode7.adaptive17.ui.LayoutMode
import dev.promode7.adaptive17.ui.pickLayoutMode
import dev.pramodpatel.handoff.ui.HandoffScreen

/**
 * The app's root composable. Reads the real current [WindowSizeClass] (via
 * `currentWindowAdaptiveInfoV2()`, the same androidx.window/material3-adaptive API
 * `:feature-adaptive` uses for its own list/detail reader) and reuses that module's
 * [pickLayoutMode] decision function directly, rather than re-implementing a size-class check
 * here: on a phone-width window the home dashboard is ordinary single-destination navigation; on
 * a medium-width-or-wider window (an unfolded foldable, a tablet, a resizable/freeform desktop
 * window) the home dashboard itself becomes a list+detail split, self-referentially dogfooding
 * the Adaptive Layouts feature this app also demonstrates as a standalone screen.
 */
@Composable
fun PlatformResetApp(
    windowSizeClass: WindowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass,
) {
    Surface(modifier = Modifier.fillMaxSize(), color = PlatformResetColors.Background) {
        when (pickLayoutMode(windowSizeClass)) {
            LayoutMode.TwoPane -> TwoPaneDashboard()
            LayoutMode.SinglePane -> PhoneNavHost()
        }
    }
}

/** Compact-width layout: ordinary single-destination Navigation Compose. */
@Composable
private fun PhoneNavHost() {
    val navController = rememberNavController()
    val requestedFeatureId by NavigationSignal.requestedFeatureId.collectAsState()
    LaunchedEffect(requestedFeatureId) {
        val id = requestedFeatureId ?: return@LaunchedEffect
        navController.navigate(id)
        NavigationSignal.requestedFeatureId.value = null
    }
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeDashboard(onFeatureClick = { id -> navController.navigate(id) })
        }
        composable(FeatureCatalog.APPFUNCTIONS) {
            NotesScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureCatalog.ADAPTIVE) {
            AdaptiveFeatureScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureCatalog.HANDOFF) {
            HandoffScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureCatalog.NPU) {
            NpuScreen(onBack = { navController.popBackStack() })
        }
        composable(FeatureCatalog.SECURITY) {
            SecurityScreen(onBack = { navController.popBackStack() })
        }
    }
}

/**
 * Medium-width-and-up layout: a 420dp feature rail (identical row content to [HomeDashboard])
 * beside a detail pane showing the selected feature's real screen inline -- exactly
 * TabletHome.dc.html's list+detail split, and exactly what `:feature-adaptive`'s own
 * `AdaptiveArticlesApp` does for its articles, just one level up at the app's own navigation.
 */
@Composable
private fun TwoPaneDashboard() {
    var selectedFeatureId by rememberSaveable { mutableStateOf(FeatureCatalog.items.first().id) }

    val requestedFeatureId by NavigationSignal.requestedFeatureId.collectAsState()
    LaunchedEffect(requestedFeatureId) {
        val id = requestedFeatureId ?: return@LaunchedEffect
        selectedFeatureId = id
        NavigationSignal.requestedFeatureId.value = null
    }

    Row(modifier = Modifier.fillMaxSize()) {
        FeatureRail(
            selectedFeatureId = selectedFeatureId,
            onFeatureClick = { selectedFeatureId = it },
            modifier = Modifier
                .width(420.dp)
                .fillMaxHeight(),
        )
        HorizontalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = PlatformResetColors.DividerRail,
        )
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            color = PlatformResetColors.Background,
        ) {
            when (selectedFeatureId) {
                FeatureCatalog.APPFUNCTIONS -> NotesScreen()
                FeatureCatalog.ADAPTIVE -> AdaptiveFeatureScreen()
                FeatureCatalog.HANDOFF -> HandoffScreen()
                FeatureCatalog.NPU -> NpuScreen()
                FeatureCatalog.SECURITY -> SecurityScreen()
            }
        }
    }
}
