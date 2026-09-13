package dev.promode7.adaptive17.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pramodpatel.platformreset.design.DetailBreadcrumb

/**
 * The Adaptive Layouts feature screen used by :app's navigation. Unlike the other four features,
 * this one's "live demo" -- the real single-pane/two-pane articles reader -- IS the feature under
 * demonstration, so it is given the whole remaining screen (via [AdaptiveArticlesApp], unmodified
 * from the standalone `android17-adaptive-layouts` repo) rather than being squeezed under a full
 * mockup-style detail chrome. [onBack] is only shown when this screen is reached via phone
 * navigation (see MainActivity's NavHost); the tablet dogfooding pane omits it entirely, matching
 * TabletHome.dc.html, where the detail pane has no back affordance at all.
 */
@Composable
fun AdaptiveFeatureScreen(onBack: (() -> Unit)? = null) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (onBack != null) {
            DetailBreadcrumb(
                breadcrumb = "Android 17 / Adaptive Layouts",
                onBack = onBack,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
            )
        }
        Box(modifier = Modifier.weight(1f)) {
            AdaptiveArticlesApp()
        }
    }
}
