package com.aikodelvalle.gameshelf.data.remote

import com.aikodelvalle.gameshelf.data.remote.dto.AuthResponse
import com.aikodelvalle.gameshelf.data.remote.dto.GameCreateRequest
import com.aikodelvalle.gameshelf.data.remote.dto.GameDto
import com.aikodelvalle.gameshelf.data.remote.dto.GameUpdateRequest
import com.aikodelvalle.gameshelf.data.remote.dto.LoginRequest
import com.aikodelvalle.gameshelf.data.remote.dto.RegisterRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("games")
    suspend fun getGames(): Response<List<GameDto>>

    @GET("games/{id}")
    suspend fun getGameById(@Path("id") id: Int): Response<GameDto>

    @POST("games")
    suspend fun createGame(@Body request: GameCreateRequest): Response<GameDto>

    @PUT("games/{id}")
    suspend fun updateGame(@Path("id") id: Int, @Body request: GameUpdateRequest): Response<GameDto>

    @DELETE("games/{id}")
    suspend fun deleteGame(@Path("id") id: Int): Response<ResponseBody>
}
