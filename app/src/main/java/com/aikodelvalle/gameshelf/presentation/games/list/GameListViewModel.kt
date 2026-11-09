package com.aikodelvalle.gameshelf.presentation.games.list

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

data class GameListUiState(
    val games: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val gameToDelete: Game? = null,
    val isDeleting: Boolean = false
)

class GameListViewModel(
    private val gameRepository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameListUiState())
    val uiState: StateFlow<GameListUiState> = _uiState.asStateFlow()

    fun loadGames() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = gameRepository.getGames()) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(
                            games = result.data,
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

    fun onDeleteClick(game: Game) {
        _uiState.update { it.copy(gameToDelete = game) }
    }

    fun onCancelDelete() {
        _uiState.update { it.copy(gameToDelete = null) }
    }

    fun confirmDelete() {
        val game = _uiState.value.gameToDelete ?: return
        _uiState.update { it.copy(isDeleting = true) }

        viewModelScope.launch {
            when (val result = gameRepository.deleteGame(game.id)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(gameToDelete = null, isDeleting = false) }
                    loadGames()
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            gameToDelete = null,
                            errorMessage = result.error.message
                        )
                    }
                }
            }
        }
    }
}
