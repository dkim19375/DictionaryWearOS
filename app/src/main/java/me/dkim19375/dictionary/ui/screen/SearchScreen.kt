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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.dkim19375.dictionary.data.WordData
import me.dkim19375.dictionary.data.room.AppDatabase
import me.dkim19375.dictionary.ui.component.AutoSizeText
import me.dkim19375.dictionary.ui.component.ChipWithEmphasizedText
import me.dkim19375.dictionary.ui.component.ToggleSavedChip
import me.dkim19375.dictionary.util.capitalize

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SearchScreen(wordData: WordData, meaningSelected: (Int, isSaved: Boolean) -> Unit) {
    val columnState = rememberColumnState()

    var isSaved by remember(wordData) {
        mutableStateOf(wordData.isSaved)
    }
    val context = LocalContext.current
    LaunchedEffect(null) {
        val newIsSaved = withContext(Dispatchers.IO) {
            AppDatabase.getInstance(context).wordDao().isWordSaved(wordData.word)
        }
        if (newIsSaved != isSaved) {
            isSaved = newIsSaved
        }
    }

    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            columnState = columnState,
        ) {
            item(key = "word") {
                Box(
                    modifier = Modifier.padding(horizontal = 23.dp)
                ) {
                    AutoSizeText(
                        text = wordData.word.capitalize(),
                        style = MaterialTheme.typography.title2.copy(textAlign = TextAlign.Center),
                        maxTextSize = MaterialTheme.typography.title2.fontSize,
                        minTextSize = 5.sp,
                        minTextSizeBeforeNewline = 14.sp,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
            }
            items(wordData.meanings.size) {
                val meaning = wordData.meanings[it]
                ChipWithEmphasizedText(
                    modifier = Modifier
                        .heightIn(max = 42.dp)
                        .padding(horizontal = 4.dp),
                    text = "${" ".repeat(4)}${meaning.partOfSpeech.name.capitalize()}"
                ) {
                    meaningSelected(it, isSaved)
                }
            }
            item("save") {
                ToggleSavedChip(
                    modifier = Modifier
                        .heightIn(max = 42.dp)
                        .padding(horizontal = 4.dp),
                    wordData = wordData,
                    isSaved = { isSaved }
                ) { isSaved = it }
            }
        }
    }
}