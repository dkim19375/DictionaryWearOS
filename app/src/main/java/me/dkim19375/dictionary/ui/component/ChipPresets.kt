/*
 * MIT License
 *
 * Copyright (c) 2024 dkim19375
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.dkim19375.dictionary.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.LocalTextStyle
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.ripple

@Composable
fun ChipWithHeading(
    modifier: Modifier = Modifier,
    heading: String,
    text: String,
    onClick: () -> Unit = {},
) = ChipWithHeading(
    modifier = modifier,
    heading = heading,
    text = AnnotatedString(text),
    onClick = onClick,
)

@Composable
fun ChipWithHeading(
    modifier: Modifier = Modifier,
    heading: String,
    text: AnnotatedString,
    onClick: () -> Unit = {},
) = TemplateChip(
    modifier = modifier,
    label = { Text(text = heading, style = MaterialTheme.typography.caption2) },
    secondaryLabel = { Text(text = text, style = MaterialTheme.typography.body2) },
    onClick = onClick,
)

@Composable
fun ChipWithEmphasizedText(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector? = null,
    iconSize: Dp = 24.dp,
    customLabel: (@Composable RowScope.() -> Unit)? = null,
    onClick: () -> Unit = {},
) = TemplateChip(
    modifier = modifier,
    label = customLabel ?: { Text(text, style = MaterialTheme.typography.caption1) },
    icon = icon?.let {
        {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.typography.caption1.color,
                modifier = Modifier.height(iconSize),
            )
        }
    },
    onClick = onClick,
)

@Composable
fun ChipWithRegularText(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit = {},
) = TemplateChip(
    modifier = modifier,
    label = { Text(text = text, style = MaterialTheme.typography.body2) },
    onClick = onClick,
)

@Composable
private fun TemplateChip(
    modifier: Modifier = Modifier,
    label: @Composable RowScope.() -> Unit,
    secondaryLabel: (@Composable RowScope.() -> Unit)? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    colors: ChipColors = ChipDefaults.chipColors(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = if (secondaryLabel != null) {
            MaterialTheme.colors.onSurfaceVariant
        } else {
            MaterialTheme.colors.onSurface
        },
        secondaryContentColor = MaterialTheme.colors.onSurfaceVariant,
        iconColor = MaterialTheme.colors.onSurface,
    ),
    role: Role? = Role.Button,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) = BaseChip(
    modifier = modifier,
    onClick = onClick,
    colors = colors,
    role = role,
    enabled = enabled
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier.wrapContentSize(align = Alignment.Center),
            ) {
                val color = colors.iconColor(enabled = enabled).value
                CompositionLocalProvider(
                    LocalContentColor provides color,
                    LocalContentAlpha provides color.alpha,
                ) {
                    icon()
                }
                Spacer(modifier = Modifier.size(width = 30.dp, height = 0.dp))
            }
        }
        Column {
            Row {
                val color = colors.contentColor(enabled).value
                CompositionLocalProvider(
                    LocalContentColor provides color,
                    LocalContentAlpha provides color.alpha,
                    LocalTextStyle provides MaterialTheme.typography.button,
                ) {
                    label()
                }
            }
            secondaryLabel?.let {
                Row {
                    val color = colors.secondaryContentColor(enabled).value
                    CompositionLocalProvider(
                        LocalContentColor provides color,
                        LocalContentAlpha provides color.alpha,
                        LocalTextStyle provides MaterialTheme.typography.caption2,
                    ) {
                        secondaryLabel()
                    }
                }
            }
        }
    }
}

@Composable
private fun BaseChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    colors: ChipColors,
    role: Role? = Role.Button,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit,
) = Box(
    modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 1.dp)
        .defaultMinSize(minHeight = 52.dp)
        .clip(RoundedCornerShape(15.dp))
        .paint(colors.background(enabled = enabled).value, contentScale = ContentScale.Crop)
        .clickable(
            onClick = onClick,
            enabled = enabled,
            role = role,
            indication = ripple(),
            interactionSource = remember { MutableInteractionSource() }
        )
        .padding(horizontal = 10.dp, vertical = 5.dp),
    contentAlignment = Alignment.CenterStart,
) {
    val color = colors.contentColor(enabled).value
    CompositionLocalProvider(
        LocalContentColor provides color,
        LocalContentAlpha provides color.alpha,
        LocalTextStyle provides MaterialTheme.typography.button,
    ) {
        content()
    }
}

/*
@Composable
private fun OldChip(
    modifier: Modifier = Modifier,
    label: @Composable RowScope.() -> Unit,
    secondaryLabel: (@Composable RowScope.() -> Unit)? = null,
    icon: (@Composable BoxScope.() -> Unit)? = null,
    onClick: () -> Unit = {},
) = Chip(
    modifier = modifier
        .fillMaxWidth()
        .padding(vertical = 1.dp),
    label = label,
    secondaryLabel = secondaryLabel,
    icon = icon,
    colors = ChipDefaults.chipColors(
        backgroundColor = MaterialTheme.colors.surface,
        contentColor = if (secondaryLabel != null) {
            MaterialTheme.colors.onSurfaceVariant
        } else {
            MaterialTheme.colors.onSurface
        },
        secondaryContentColor = MaterialTheme.colors.onSurfaceVariant,
        iconColor = MaterialTheme.colors.onSurface,
    ),
    shape = RoundedCornerShape(20.dp),
    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp),
    onClick = onClick,
)
*/
