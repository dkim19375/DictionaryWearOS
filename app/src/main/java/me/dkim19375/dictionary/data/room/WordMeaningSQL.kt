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

package me.dkim19375.dictionary.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import me.dkim19375.dictionary.enumclass.PartOfSpeech

@Entity(tableName = "meanings")
data class WordMeaningSQL(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    val word: String,
    @ColumnInfo(name = "meaning_index")
    val meaningIndex: Int,
    @ColumnInfo(name = "part_of_speech")
    val partOfSpeech: PartOfSpeech,
    val synonyms: List<String> = emptyList(),
    val antonyms: List<String> = emptyList(),
    @Ignore
    val definitions: List<WordDefinitionSQL> = emptyList(),
) {
    constructor(
        word: String,
        meaningIndex: Int,
        partOfSpeech: PartOfSpeech,
        synonyms: List<String>,
        antonyms: List<String>,
    ) : this(null, word, meaningIndex, partOfSpeech, synonyms, antonyms, emptyList())
}