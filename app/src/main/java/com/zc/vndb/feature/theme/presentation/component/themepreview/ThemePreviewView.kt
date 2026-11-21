package com.zc.vndb.feature.theme.presentation.component.themepreview

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import com.zc.vndb.common.domain.model.AppTheme
import com.zc.vndb.common.ui.resource.StringResource
import com.zc.vndb.common.ui.text.UiText
import com.zc.vndb.common.ui.theme.AngelicSerenadeThemeSpec
import com.zc.vndb.common.ui.theme.OriginalThemeSpec
import com.zc.vndb.common.ui.theme.VndbTheme
import com.zc.vndb.common.ui.theme.cardBackground
import com.zc.vndb.common.ui.util.AdaptivePreviews
import com.zc.vndb.feature.theme.presentation.component.themepreview.model.ThemeUIModel

/** The wallpaper's own 1080 x 2400 ratio, so the preview shows all of it, as the real screen does. */
private const val PREVIEW_ASPECT_RATIO = 9f / 20f
private val SwatchSize = 18.dp
private val NavDotSize = 8.dp
private val ScoreBadgeWidth = 24.dp
private val ScoreBadgeHeight = 10.dp

/** The check icon's size, kept as the name row's minimum height so selecting a card never shifts the layout. */
private val CheckIconSize = 24.dp
private const val NAV_DOT_COUNT = 3

/** Opacity of the content inside a mock card over a wallpaper, so the art shows through it slightly. */
private const val MOCK_CONTENT_ALPHA = 0.6f

/** The mock text lines are softer than the real text. */
private const val LINE_ALPHA = 0.7f

/**
 * A theme as a small phone: its wallpaper and real tokens drawn under that theme, so it can never
 * drift from the theme itself. Tapping selects it. Below the phone: four swatches, then the name (which may wrap).
 */
@Composable
fun ThemePreviewView(model: ThemeUIModel, onSelect: () -> Unit, modifier: Modifier = Modifier) {
    val shape = MaterialTheme.shapes.large
    val borderColor = if (model.isSelected) VndbTheme.colors.accent else VndbTheme.colors.borderSubtle
    val borderWidth = if (model.isSelected) VndbTheme.dimens.focusRingWidth else VndbTheme.dimens.borderWidth
    Column(
        modifier = modifier.selectable(
            selected = model.isSelected,
            role = Role.RadioButton,
            onClick = onSelect,
        ),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
    ) {
        PhoneMock(
            model = model,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(PREVIEW_ASPECT_RATIO)
                .clip(shape)
                .border(borderWidth, borderColor, shape),
        )
        VndbTheme(model.spec) {
            Row(
                modifier = Modifier.clearAndSetSemantics {},
                horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
            ) {
                Swatch(MaterialTheme.colorScheme.background)
                Swatch(VndbTheme.colors.surfaceCard)
                Swatch(VndbTheme.colors.accent)
                Swatch(VndbTheme.colors.score)
            }
        }
        Row(
            modifier = Modifier.heightIn(min = CheckIconSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
        ) {
            Text(
                text = model.name.asString(),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (model.isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = UiText.StringResource(
                        StringResource.theme_selected,
                    ).asString(),
                    tint = VndbTheme.colors.accent,
                )
            }
        }
    }
}

@Composable
private fun Swatch(color: Color) {
    Box(
        modifier = Modifier
            .size(SwatchSize)
            .clip(CircleShape)
            .background(color)
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, CircleShape),
    )
}

/** Decorative: hidden from accessibility; the name below describes it. */
@Composable
private fun PhoneMock(model: ThemeUIModel, modifier: Modifier = Modifier) {
    VndbTheme(model.spec) {
        Box(
            modifier = modifier
                .clearAndSetSemantics {}
                .background(MaterialTheme.colorScheme.background),
        ) {
            VndbTheme.wallpaper?.let { wallpaper ->
                Image(
                    painter = painterResource(wallpaper.image),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = wallpaper.alpha,
                )
            }
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(VndbTheme.spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.sm),
                ) {
                    Text(
                        text = UiText.StringResource(StringResource.app_name).asString(),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    MockCard(isTall = true)
                    MockCard(isTall = false)
                }
                MockNavBar()
            }
        }
    }
}

@Composable
private fun MockCard(isTall: Boolean) {
    val shape = MaterialTheme.shapes.medium
    // Over a wallpaper the card's content is see-through, so the small preview still shows the art.
    // Solid on plain themes, where there is nothing behind it.
    val alpha = if (VndbTheme.wallpaper != null) MOCK_CONTENT_ALPHA else 1f
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .cardBackground()
            .border(VndbTheme.dimens.borderWidth, VndbTheme.colors.borderSubtle, shape)
            .padding(VndbTheme.spacing.sm),
        verticalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
    ) {
        if (isTall) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(VndbTheme.colors.surfaceElevated.copy(alpha = alpha)),
            ) {
                // The score color, as the rating badge on a cover.
                ScoreBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(VndbTheme.spacing.xs),
                )
            }
        }
        Line(
            widthFraction = 0.7f,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = LINE_ALPHA * alpha),
        )
        Line(
            widthFraction = 0.45f,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = LINE_ALPHA * alpha),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MockChip(VndbTheme.colors.chipNeutral.container.copy(alpha = alpha))
            MockChip(VndbTheme.colors.chipNeutral.container.copy(alpha = alpha))
            if (!isTall) {
                Spacer(Modifier.weight(1f))
                ScoreBadge()
            }
        }
        if (isTall) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
                    .clip(MaterialTheme.shapes.small)
                    // Solid, so the accent in the preview is the accent swatch's exact color.
                    .background(VndbTheme.colors.accent),
            )
        }
    }
}

/** A small pill in the theme's score color. Solid, so it matches the score swatch exactly. */
@Composable
private fun ScoreBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(ScoreBadgeWidth)
            .height(ScoreBadgeHeight)
            .clip(CircleShape)
            .background(VndbTheme.colors.score),
    )
}

@Composable
private fun Line(widthFraction: Float, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(5.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun MockChip(color: Color) {
    Box(
        modifier = Modifier
            .width(22.dp)
            .height(8.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun MockNavBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(VndbTheme.colors.surfaceInset)
            .padding(vertical = VndbTheme.spacing.sm),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        repeat(NAV_DOT_COUNT) { index ->
            Box(
                modifier = Modifier
                    .size(NavDotSize)
                    .clip(CircleShape)
                    .background(
                        if (index == 0) {
                            VndbTheme.colors.accent
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    ),
            )
        }
    }
}

@AdaptivePreviews
@Composable
private fun ThemePreviewViewPreview() {
    VndbTheme {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(VndbTheme.spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(VndbTheme.spacing.lg),
        ) {
            ThemePreviewView(
                model = ThemeUIModel(
                    AppTheme.Original,
                    UiText.StringValue("Original"),
                    OriginalThemeSpec,
                    isSelected = true,
                ),
                onSelect = {},
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
            ThemePreviewView(
                model = ThemeUIModel(
                    AppTheme.AngelicSerenade,
                    UiText.StringValue("Angelic Serenade"),
                    AngelicSerenadeThemeSpec,
                    isSelected = false,
                ),
                onSelect = {},
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )
        }
    }
}
