
package com.aikodelvalle.gameshelf.core.network

import com.aikodelvalle.gameshelf.BuildConfig
import com.aikodelvalle.gameshelf.data.remote.ApiService
import com.aikodelvalle.gameshelf.domain.repository.SessionRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

class RetrofitClient(
    private val sessionRepository: SessionRepository,
    private val isDebug: Boolean = BuildConfig.DEBUG
) {

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(sessionRepository))

        if (isDebug) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
                redactHeader("Authorization")
            }
            builder.addInterceptor(loggingInterceptor)
        }

        builder.build()
    }

    val apiService: ApiService by lazy {
        val contentType = "application/json".toMediaType()

        Retrofit.Builder()
            .baseUrl("http://192.168.1.12:3000/")
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
            .create(ApiService::class.java)
    }
}
