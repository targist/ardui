/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.targist.ardui.ui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.ProvideWindowInsets
import com.targist.ardui.ArdUINavGraph
import com.targist.ardui.MainActions
import com.targist.ardui.MainDestinations
import com.targist.ardui.di.AppContainer
import com.targist.ardui.ui.draw.DrawerView
import com.targist.ardui.ui.theme.ArdUITheme
import com.targist.ardui.ui.topbar.ArdUITopBar
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ArdUIdApp(
    appContainer: AppContainer
) {
    ArdUITheme {
        ProvideWindowInsets {

            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val coroutineScope = rememberCoroutineScope()
            val mainActions = remember {
                MainActions(navController)
            }
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route ?: MainDestinations.HOME_ROUTE

            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    ArdUITopBar(
                        openDrawer = { coroutineScope.launch { scaffoldState.drawerState.open() } },
                        currentRoute = currentRoute,
                        homeRoute = MainDestinations.HOME_ROUTE,
                        navigateToHome = mainActions.navigateToHome,
                        appContainer = appContainer,
                    )
                },
                drawerContent = {
                    DrawerView(
                        currentRoute = currentRoute,
                        mainActions = mainActions,
                        closeDrawer = { coroutineScope.launch { scaffoldState.drawerState.close() } }
                    )
                }
            ) {
                ArdUINavGraph(
                    appContainer = appContainer,
                    navController = navController,
                )
            }
        }
    }
}
