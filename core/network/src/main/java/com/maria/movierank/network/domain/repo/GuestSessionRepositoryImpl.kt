package com.maria.movierank.network.domain.repo

import android.content.SharedPreferences
import com.maria.movierank.network.data.api.SessionApiService
import com.maria.movierank.network.domain.model.GuestSession
import com.maria.movierank.network.domain.utils.safeApiCall
import javax.inject.Inject
import javax.inject.Singleton

private const val KEY_GUEST_SESSION_ID = "guest_session_id"

@Singleton
class GuestSessionRepositoryImpl @Inject constructor(
    private val apiService: SessionApiService,
    private val encryptedPrefs: SharedPreferences
) : GuestSessionRepository {

    override suspend fun createGuestSession(): GuestSession = safeApiCall {
        val response = apiService.createGuestSession()
        GuestSession(
            guestSessionId = response.guestSessionId,
            expiresAt = response.expiresAt
        )
    }

    override fun getSavedSessionId(): String? =
        encryptedPrefs.getString(KEY_GUEST_SESSION_ID, null)

    override fun saveSessionId(sessionId: String) {
        encryptedPrefs.edit().putString(KEY_GUEST_SESSION_ID, sessionId).apply()
    }

    override suspend fun getOrCreate(): String {
        val saved = getSavedSessionId()
        if (!saved.isNullOrBlank()) return saved
        val session = createGuestSession()
        saveSessionId(session.guestSessionId)
        return session.guestSessionId
    }
}
