package com.aikodelvalle.gameshelf.data.repository

import android.util.Base64
import com.aikodelvalle.gameshelf.core.common.AppError
import com.aikodelvalle.gameshelf.core.common.Resource
import com.aikodelvalle.gameshelf.data.remote.ApiService
import com.aikodelvalle.gameshelf.data.remote.dto.LoginRequest
import com.aikodelvalle.gameshelf.data.remote.dto.RegisterRequest
import com.aikodelvalle.gameshelf.domain.model.User
import com.aikodelvalle.gameshelf.domain.repository.AuthRepository
import com.aikodelvalle.gameshelf.domain.repository.SessionRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val sessionRepository: SessionRepository
) : AuthRepository {

    override suspend fun register(name: String, email: String, password: String): Resource<User> {
        return try {
            val response = apiService.register(RegisterRequest(name, email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.accessToken
                val userId = authResponse.user?.id ?: parseUserIdFromJwt(token)

                if (userId == null || userId <= 0) {
                    return Resource.Error(AppError.UnknownError("Respuesta de registro no contiene un identificador de usuario válido."))
                }

                val userName = authResponse.user?.name ?: name
                val userEmail = authResponse.user?.email ?: email

                val user = User(id = userId, email = userEmail, name = userName)
                Resource.Success(user)
            } else {
                val code = response.code()
                val msg = if (code == 400) "El correo ya está registrado o los datos son inválidos." else "Error en el registro ($code)."
                Resource.Error(AppError.HttpError(code, msg))
            }
        } catch (e: IOException) {
            Resource.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Resource.Error(AppError.UnknownError(e.localizedMessage ?: "Error desconocido."))
        }
    }

    override suspend fun login(email: String, password: String): Resource<User> {
        return try {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val token = authResponse.accessToken

                if (token.isBlank()) {
                    return Resource.Error(AppError.UnknownError("El servidor no proporcionó un token de sesión válido."))
                }

                val userId = authResponse.user?.id ?: parseUserIdFromJwt(token)

                if (userId == null || userId <= 0) {
                    return Resource.Error(AppError.UnknownError("No se pudo obtener la identidad del usuario desde el servidor."))
                }

                val userName = authResponse.user?.name ?: ""
                val userEmail = authResponse.user?.email ?: email

                // Guardar la sesión únicamente si los datos son válidos
                sessionRepository.saveToken(token)
                sessionRepository.saveUserId(userId)
                sessionRepository.saveEmail(userEmail)

                val user = User(id = userId, email = userEmail, name = userName)
                Resource.Success(user)
            } else {
                val code = response.code()
                val msg = if (code == 400) "Credenciales incorrectas." else "Error de autenticación ($code)."
                Resource.Error(AppError.HttpError(code, msg))
            }
        } catch (e: IOException) {
            Resource.Error(AppError.NetworkError())
        } catch (e: Exception) {
            Resource.Error(AppError.UnknownError(e.localizedMessage ?: "Error desconocido."))
        }
    }

    private fun parseUserIdFromJwt(token: String): Int? {
        return try {
            val parts = token.split(".")
            if (parts.size >= 2) {
                val payloadJson = String(
                    Base64.decode(
                        parts[1],
                        Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP
                    ),
                    Charsets.UTF_8
                )
                val jsonElement = Json.parseToJsonElement(payloadJson)
                val sub = jsonElement.jsonObject["sub"]?.jsonPrimitive?.content
                sub?.toIntOrNull()
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
