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

package me.dkim19375.dictionary.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import me.dkim19375.dictionary.ui.component.AutoSizeText
import me.dkim19375.dictionary.util.capitalize

@Composable
fun WordNotFoundScreen(
    word: String,
    backToHome: () -> Unit,
) = Column(
    modifier = Modifier
        .fillMaxSize()
        .padding(vertical = 25.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.SpaceEvenly,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (word.isNotBlank()) {
                "Word not found:"
            } else {
                "Please enter a word"
            },
            style = MaterialTheme.typography.title2,
        )
        if (word.isNotBlank()) {
            Box(
                modifier = Modifier.padding(horizontal = 5.dp)
            ) {
                AutoSizeText(
                    text = word.capitalize(),
                    style = MaterialTheme.typography.title2,
                    maxTextSize = MaterialTheme.typography.title2.fontSize,
                    minTextSize = 12.sp,
                    minTextSizeBeforeNewline = 16.sp,
                )
            }
        }
    }
    Button(
        modifier = Modifier
            .height(30.dp)
            .width(72.dp),
        colors = ButtonDefaults.primaryButtonColors(contentColor = Color.Red),
        shape = RoundedCornerShape(60),
        onClick = backToHome,
    ) {
        Text(text = "Home", style = MaterialTheme.typography.button)
    }
}