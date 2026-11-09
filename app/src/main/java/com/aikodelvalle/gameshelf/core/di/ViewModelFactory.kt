package com.aikodelvalle.gameshelf.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aikodelvalle.gameshelf.GameShelfApplication
import com.aikodelvalle.gameshelf.presentation.auth.SessionViewModel
import com.aikodelvalle.gameshelf.presentation.auth.login.LoginViewModel
import com.aikodelvalle.gameshelf.presentation.auth.register.RegisterViewModel
import com.aikodelvalle.gameshelf.presentation.games.form.GameFormViewModel
import com.aikodelvalle.gameshelf.presentation.games.list.GameListViewModel

object ViewModelFactory {

    val Factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val app = gameShelfApplication()
            LoginViewModel(authRepository = app.container.authRepository)
        }
        initializer {
            val app = gameShelfApplication()
            RegisterViewModel(authRepository = app.container.authRepository)
        }
        initializer {
            val app = gameShelfApplication()
            SessionViewModel(sessionRepository = app.container.sessionRepository)
        }
        initializer {
            val app = gameShelfApplication()
            GameListViewModel(gameRepository = app.container.gameRepository)
        }
        initializer {
            val app = gameShelfApplication()
            GameFormViewModel(gameRepository = app.container.gameRepository)
        }
    }
}

private fun CreationExtras.gameShelfApplication(): GameShelfApplication {
    return (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as GameShelfApplication)
}
