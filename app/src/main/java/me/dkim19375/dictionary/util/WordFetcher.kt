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

package me.dkim19375.dictionary.util

import android.content.Context
import android.util.Log
import com.google.gson.GsonBuilder
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withTimeout
import me.dkim19375.dictionary.data.WordData
import me.dkim19375.dictionary.data.WordJsonData
import me.dkim19375.dictionary.data.room.AppDatabase
import me.dkim19375.dkimcore.extension.toURL
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.time.Duration
import kotlin.time.Duration.Companion.seconds

object WordFetcher {
    private const val API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/%s"

    private val gson = GsonBuilder().enableComplexMapKeySerialization().create()

    private val httpClient = OkHttpClient.Builder().apply {
        connectTimeout(Duration.ofSeconds(5))
        callTimeout(Duration.ofSeconds(15))
    }.build()

    suspend fun fetchWord(word: String, context: Context? = null): WordData? = coroutineScope {
        if (word.trim() != word) return@coroutineScope fetchWord(word.trim())

        Log.i("idk", "fetching $word")
        val jsonText = withTimeout(15.seconds) {
            Log.i("idk", "Awaiting response")
            httpClient.newCall(
                Request.Builder().url(API_URL.format(word)).build()
            ).await().also {
                Log.i("idk", "Awaited response, is successful: ${it.isSuccessful}")
            }.takeIf(Response::isSuccessful)?.body?.string()
        } ?: return@coroutineScope null
        Log.i("idk", "json: $jsonText")
        val jsonData = runCatching {
            gson.fromJson(jsonText, Array<WordJsonData>::class.java)
        }.getOrElse {
            Log.e("idk", "Failed to parse json", it)
            return@coroutineScope null
        }.ifEmpty {
            Log.e("idk", "Empty json")
            return@coroutineScope null
        }
        Log.i("idk", "jsonData: $jsonData")
        WordData(
            word = jsonData[0].word,
            phonetics = jsonData.mapNotNull(WordJsonData::phonetic).distinct(),
            meanings = jsonData.flatMap(WordJsonData::meanings)
                .fold(mutableListOf()) { list, new ->
                    val existingIndex = list.indexOfFirst { it.partOfSpeech == new.partOfSpeech }
                    if (existingIndex == -1) {
                        list.add(new)
                        return@fold list
                    }
                    val existing = list[existingIndex]
                    list[existingIndex] = existing.copy(
                        definitions = (existing.definitions + new.definitions).distinct(),
                        synonyms = (existing.synonyms + new.synonyms).distinct(),
                        antonyms = (existing.antonyms + new.antonyms).distinct(),
                    )
                    list
                },
            licenses = jsonData.map(WordJsonData::license).distinct(),
            sourceUrls = jsonData.flatMap(WordJsonData::sourceUrls).distinct(),
            isSaved = context?.let {
                AppDatabase.getInstance(it).wordDao().isWordSaved(word)
            } ?: false,
        )
    }

}