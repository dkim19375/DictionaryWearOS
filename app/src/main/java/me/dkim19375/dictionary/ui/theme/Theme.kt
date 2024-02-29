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

package me.dkim19375.dictionary.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import me.dkim19375.dictionary.R

@Composable
fun DictionaryTheme(
    content: @Composable () -> Unit,
) {
    val titilliumWebFontFamily = FontFamily(
        Font(R.font.titilliumweb_extralight, FontWeight.ExtraLight),
        Font(R.font.titilliumweb_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(R.font.titilliumweb_light, FontWeight.Light),
        Font(R.font.titilliumweb_lightitalic, FontWeight.Light, FontStyle.Italic),
        Font(R.font.titilliumweb_regular, FontWeight.Normal),
        Font(R.font.titilliumweb_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.titilliumweb_semibold, FontWeight.SemiBold),
        Font(R.font.titilliumweb_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(R.font.titilliumweb_bold, FontWeight.Bold),
        Font(R.font.titilliumweb_bolditalic, FontWeight.Bold, FontStyle.Italic),
        Font(R.font.titilliumweb_black, FontWeight.Black),
    )
    MaterialTheme(
        colors = MaterialTheme.colors.copy(
            background = Color.Black,
            onBackground = Color(0xFF9696A0),
            surface = Color(0xFF313133),
            primary = Color(0xFF3E91FF),
            onPrimary = Color.White,
        ),
        typography = MaterialTheme.typography.copy(
            title2 = MaterialTheme.typography.title2.copy(
                color = Color.LightGray,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = titilliumWebFontFamily,
            ),
            caption1 = MaterialTheme.typography.caption1.copy(
                color = Color(0xEEEEEEFF),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                fontFamily = titilliumWebFontFamily,
            ),
            caption2 = MaterialTheme.typography.caption2.copy(
                color = Color.LightGray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                fontFamily = titilliumWebFontFamily,
            ),
            caption3 = MaterialTheme.typography.caption3.copy(
                color = Color.Gray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                fontFamily = titilliumWebFontFamily,
            ),
            body2 = MaterialTheme.typography.body2.copy(
                color = Color(0xFFDEDEDE),
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Start,
                fontFamily = titilliumWebFontFamily,
            ),
            button = MaterialTheme.typography.button.copy(
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontFamily = titilliumWebFontFamily,
            ),
        ),
        content = content
    )
}