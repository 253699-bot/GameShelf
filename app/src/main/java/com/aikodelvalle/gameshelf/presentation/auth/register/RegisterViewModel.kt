package com.aikodelvalle.gameshelf.presentation.auth.register

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

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccessful: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, errorMessage = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update { it.copy(confirmPassword = confirmPassword, errorMessage = null) }
    }

    fun register() {
        val name = _uiState.value.name.trim()
        val email = _uiState.value.email.trim()
        val password = _uiState.value.password
        val confirmPassword = _uiState.value.confirmPassword

        if (name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre es obligatorio.") }
            return
        }

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

        if (password.length < 8) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 8 caracteres.") }
            return
        }

        if (confirmPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Debes confirmar la contraseña.") }
            return
        }

        if (password != confirmPassword) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = authRepository.register(name, email, password)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isLoading = false, isRegistrationSuccessful = true, errorMessage = null)
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

    fun onRegistrationHandled() {
        _uiState.update { it.copy(isRegistrationSuccessful = false) }
    }
}
