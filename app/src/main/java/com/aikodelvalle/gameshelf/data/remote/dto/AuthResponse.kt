package com.aikodelvalle.gameshelf.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val accessToken: String,
    val user: UserDto? = null
)
