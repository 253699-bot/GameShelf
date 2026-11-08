package com.aikodelvalle.gameshelf.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class GameCreateRequest(
    val userId: Int,
    val title: String,
    val platform: String,
    val genre: String,
    val releaseYear: Int,
    val completed: Boolean,
    val notes: String,
    val createdAt: String
)
