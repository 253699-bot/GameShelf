package com.aikodelvalle.gameshelf.data.mapper

import com.aikodelvalle.gameshelf.data.remote.dto.GameCreateRequest
import com.aikodelvalle.gameshelf.data.remote.dto.GameDto
import com.aikodelvalle.gameshelf.data.remote.dto.GameUpdateRequest
import com.aikodelvalle.gameshelf.domain.model.Game

fun GameDto.toDomain(): Game {
    return Game(
        id = id,
        userId = userId,
        title = title,
        platform = platform,
        genre = genre,
        releaseYear = releaseYear,
        completed = completed,
        notes = notes.orEmpty(),
        createdAt = createdAt
    )
}

fun Game.toCreateRequest(): GameCreateRequest {
    return GameCreateRequest(
        userId = userId,
        title = title,
        platform = platform,
        genre = genre,
        releaseYear = releaseYear,
        completed = completed,
        notes = notes,
        createdAt = createdAt
    )
}

fun Game.toUpdateRequest(): GameUpdateRequest {
    return GameUpdateRequest(
        userId = userId,
        title = title,
        platform = platform,
        genre = genre,
        releaseYear = releaseYear,
        completed = completed,
        notes = notes,
        createdAt = createdAt
    )
}
