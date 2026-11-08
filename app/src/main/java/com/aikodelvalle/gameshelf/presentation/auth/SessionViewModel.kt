package com.aikodelvalle.gameshelf.presentation.auth

import androidx.lifecycle.ViewModel
import com.aikodelvalle.gameshelf.domain.repository.SessionRepository

class SessionViewModel(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    fun isUserLoggedIn(): Boolean {
        val token = sessionRepository.getToken()
        val userId = sessionRepository.getUserId()
        return !token.isNullOrBlank() && userId > 0
    }

    fun logout() {
        sessionRepository.clearSession()
    }
}
