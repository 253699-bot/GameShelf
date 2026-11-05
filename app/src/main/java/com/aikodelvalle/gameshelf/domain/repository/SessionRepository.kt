package com.aikodelvalle.gameshelf.domain.repository

interface SessionRepository {
    fun saveToken(token: String)
    fun getToken(): String?
    fun saveUserId(id: Int)
    fun getUserId(): Int
    fun saveEmail(email: String)
    fun getEmail(): String?
    fun clearSession()
}
