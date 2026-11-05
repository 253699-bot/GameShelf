package com.aikodelvalle.gameshelf.core.common

sealed class AppError(open val message: String) {
    data class HttpError(val code: Int, override val message: String) : AppError(message)
    data class NetworkError(override val message: String = "Error de conexión a la red. Verifica tu conexión.") : AppError(message)
    data class SerializationError(override val message: String = "Error al procesar la respuesta del servidor.") : AppError(message)
    data class UnknownError(override val message: String = "Ha ocurrido un error inesperado.") : AppError(message)
}
