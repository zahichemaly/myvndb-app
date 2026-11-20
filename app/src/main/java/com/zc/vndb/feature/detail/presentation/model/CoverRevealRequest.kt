package com.zc.vndb.feature.detail.presentation.model

import androidx.compose.runtime.Immutable

/**
 * The 18+ question about a blurred cover, open until it is answered. A cover opened from the
 * carousel goes on to the full-screen viewer at [viewerIndex] once it is confirmed; the page's own
 * cover (null) is just shown.
 */
@Immutable
data class CoverRevealRequest(val viewerIndex: Int?)
