package com.aikodelvalle.gameshelf.core.di

import android.content.Context
import com.aikodelvalle.gameshelf.core.network.RetrofitClient
import com.aikodelvalle.gameshelf.data.local.SessionManager
import com.aikodelvalle.gameshelf.data.remote.ApiService
import com.aikodelvalle.gameshelf.data.repository.AuthRepositoryImpl
import com.aikodelvalle.gameshelf.data.repository.GameRepositoryImpl
import com.aikodelvalle.gameshelf.domain.repository.AuthRepository
import com.aikodelvalle.gameshelf.domain.repository.GameRepository
import com.aikodelvalle.gameshelf.domain.repository.SessionRepository

class AppContainer(context: Context) {

    private val applicationContext = context.applicationContext

    val sessionRepository: SessionRepository by lazy {
        SessionManager(applicationContext)
    }

    private val retrofitClient: RetrofitClient by lazy {
        RetrofitClient(sessionRepository = sessionRepository)
    }

    val apiService: ApiService by lazy {
        retrofitClient.apiService
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            apiService = apiService,
            sessionRepository = sessionRepository
        )
    }

    val gameRepository: GameRepository by lazy {
        GameRepositoryImpl(
            apiService = apiService,
            sessionRepository = sessionRepository
        )
    }
}
