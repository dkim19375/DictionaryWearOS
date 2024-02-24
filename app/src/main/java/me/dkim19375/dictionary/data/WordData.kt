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

package me.dkim19375.dictionary.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromHexString
import kotlinx.serialization.encodeToHexString
import me.dkim19375.dictionary.data.room.WordDefinitionSQL
import me.dkim19375.dictionary.data.room.WordMeaningSQL
import me.dkim19375.dictionary.data.room.WordSQL
import me.dkim19375.dictionary.util.experimentalSerializationApi

@Serializable
data class WordData(
    val word: String,
    val phonetics: List<String> = emptyList(),
    val meanings: List<WordMeaningJsonData>,
    val licenses: List<WordLicenseJsonData> = emptyList(),
    val sourceUrls: List<String>,
    var isSaved: Boolean = false,
) {

    fun toSQL(): WordSQL = WordSQL(
        word = word,
        phonetics = phonetics,
        licenses = licenses.map(WordLicenseJsonData::name),
        sourceUrls = sourceUrls,
        meanings = meanings.mapIndexed { i, meaning ->
            WordMeaningSQL(
                word = word,
                meaningIndex = i,
                partOfSpeech = meaning.partOfSpeech,
                synonyms = meaning.synonyms,
                antonyms = meaning.antonyms,
                definitions = meaning.definitions.mapIndexed { j, definition ->
                    WordDefinitionSQL(
                        definition = definition.definition,
                        definitionIndex = j,
                        example = definition.example,
                    )
                },
            )
        }
    )

    @OptIn(ExperimentalSerializationApi::class)
    fun serializeToCborHex(): String = experimentalSerializationApi {
        Cbor.encodeToHexString(this)
    }

    companion object {
        @OptIn(ExperimentalSerializationApi::class)
        fun deserializeFromCborHex(cborHex: String): WordData = experimentalSerializationApi {
            Cbor.decodeFromHexString(cborHex)
        }

        fun fromSQL(sqlData: WordSQL): WordData = WordData(
            word = sqlData.word,
            phonetics = sqlData.phonetics,
            meanings = sqlData.meanings.map { meaning ->
                WordMeaningJsonData(
                    partOfSpeech = meaning.partOfSpeech,
                    synonyms = meaning.synonyms,
                    antonyms = meaning.antonyms,
                    definitions = meaning.definitions.map { definition ->
                        WordDefinitionJsonData(
                            definition = definition.definition,
                            example = definition.example,
                        )
                    }
                )
            },
            licenses = sqlData.licenses.map { WordLicenseJsonData(it) },
            sourceUrls = sqlData.sourceUrls,
        )
    }

}