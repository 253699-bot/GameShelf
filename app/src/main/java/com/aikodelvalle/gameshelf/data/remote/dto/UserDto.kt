package com.aikodelvalle.gameshelf.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: Int? = null,
    val email: String? = null,
    val name: String? = null
)
