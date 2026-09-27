package com.maria.movierank.network.logs

interface AppLogger {

    fun log(message: String)

    fun logError(
        message: String,
        throwable: Throwable? = null
    )

    fun recordException(throwable: Throwable)
}