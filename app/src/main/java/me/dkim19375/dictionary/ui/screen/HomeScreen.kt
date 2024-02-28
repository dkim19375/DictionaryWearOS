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

import android.app.RemoteInput
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.ExperimentalWearFoundationApi
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.foundation.rememberActiveFocusRequester
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.input.RemoteInputIntentHelper
import androidx.wear.input.wearableExtender
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dkim19375.dictionary.data.room.AppDatabase
import me.dkim19375.dictionary.ui.component.ChipWithEmphasizedText

@OptIn(ExperimentalWearFoundationApi::class)
@Composable
fun HomeScreen(
    noInputText: () -> Unit,
    searchInputReceived: (inputText: String) -> Unit,
    savedButtonPressed: () -> Unit,
    savedWordsLoaded: (List<String>) -> Unit,
) {
    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = 0,
        initialCenterItemScrollOffset = 120,
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
        item(key = "title") {
            Text(
                text = "Dictionary",
                style = MaterialTheme.typography.title2,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
        item(key = "search") {
            val launcher = rememberLauncherForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) launcher@{ result ->
                val intentData = result.data ?: return@launcher
                val results: Bundle = RemoteInput.getResultsFromIntent(intentData)
                val newInputText = results.getCharSequence("input_text")
                val inputText = newInputText?.toString()?.trim()?.takeIf(String::isNotBlank)
                if (inputText == null) {
                    noInputText()
                    return@launcher
                }
                searchInputReceived(inputText)
            }
            val intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
            val remoteInputs: List<RemoteInput> = listOf(
                RemoteInput.Builder("input_text")
                    .setLabel("Dictionary Search")
                    .wearableExtender {
                        setEmojisAllowed(false)
                        setInputActionType(EditorInfo.IME_ACTION_SEARCH)
                    }.build()
            )
            RemoteInputIntentHelper.putRemoteInputsExtra(intent, remoteInputs)

            ChipWithEmphasizedText(
                modifier = Modifier.heightIn(max = 48.dp),
                text = "Search",
                icon = Icons.Rounded.Search,
                onClick = {
                    launcher.launch(intent)
                },
            )
        }
        item(key = "saved") {
            val context = LocalContext.current
            ChipWithEmphasizedText(
                modifier = Modifier.heightIn(max = 48.dp),
                text = "Saved",
                icon = Icons.Rounded.Bookmarks,
                iconSize = 23.dp,
            ) {
                savedButtonPressed()
                MAIN_SCOPE.launch {
                    runCatching {
                        val savedWords = withContext(Dispatchers.IO) {
                            AppDatabase.getInstance(context).wordDao().getSavedWords()
                        }
                        withContext(Dispatchers.Main) {
                            savedWordsLoaded(savedWords)
                        }
                    }.exceptionOrNull()?.printStackTrace()
                }
            }
        }
    }
}