package com.aikodelvalle.gameshelf.domain.model

data class Game(
    val id: Int = 0,
    val userId: Int = 0,
    val title: String,
    val platform: String,
    val genre: String,
    val releaseYear: Int,
    val completed: Boolean,
    val notes: String,
    val createdAt: String
)
