package com.aikodelvalle.gameshelf.domain.repository

import com.aikodelvalle.gameshelf.core.common.Resource
import com.aikodelvalle.gameshelf.domain.model.Game

interface GameRepository {
    suspend fun getGames(): Resource<List<Game>>
    suspend fun getGameById(id: Int): Resource<Game>
    suspend fun createGame(game: Game): Resource<Game>
    suspend fun updateGame(game: Game): Resource<Game>
    suspend fun deleteGame(id: Int): Resource<Unit>
}
