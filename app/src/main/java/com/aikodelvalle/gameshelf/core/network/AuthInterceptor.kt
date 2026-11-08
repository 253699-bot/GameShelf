package com.aikodelvalle.gameshelf.core.network

import com.aikodelvalle.gameshelf.domain.repository.SessionRepository
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val sessionRepository: SessionRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val path = originalRequest.url.encodedPath

        if (path.endsWith("/login") || path.endsWith("/register")) {
            return chain.proceed(originalRequest)
        }

        val token = sessionRepository.getToken()
        if (token.isNullOrBlank() || originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
