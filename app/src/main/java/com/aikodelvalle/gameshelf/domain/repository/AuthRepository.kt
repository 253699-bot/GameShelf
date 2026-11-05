package com.aikodelvalle.gameshelf.domain.repository

import com.aikodelvalle.gameshelf.core.common.Resource
import com.aikodelvalle.gameshelf.domain.model.User

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Resource<User>
    suspend fun login(email: String, password: String): Resource<User>
}
