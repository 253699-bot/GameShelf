package com.aikodelvalle.gameshelf.presentation.games.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aikodelvalle.gameshelf.core.common.Resource
import com.aikodelvalle.gameshelf.domain.model.Game
import com.aikodelvalle.gameshelf.domain.repository.GameRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class GameFormUiState(
    val gameId: Int? = null,
    val title: String = "",
    val platform: String = "",
    val genre: String = "",
    val releaseYear: String = "",
    val completed: Boolean = false,
    val notes: String = "",
    val titleError: String? = null,
    val platformError: String? = null,
    val genreError: String? = null,
    val releaseYearError: String? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val isSaveSuccessful: Boolean = false,
    val existingCreatedAt: String = ""
)

class GameFormViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameFormUiState())
    val uiState: StateFlow<GameFormUiState> = _uiState.asStateFlow()

    fun loadGame(id: Int) {
        if (id <= 0 || _uiState.value.gameId == id) return
        _uiState.update { it.copy(gameId = id, isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            when (val result = gameRepository.getGameById(id)) {
                is Resource.Success -> {
                    val game = result.data
                    _uiState.update {
                        it.copy(
                            gameId = game.id,
                            title = game.title,
                            platform = game.platform,
                            genre = game.genre,
                            releaseYear = if (game.releaseYear > 0) game.releaseYear.toString() else "",
                            completed = game.completed,
                            notes = game.notes,
                            existingCreatedAt = game.createdAt,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.error.message
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(title: String) {
        _uiState.update { it.copy(title = title, titleError = null, errorMessage = null) }
    }

    fun onPlatformChange(platform: String) {
        _uiState.update { it.copy(platform = platform, platformError = null, errorMessage = null) }
    }

    fun onGenreChange(genre: String) {
        _uiState.update { it.copy(genre = genre, genreError = null, errorMessage = null) }
    }

    fun onReleaseYearChange(year: String) {
        _uiState.update { it.copy(releaseYear = year, releaseYearError = null, errorMessage = null) }
    }

    fun onCompletedChange(completed: Boolean) {
        _uiState.update { it.copy(completed = completed) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }

    fun saveGame() {
        if (_uiState.value.isLoading || _uiState.value.isSaving) return

        val isEditing = (_uiState.value.gameId != null && _uiState.value.gameId!! > 0)
        if (isEditing && _uiState.value.existingCreatedAt.isBlank() && _uiState.value.errorMessage != null) {
            return
        }

        val title = _uiState.value.title.trim()
        val platform = _uiState.value.platform.trim()
        val genre = _uiState.value.genre.trim()
        val yearStr = _uiState.value.releaseYear.trim()
        val completed = _uiState.value.completed
        val notes = _uiState.value.notes.trim()

        var hasError = false
        var titleErr: String? = null
        var platformErr: String? = null
        var genreErr: String? = null
        var yearErr: String? = null

        if (title.length < 3) {
            titleErr = "El título debe tener al menos 3 caracteres."
            hasError = true
        }

        if (platform.isBlank()) {
            platformErr = "La plataforma es obligatoria."
            hasError = true
        }

        if (genre.isBlank()) {
            genreErr = "El género es obligatorio."
            hasError = true
        }

        val yearInt = yearStr.toIntOrNull()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (yearInt == null || yearInt < 1958 || yearInt > currentYear) {
            yearErr = "Ingresa un año válido entre 1958 y $currentYear."
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    titleError = titleErr,
                    platformError = platformErr,
                    genreError = genreErr,
                    releaseYearError = yearErr
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, errorMessage = null) }

        viewModelScope.launch {
            val createdAt = if (isEditing) {
                _uiState.value.existingCreatedAt.ifBlank {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                }
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            }

            val gameToSave = Game(
                id = _uiState.value.gameId ?: 0,
                userId = 0, // Assigned by repository
                title = title,
                platform = platform,
                genre = genre,
                releaseYear = yearInt!!,
                completed = completed,
                notes = notes,
                createdAt = createdAt
            )

            val result = if (isEditing) {
                gameRepository.updateGame(gameToSave)
            } else {
                gameRepository.createGame(gameToSave)
            }

            when (result) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(isSaving = false, isSaveSuccessful = true, errorMessage = null)
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isSaving = false, errorMessage = result.error.message)
                    }
                }
            }
        }
    }

    fun onSaveHandled() {
        _uiState.update { it.copy(isSaveSuccessful = false) }
    }
}
