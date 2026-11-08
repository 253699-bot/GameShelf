package com.aikodelvalle.gameshelf.presentation.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aikodelvalle.gameshelf.core.common.Resource
import com.aikodelvalle.gameshelf.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccessful: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun login() {
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password

        if (email.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El correo electrónico es obligatorio.") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido.") }
            return
        }

        if (password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "La contraseña es obligatoria.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isLoginSuccessful = true, errorMessage = null)
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = result.error.message)
                    }
                }
            }
        }
    }

    fun onLoginHandled() {
        _uiState.update { it.copy(isLoginSuccessful = false) }
    }
}
