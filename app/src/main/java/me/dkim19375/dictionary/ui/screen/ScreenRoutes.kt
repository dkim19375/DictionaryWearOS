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

import me.dkim19375.dictionary.data.WordData

sealed interface ScreenRoute {
    val route: String
}

data object HomeScreenRoute : ScreenRoute {
    override val route = "home"
}

data object LoadingScreenRoute : ScreenRoute {
    override val route = "loading"
}

data class WordNotFoundScreenRoute(val searchInput: String?) : ScreenRoute {
    override val route = "wordNotFound/${searchInput ?: "{searchInput}"}"
}

data class SearchScreenRoute(val wordData: WordData?) : ScreenRoute {
    override val route = "search/${wordData?.serializeToCborHex() ?: "{wordData}"}"
}

data class WordInfoScreenRoute(val wordData: WordData?, val meaningIndex: Int?) : ScreenRoute {
    override val route = "wordInfo/${wordData?.serializeToCborHex() ?: "{wordData}"}/${
        meaningIndex ?: "{meaningIndex}"
    }"
}

data class SavedWordsScreenRoute(val words: List<String>?) : ScreenRoute {
    override val route = "saved/${words?.joinToString("|", "[", "]") ?: "{words}"}"
}

data class CheckInternetScreenRoute(val searchInput: String?) : ScreenRoute {
    override val route = "checkInternet/${searchInput ?: "{searchInput}"}"
}