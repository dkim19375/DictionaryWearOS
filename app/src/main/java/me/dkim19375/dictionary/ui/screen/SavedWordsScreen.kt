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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.google.android.horologist.annotations.ExperimentalHorologistApi
import com.google.android.horologist.compose.layout.ScalingLazyColumn
import com.google.android.horologist.compose.layout.ScreenScaffold
import com.google.android.horologist.compose.layout.rememberColumnState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dkim19375.dictionary.data.room.AppDatabase
import me.dkim19375.dictionary.data.room.WordSQL
import me.dkim19375.dictionary.ui.component.AutoSizeText
import me.dkim19375.dictionary.ui.component.ChipWithEmphasizedText
import me.dkim19375.dictionary.ui.component.ChipWithHeading
import me.dkim19375.dictionary.util.MAIN_SCOPE
import me.dkim19375.dictionary.util.capitalize

@OptIn(ExperimentalHorologistApi::class)
@Composable
fun SavedWordsScreen(
    words: List<String>,
    wordSelected: () -> Unit,
    wordLoaded: (String, WordSQL?) -> Unit,
) {
    val columnState = rememberColumnState()
    var context = LocalContext.current
    var newWords by remember(words) { mutableStateOf(words) }
    LaunchedEffect(null) {
        val updatedWords = withContext(Dispatchers.IO) {
            AppDatabase.getInstance(context).wordDao().getSavedWords()
        }
        if (newWords != updatedWords) {
            newWords = updatedWords
        }
    }
    ScreenScaffold(scrollState = columnState) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            columnState = columnState,
        ) {
            item(key = "title") {
                Text(
                    text = "Saved Words",
                    style = MaterialTheme.typography.title2,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
            if (newWords.isNotEmpty()) {
                items(newWords) { word ->
                    context = LocalContext.current
                    ChipWithEmphasizedText(
                        modifier = Modifier
                            .heightIn(max = 42.dp)
                            .padding(horizontal = 4.dp),
                        text = word.capitalize(),
                        customLabel = {
                            AutoSizeText(
                                text = word.capitalize(),
                                style = MaterialTheme.typography.caption1,
                                maxTextSize = MaterialTheme.typography.caption1.fontSize,
                                minTextSize = 5.sp,
                                minTextSizeBeforeNewline = 12.sp,
                                modifier = Modifier.padding(start = 4.dp, end = 2.dp)
                            )
                        }
                    ) {
                        wordSelected()
                        MAIN_SCOPE.launch {
                            val sqlData = withContext(Dispatchers.IO) {
                                AppDatabase.getInstance(context).wordDao()
                                    .getSavedWordWithData(word)
                            }
                            if (sqlData == null) {
                                withContext(Dispatchers.IO) {
                                    AppDatabase.getInstance(context).wordDao()
                                        .unSaveWordWithoutUnSavingMeaningsOrDefinitions(word)
                                }
                            }
                            wordLoaded(word, sqlData)
                        }
                    }
                }
                item("info") {
                    Text(
                        text = "All saved words are available offline",
                        style = MaterialTheme.typography.caption3,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                item("info") {
                    ChipWithHeading(
                        heading = "Save some words!",
                        text = "(available offline)",
                    )
                }
            }
        }
    }
}