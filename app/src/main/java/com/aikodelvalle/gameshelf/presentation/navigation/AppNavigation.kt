package com.aikodelvalle.gameshelf.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.aikodelvalle.gameshelf.core.di.ViewModelFactory
import com.aikodelvalle.gameshelf.presentation.auth.SessionViewModel
import com.aikodelvalle.gameshelf.presentation.auth.login.LoginScreen
import com.aikodelvalle.gameshelf.presentation.auth.login.LoginViewModel
import com.aikodelvalle.gameshelf.presentation.auth.register.RegisterScreen
import com.aikodelvalle.gameshelf.presentation.auth.register.RegisterViewModel
import com.aikodelvalle.gameshelf.presentation.games.form.GameFormScreen
import com.aikodelvalle.gameshelf.presentation.games.form.GameFormViewModel
import com.aikodelvalle.gameshelf.presentation.games.list.GameListScreen
import com.aikodelvalle.gameshelf.presentation.games.list.GameListViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val GAME_FORM = "game_form?gameId={gameId}"

    fun buildGameFormRoute(gameId: Int? = null): String {
        return if (gameId != null && gameId > 0) {
            "game_form?gameId=$gameId"
        } else {
            "game_form"
        }
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = viewModel(factory = ViewModelFactory.Factory)
) {
    val startDestination = if (sessionViewModel.isUserLoggedIn()) Routes.HOME else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            val loginViewModel: LoginViewModel = viewModel(factory = ViewModelFactory.Factory)
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.REGISTER) {
            val registerViewModel: RegisterViewModel = viewModel(factory = ViewModelFactory.Factory)
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            val gameListViewModel: GameListViewModel = viewModel(factory = ViewModelFactory.Factory)
            GameListScreen(
                viewModel = gameListViewModel,
                onNavigateToCreateGame = {
                    navController.navigate(Routes.buildGameFormRoute())
                },
                onNavigateToEditGame = { gameId ->
                    navController.navigate(Routes.buildGameFormRoute(gameId))
                },
                onLogout = {
                    sessionViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.GAME_FORM,
            arguments = listOf(
                navArgument("gameId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val gameIdArg = backStackEntry.arguments?.getInt("gameId")
            val effectiveGameId = if (gameIdArg != null && gameIdArg > 0) gameIdArg else null
            val gameFormViewModel: GameFormViewModel = viewModel(factory = ViewModelFactory.Factory)

            GameFormScreen(
                gameId = effectiveGameId,
                viewModel = gameFormViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
