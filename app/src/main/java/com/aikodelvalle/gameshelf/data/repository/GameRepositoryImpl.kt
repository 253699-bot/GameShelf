package com.aikodelvalle.gameshelf.data.repository

import com.aikodelvalle.gameshelf.core.common.AppError
import com.aikodelvalle.gameshelf.core.common.Resource
import com.aikodelvalle.gameshelf.data.mapper.toCreateRequest
import com.aikodelvalle.gameshelf.data.mapper.toDomain
import com.aikodelvalle.gameshelf.data.mapper.toUpdateRequest
import com.aikodelvalle.gameshelf.data.remote.ApiService
import com.aikodelvalle.gameshelf.domain.model.Game
import com.aikodelvalle.gameshelf.domain.repository.GameRepository
import com.aikodelvalle.gameshelf.domain.repository.SessionRepository
import kotlinx.serialization.SerializationException
import java.io.IOException

class GameRepositoryImpl(
    private val apiService: ApiService,
    private val sessionRepository: SessionRepository
) : GameRepository {

    override suspend fun getGames(): Resource<List<Game>> {
        return try {
            val response = apiService.getGames()
            if (response.isSuccessful && response.body() != null) {
                val gamesList = response.body()!!.map { it.toDomain() }
                Resource.Success(gamesList)
            } else {
                val code = response.code()
                Resource.Error(AppError.HttpError(code, "Error al obtener videojuegos ($code)."))
            }
        } catch (e: IOException) {
            Resource.Error(AppError.NetworkError())
        } catch (e: SerializationException) {
            Resource.Error(AppError.SerializationError())
        } catch (e: Exception) {
            Resource.Error(AppError.UnknownError(e.localizedMessage ?: "Error desconocido."))
        }
    }

    override suspend fun getGameById(id: Int): Resource<Game> {
        return try {
            val response = apiService.getGameById(id)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                val code = response.code()
                val msg = if (code == 404) "El videojuego no existe o no tienes acceso." else "Error al obtener el juego ($code)."
                Resource.Error(AppError.HttpError(code, msg))
            }
        } catch (e: IOException) {
            Resource.Error(AppError.NetworkError())
        } catch (e: SerializationException) {
            Resource.Error(AppError.SerializationError())
        } catch (e: Exception) {
            Resource.Error(AppError.UnknownError(e.localizedMessage ?: "Error desconocido."))
        }
    }

    override suspend fun createGame(game: Game): Resource<Game> {
        val userId = sessionRepository.getUserId()
        if (userId <= 0) {
            return Resource.Error(AppError.HttpError(401, "Usuario no autenticado."))
        }

        val gameWithUser = game.copy(userId = userId)
        val createRequest = gameWithUser.toCreateRequest()

        return try {
            val response = apiService.createGame(createRequest)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                val code = response.code()
                Resource.Error(AppError.HttpError(code, "Error al guardar el videojuego ($code)."))
            }
        } catch (e: IOException) {
            Resource.Error(AppError.NetworkError())
        } catch (e: SerializationException) {
            Resource.Error(AppError.SerializationError())
        } catch (e: Exception) {
            Resource.Error(AppError.UnknownError(e.localizedMessage ?: "Error desconocido."))
        }
    }

    override suspend fun updateGame(game: Game): Resource<Game> {
        val userId = sessionRepository.getUserId()
        if (userId <= 0) {
            return Resource.Error(AppError.HttpError(401, "Usuario no autenticado."))
        }

        val gameWithUser = game.copy(userId = userId)
        val updateRequest = gameWithUser.toUpdateRequest()

        return try {
            val response = apiService.updateGame(game.id, updateRequest)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!.toDomain())
            } else {
                val code = response.code()
                Resource.Error(AppError.HttpError(code, "Error al actualizar el videojuego ($code)."))
            }
        } catch (e: IOException) {
            Resource.Error(AppError.NetworkError())
        } catch (e: SerializationException) {
            Resource.Error(AppError.SerializationError())
        } catch (e: Exception) {
            Resource.Error(AppError.UnknownError(e.localizedMessage ?: "Error desconocido."))
        }
    }

    override suspend fun deleteGame(id: Int): Resource<Unit> {
        return try {
            val response = apiService.deleteGame(id)
            if (response.isSuccessful) {
                response.body()?.close()
                Resource.Success(Unit)
            } else {
                val code = response.code()
                Resource.Error(AppError.HttpError(code, "Error al eliminar el videojuego ($code)."))
            }
        } catch (e: IOException) {
            Resource.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Resource.Error(AppError.UnknownError(e.localizedMessage ?: "Error desconocido."))
        }
    }
}
