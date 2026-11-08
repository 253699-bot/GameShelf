package com.aikodelvalle.gameshelf.data.local

import android.content.Context
import android.content.SharedPreferences
import com.aikodelvalle.gameshelf.domain.repository.SessionRepository

class SessionManager(context: Context) : SessionRepository {

    private val prefs: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    override fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    override fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    override fun saveUserId(id: Int) {
        prefs.edit().putInt(KEY_USER_ID, id).apply()
    }

    override fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    override fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    override fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    override fun clearSession() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "gameshelf_session_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_EMAIL = "user_email"
    }
}
