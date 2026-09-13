package com.pramodpatel.platformreset.design

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight

/**
 * IBM Plex Sans and IBM Plex Mono, the two typefaces the approved mockups specify (headings/body
 * in Plex Sans, labels/metadata/code in Plex Mono). Real static .ttf files, downloaded from IBM's
 * own https://github.com/IBM/plex (SIL Open Font License 1.1) -- see /licenses/OFL-IBM-Plex.txt at
 * the repo root for the license text and the root README for attribution.
 */
val PlexSans = FontFamily(
    Font(R.font.ibmplexsans_regular, FontWeight.Normal),
    Font(R.font.ibmplexsans_medium, FontWeight.Medium),
    Font(R.font.ibmplexsans_semibold, FontWeight.SemiBold),
    Font(R.font.ibmplexsans_bold, FontWeight.Bold),
)

val PlexMono = FontFamily(
    Font(R.font.ibmplexmono_regular, FontWeight.Normal),
    Font(R.font.ibmplexmono_medium, FontWeight.Medium),
)
