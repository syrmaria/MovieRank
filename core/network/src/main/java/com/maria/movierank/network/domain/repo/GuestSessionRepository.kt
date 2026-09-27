package com.maria.movierank.network.domain.repo

import com.maria.movierank.network.domain.model.GuestSession

interface GuestSessionRepository {
    suspend fun createGuestSession(): GuestSession
    fun getSavedSessionId(): String?
    fun saveSessionId(sessionId: String)
    suspend fun getOrCreate(): String
}