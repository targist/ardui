package com.targist.ardui

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.targist.ardui.di.AppContainer
import com.targist.ardui.ui.draw.AboutView
import com.targist.ardui.ui.draw.HelpView
import com.targist.ardui.ui.edit.EditProjectScreen
import com.targist.ardui.ui.edit.NewProjectScreen
import com.targist.ardui.ui.home.HomeScreen
import com.targist.ardui.ui.serial.SerialView

/**
 * Destinations used in the ([JetnewsApp]).
 */
object MainDestinations {

    const val HOME_ROUTE = "all"
    const val EDIT_PROGRAM_ROUTE = "edit"
    const val NEW_PROGRAM = "new"
    const val PROJECT_ID = "projectId"
    const val ABOUT = "about"
    const val HELP = "help"
    const val Serial = "run"
}

@ExperimentalMaterialApi
@Composable
fun ArdUINavGraph(
    appContainer: AppContainer,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.HOME_ROUTE
) {
    val actions = remember(navController) { MainActions(navController) }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = MainDestinations.HOME_ROUTE) {
            HomeScreen(
                navigateNewProject = actions.navigateToNewScript,
                navigateToEditProject = actions.navigateToProjectEdit,
                appContainer = appContainer,
            )
        }
        composable(route = MainDestinations.NEW_PROGRAM) {
            NewProjectScreen(
                appContainer = appContainer
            )
        }
        composable("${MainDestinations.EDIT_PROGRAM_ROUTE}/{${MainDestinations.PROJECT_ID}}") {
            EditProjectScreen(
                projectId = it.arguments?.getString(MainDestinations.PROJECT_ID),
                appContainer = appContainer
            )
        }
        composable(route = MainDestinations.ABOUT) {
            AboutView()
        }

        composable(route = MainDestinations.HELP) {
            HelpView()
        }
        composable(route = MainDestinations.Serial) {
            SerialView(appContainer = appContainer)
        }
    }
}


class MainActions(navController: NavHostController) {
    val navigateToProjectEdit: (String) -> Unit = {
        navController.navigate("${MainDestinations.EDIT_PROGRAM_ROUTE}/$it")
    }
    val navigateToNewScript: () -> Unit = {
        navController.navigate(MainDestinations.NEW_PROGRAM)
    }

    val navigateToAbout: () -> Unit = {
        navController.navigate(MainDestinations.ABOUT)
    }
    val navigateToHome: () -> Unit = {
        navController.navigate(MainDestinations.HOME_ROUTE)
    }

    val navigateToHelp: () -> Unit = {
        navController.navigate(MainDestinations.HELP)
    }

    val navigateToSerial: () -> Unit = {
        navController.navigate(MainDestinations.Serial)
    }
}
