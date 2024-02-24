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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dkim19375.dictionary.data.WordData
import me.dkim19375.dictionary.data.room.AppDatabase
import me.dkim19375.dictionary.util.MAIN_SCOPE

@Composable
fun ToggleSavedChip(
    modifier: Modifier = Modifier,
    wordData: WordData,
    isSaved: () -> Boolean,
    setSaved: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    ChipWithEmphasizedText(
        text = if (isSaved()) "Saved" else "Save",
        icon = if (isSaved()) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
        iconSize = 23.dp,
        modifier = modifier,
    ) {
        MAIN_SCOPE.launch {
            withContext(Dispatchers.IO) {
                val dao = AppDatabase.getInstance(context).wordDao()
                if (!isSaved()) {
                    dao.saveWord(wordData.toSQL())
                } else {
                    dao.getSavedWordWithData(wordData.word)?.let(dao::unSaveWord)
                }
            }
            setSaved(!isSaved())
        }
    }
}