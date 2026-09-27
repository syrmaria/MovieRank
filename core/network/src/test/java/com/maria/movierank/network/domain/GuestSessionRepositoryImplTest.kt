package com.maria.movierank.network.domain

import android.content.SharedPreferences
import com.maria.movierank.network.data.api.SessionApiService
import com.maria.movierank.network.data.model.GuestSessionResponse
import com.maria.movierank.network.domain.repo.GuestSessionRepositoryImpl
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class GuestSessionRepositoryImplTest {

    private lateinit var mockApiService: SessionApiService
    private lateinit var mockPrefs: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor
    private lateinit var repo: GuestSessionRepositoryImpl

    @Before
    fun setUp() {
        mockApiService = mock()
        mockPrefs = mock()
        mockEditor = mock()
        whenever(mockPrefs.edit()).thenReturn(mockEditor)
        whenever(mockEditor.putString(any(), anyOrNull())).thenReturn(mockEditor)
        repo = GuestSessionRepositoryImpl(mockApiService, mockPrefs)
    }

    @Test
    fun `getSavedSessionId returns null when nothing saved`() {
        whenever(mockPrefs.getString(any(), anyOrNull())).thenReturn(null)
        assertNull(repo.getSavedSessionId())
    }

    @Test
    fun `getSavedSessionId returns saved session id`() {
        whenever(mockPrefs.getString(any(), anyOrNull())).thenReturn("abc-123")
        assertEquals("abc-123", repo.getSavedSessionId())
    }

    @Test
    fun `saveSessionId stores value in shared preferences`() {
        repo.saveSessionId("abc-123")
        verify(mockEditor).putString("guest_session_id", "abc-123")
        verify(mockEditor).apply()
    }

    @Test
    fun `createGuestSession maps API response to domain model`() = runTest {
        whenever(mockApiService.createGuestSession())
            .thenReturn(GuestSessionResponse(true, "new-guest-id", "2025-12-31"))
        val session = repo.createGuestSession()
        assertEquals("new-guest-id", session.guestSessionId)
        assertEquals("2025-12-31", session.expiresAt)
    }

    @Test
    fun `getOrCreate calls API and saves session when no saved session`() = runTest {
        whenever(mockPrefs.getString(any(), anyOrNull())).thenReturn(null)
        whenever(mockApiService.createGuestSession())
            .thenReturn(GuestSessionResponse(true, "fresh-id", "2025-01-01"))
        val result = repo.getOrCreate()
        assertEquals("fresh-id", result)
        verify(mockEditor).putString("guest_session_id", "fresh-id")
    }

    @Test
    fun `getOrCreate returns saved session without calling API`() = runTest {
        whenever(mockPrefs.getString(any(), anyOrNull())).thenReturn("existing-id")
        val result = repo.getOrCreate()
        assertEquals("existing-id", result)
        verify(mockApiService, never()).createGuestSession()
    }
}
