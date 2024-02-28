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

package me.dkim19375.dictionary.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.android.horologist.compose.layout.AppScaffold
import com.google.android.horologist.compose.layout.ResponsiveTimeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.dkim19375.dictionary.data.WordData
import me.dkim19375.dictionary.ui.screen.CheckInternetScreen
import me.dkim19375.dictionary.ui.screen.CheckInternetScreenRoute
import me.dkim19375.dictionary.ui.screen.HomeScreen
import me.dkim19375.dictionary.ui.screen.HomeScreenRoute
import me.dkim19375.dictionary.ui.screen.LoadingScreen
import me.dkim19375.dictionary.ui.screen.LoadingScreenRoute
import me.dkim19375.dictionary.ui.screen.SavedWordsScreen
import me.dkim19375.dictionary.ui.screen.SavedWordsScreenRoute
import me.dkim19375.dictionary.ui.screen.ScreenRoute
import me.dkim19375.dictionary.ui.screen.SearchScreen
import me.dkim19375.dictionary.ui.screen.SearchScreenRoute
import me.dkim19375.dictionary.ui.screen.WordInfoScreen
import me.dkim19375.dictionary.ui.screen.WordInfoScreenRoute
import me.dkim19375.dictionary.ui.screen.WordNotFoundScreen
import me.dkim19375.dictionary.ui.screen.WordNotFoundScreenRoute
import me.dkim19375.dictionary.ui.theme.DictionaryTheme
import me.dkim19375.dictionary.util.WordFetcher

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            DictionaryApp()
        }
    }
}

@SuppressLint("RestrictedApi")
@Composable
fun DictionaryApp() = DictionaryTheme {
    val navController = rememberNavController()
    navController.addOnDestinationChangedListener { controller, _, _ ->
        val routes = controller.currentBackStack.value.map { it.destination.route }
        if (routes.filterNotNull().asReversed().drop(1).any {
                it in setOf(
                    LoadingScreenRoute,
                    LoadingScreenRoute,
                    WordNotFoundScreenRoute(null),
                    CheckInternetScreenRoute(null)
                ).map(ScreenRoute::route)
            }) {
            Log.w("BackStackLog", "Backstack: ${routes.joinToString()}")
        } else {
            Log.i("BackStackLog", "Backstack: ${routes.joinToString()}")
        }
    }
    AppScaffold(
        timeText = {
            ResponsiveTimeText(
                timeTextStyle = MaterialTheme.typography.caption3.copy(fontSize = 15.sp)
            )
        }
    ) {
        val coroutineScope = rememberCoroutineScope()

        NavHost(
            navController = navController,
            startDestination = HomeScreenRoute.route,
        ) {

            composable(route = HomeScreenRoute.route) {
                val context = LocalContext.current
                HomeScreen(
                    noInputText = {
                        navController.navigate(WordNotFoundScreenRoute(" ").route)
                    },
                    searchInputReceived = {
                        navController.navigate(LoadingScreenRoute.route)
                        coroutineScope.launch {
                            searchForWord(it, context, navController)
                        }
                    },
                    savedButtonPressed = { navController.navigate(LoadingScreenRoute.route) },
                    savedWordsLoaded = { words ->
                        navController.navigate(SavedWordsScreenRoute(words).route) {
                            popUpTo(LoadingScreenRoute.route) { inclusive = true }
                        }
                    },
                )
            }

            composable(route = LoadingScreenRoute.route) {
                LoadingScreen()
            }

            composable(
                route = WordNotFoundScreenRoute(null).route,
                arguments = listOf(navArgument("searchInput") { type = NavType.StringType })
            ) {
                WordNotFoundScreen(it.arguments?.getString("searchInput")!!) {
                    navController.navigate(HomeScreenRoute.route) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            }

            composable(
                route = SearchScreenRoute(null).route,
                arguments = listOf(navArgument("wordData") { type = NavType.StringType })
            ) {
                val wordData = it.arguments?.getString("wordData")?.let { cbor ->
                    WordData.deserializeFromCborHex(cbor)
                }!!
                SearchScreen(wordData) { index, isSaved ->
                    navController.navigate(
                        WordInfoScreenRoute(wordData.copy(isSaved = isSaved), index).route
                    )
                }
            }

            composable(
                route = WordInfoScreenRoute(null, null).route,
                arguments = listOf(
                    navArgument("wordData") { type = NavType.StringType },
                    navArgument("meaningIndex") { type = NavType.IntType }
                )
            ) {
                val wordData = it.arguments?.getString("wordData")?.let { cbor ->
                    WordData.deserializeFromCborHex(cbor)
                }!!
                WordInfoScreen(wordData, wordData.meanings[it.arguments?.getInt("meaningIndex")!!])
            }

            composable(
                route = SavedWordsScreenRoute(null).route,
                arguments = listOf(navArgument("words") { type = NavType.StringType })
            ) {
                val words = it.arguments?.getString("words")
                    ?.removePrefix("[")
                    ?.removeSuffix("]")
                    ?.split("|")
                    ?.filter(String::isNotBlank)
                    ?.toList()!!
                SavedWordsScreen(
                    words = words,
                    wordSelected = { navController.navigate(LoadingScreenRoute.route) },
                    wordLoaded = { word, sqlData ->
                        if (sqlData == null) {
                            navController.navigate(WordNotFoundScreenRoute(word).route) {
                                popUpTo(LoadingScreenRoute.route) { inclusive = true }
                            }
                            return@SavedWordsScreen
                        }
                        val wordData = WordData.fromSQL(sqlData).copy(isSaved = true)
                        navController.navigate(SearchScreenRoute(wordData).route) {
                            popUpTo(LoadingScreenRoute.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = CheckInternetScreenRoute(null).route,
                arguments = listOf(navArgument("searchInput") { type = NavType.StringType })
            ) {
                val searchInput = it.arguments?.getString("searchInput")!!
                val context = LocalContext.current
                CheckInternetScreen(
                    tryAgain = {
                        navController.navigate(LoadingScreenRoute.route) {
                            popUpTo(CheckInternetScreenRoute(searchInput).route) {
                                inclusive = true
                            }
                        }
                        coroutineScope.launch {
                            searchForWord(
                                inputText = searchInput,
                                context = context,
                                navController = navController,
                            )
                        }
                    },
                    backToHome = {
                        navController.navigate(HomeScreenRoute.route) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

private suspend fun searchForWord(
    inputText: String,
    context: Context,
    navController: NavController,
) {
    val wordData = withContext(Dispatchers.IO) {
        Log.i("idk", "In IO: $inputText")
        runCatching {
            WordFetcher.fetchWord(inputText, context)?.takeIf { wordData ->
                Log.i("idk", "Raw word data: $wordData")
                wordData.meanings.isNotEmpty()
            }
        }
    }.getOrElse { error ->
        error.printStackTrace()
        navController.navigate(CheckInternetScreenRoute(inputText).route) {
            popUpTo(LoadingScreenRoute.route) { inclusive = true }
        }
        return@searchForWord
    }
    Log.i("idk", "Got word data: ${wordData?.word}")
    if (wordData == null) {
        navController.navigate(WordNotFoundScreenRoute(inputText).route) {
            popUpTo(LoadingScreenRoute.route) { inclusive = true }
        }
    } else {
        navController.navigate(SearchScreenRoute(wordData).route) {
            popUpTo(LoadingScreenRoute.route) { inclusive = true }
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showSystemUi = true)
@Composable
fun DefaultPreview() = DictionaryApp()