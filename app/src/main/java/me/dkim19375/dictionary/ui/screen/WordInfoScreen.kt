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

import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dkim19375.dictionary.data.WordData
import me.dkim19375.dictionary.data.WordMeaningJsonData
import me.dkim19375.dictionary.data.room.AppDatabase
import me.dkim19375.dictionary.ui.component.AutoSizeText
import me.dkim19375.dictionary.ui.component.ChipWithHeading
import me.dkim19375.dictionary.ui.component.ToggleSavedChip
import me.dkim19375.dictionary.util.capitalize

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun WordInfoScreen(wordData: WordData, meaning: WordMeaningJsonData) {
    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = 0,
        initialCenterItemScrollOffset = 115,
    )
    val coroutineScope = rememberCoroutineScope()

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .onRotaryScrollEvent {
                coroutineScope.launch {
                    listState.animateScrollBy(it.verticalScrollPixels, tween())
                }
                true
            }
            .focusRequester(rememberActiveFocusRequester())
            .focusable(),
        horizontalAlignment = Alignment.CenterHorizontally,
        state = listState,
    ) {
        item(key = "word") {
            Box(
                modifier = Modifier.padding(horizontal = 23.dp)
            ) {
                AutoSizeText(
                    text = "${wordData.word.capitalize()} (${meaning.partOfSpeech.abbreviation}.)",
                    style = MaterialTheme.typography.title2,
                    maxTextSize = MaterialTheme.typography.title2.fontSize,
                    minTextSize = 5.sp,
                    minTextSizeBeforeNewline = 12.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                )
            }
        }
        if (wordData.phonetics.isNotEmpty()) {
            item(key = "ipa") {
                ChipWithHeading(
                    heading = "IPA${if (wordData.phonetics.size == 1) "" else "s"}:",
                    text = wordData.phonetics.joinToString("\n")
                )
            }
        }
        itemsIndexed(meaning.definitions) { i, definition ->
            ChipWithHeading(
                heading = "Definition ${i + 1}:",
                text = definition.definition,
            )
        }
        if (meaning.synonyms.isNotEmpty()) {
            item("Synonyms") {
                ChipWithHeading(
                    heading = "Synonyms:",
                    text = meaning.synonyms.joinToString(", "),
                )
            }
        }
        if (meaning.antonyms.isNotEmpty()) {
            item("Antonyms") {
                ChipWithHeading(
                    heading = "Antonyms:",
                    text = meaning.antonyms.joinToString(", "),
                )
            }
        }
        /*if (wordData.licenses.isNotEmpty()) {
            item("licenses") {
                ChipWithHeading(
                    heading = "Licenses:",
                    text = wordData.licenses.joinToString(
                        separator = "\n",
                        transform = WordLicenseJsonData::name
                    ),
                )
            }
        }*/
        /*if (wordData.sourceUrls.isNotEmpty()) {
            item("sourceUrls") {
                ChipWithHeading(
                    heading = "Sources:",
                    text = wordData.sourceUrls.joinToString("\n"),
                )
            }
        }*/
        item("save") {
            var isSaved by remember(wordData) { mutableStateOf(wordData.isSaved) }
            val context = LocalContext.current
            LaunchedEffect(null) {
                val newIsSaved = withContext(Dispatchers.IO) {
                    AppDatabase.getInstance(context).wordDao().isWordSaved(wordData.word)
                }
                if (newIsSaved != isSaved) {
                    isSaved = newIsSaved
                }
            }
            ToggleSavedChip(
                wordData = wordData,
                isSaved = { isSaved }
            ) { isSaved = it }
        }
        item("credits") {
            Text(
                text = "Powered by, but not associated with dictionaryapi.dev",
                style = MaterialTheme.typography.caption3,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}