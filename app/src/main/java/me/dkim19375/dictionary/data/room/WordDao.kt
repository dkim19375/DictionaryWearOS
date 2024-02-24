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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
abstract class WordDao {
    @Query("SELECT * FROM words")
    // intentionally long to prevent accidental use :)
    abstract fun getSavedWordsWithoutDefinitionsOrMeanings(): List<WordSQL>

    @Transaction
    @Query("")
    fun getSavedWordsWithData(): List<WordSQL> =
        getSavedWordsWithoutDefinitionsOrMeanings().map { word ->
            word.copy(
                meanings = getUnsortedSavedMeaningsWithoutDefinitions(word.word).map { meaning ->
                    meaning.id?.let { meaningID ->
                        meaning.copy(definitions = getUnsortedSavedDefinitions(meaningID))
                    } ?: meaning
                }
            ).getSortedMeaningsAndDefinitions()
        }

    @Query("SELECT word FROM words")
    abstract fun getSavedWords(): List<String>

    @Query("SELECT * FROM meanings WHERE word = :word")
    abstract fun getUnsortedSavedMeaningsWithoutDefinitions(word: String): List<WordMeaningSQL>

    @Query("SELECT * FROM definitions WHERE parent_meaning = :meaningID")
    abstract fun getUnsortedSavedDefinitions(meaningID: Long): List<WordDefinitionSQL>

    @Query("SELECT * FROM words WHERE word = :word")
    abstract fun getSavedWordWithoutDefinitionsOrMeanings(word: String): WordSQL?

    @Transaction
    @Query("")
    fun getSavedWordWithData(word: String): WordSQL? =
        getSavedWordWithoutDefinitionsOrMeanings(word)?.let { wordSQL ->
            wordSQL.copy(
                meanings = getUnsortedSavedMeaningsWithoutDefinitions(wordSQL.word).map { meaning ->
                    meaning.id?.let { meaningID ->
                        meaning.copy(definitions = getUnsortedSavedDefinitions(meaningID))
                    } ?: meaning
                }
            ).getSortedMeaningsAndDefinitions()
        }

    @Query("SELECT EXISTS(SELECT * FROM words WHERE word = :word)")
    abstract fun isWordSaved(word: String): Boolean

    @Query("DELETE FROM words WHERE word = :word")
    abstract fun unSaveWordWithoutUnSavingMeaningsOrDefinitions(word: String)

    @Query("DELETE FROM meanings WHERE word = :word")
    abstract fun unSaveMeaningsWithoutUnSavingDefinitions(word: String)

    @Query("DELETE FROM definitions WHERE parent_meaning = :meaningID")
    abstract fun unSaveDefinitions(meaningID: Long)

    @Transaction
    @Query("")
    fun unSaveWord(word: WordSQL) {
        unSaveWordWithoutUnSavingMeaningsOrDefinitions(word.word)
        unSaveMeaningsWithoutUnSavingDefinitions(word.word)
        word.meanings.forEach {
            it.id?.let(::unSaveDefinitions)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveWordWithoutMeaningsAndDefinitions(word: WordSQL)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveMeaningsWithoutDefinitions(meaning: List<WordMeaningSQL>): Array<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun saveDefinitions(definition: List<WordDefinitionSQL>)

    @Query("SELECT id FROM meanings WHERE rowid = :rowId")
    abstract fun getMeaningIDFromRowID(rowId: Long): Long?

    @Transaction
    @Query("")
    fun saveWord(word: WordSQL) {
        saveWordWithoutMeaningsAndDefinitions(word)
        val rowIDs = saveMeaningsWithoutDefinitions(word.meanings)
        for ((i, definitions) in word.meanings.map(WordMeaningSQL::definitions).withIndex()) {
            val id = getMeaningIDFromRowID(rowIDs[i]) ?: continue
            saveDefinitions(definitions.map { it.copy(parentMeaning = id) })
        }
    }
}