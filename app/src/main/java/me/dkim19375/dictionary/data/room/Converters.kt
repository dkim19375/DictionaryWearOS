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

import androidx.room.TypeConverter
import me.dkim19375.dictionary.enumclass.PartOfSpeech

class Converters {
    @TypeConverter
    fun fromPartOfSpeech(value: PartOfSpeech): String = value.name

    @TypeConverter
    fun toPartOfSpeech(value: String): PartOfSpeech = PartOfSpeech.valueOf(value)

    @TypeConverter
    fun fromIntList(value: List<Int>): String = "[${value.joinToString("|")}]"

    @TypeConverter
    fun toIntList(value: String): List<Int> = value
        .removePrefix("[")
        .removeSuffix("]")
        .split("|")
        .filter(String::isNotBlank)
        .map(String::toInt)

    @TypeConverter
    fun fromStringList(value: List<String>): String = "[${value.joinToString("|")}]"

    @TypeConverter
    fun toStringList(value: String): List<String> = value
        .removePrefix("[")
        .removeSuffix("]")
        .split("|")
        .filter(String::isNotBlank)
}