package com.maria.movierank.network.domain.model

sealed class NetworkException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    abstract val userMessage: String

    class HttpError(
        val code: Int,
        val body: String?
    ) : NetworkException("HTTP error $code") {
        override val userMessage = "Server error ($code). Try again later."
    }

    class ConnectionError(
        cause: Throwable
    ) : NetworkException("No internet connection", cause) {
        override val userMessage = "No internet connection. Check your network."
    }

    class TimeoutError(
        cause: Throwable
    ) : NetworkException("Connection timed out", cause) {
        override val userMessage = "Connection timed out. Try again."
    }

    class UnknownError(
        cause: Throwable
    ) : NetworkException(cause.message ?: "Unknown error", cause) {
        override val userMessage = "Something went wrong. Try again."
    }
}