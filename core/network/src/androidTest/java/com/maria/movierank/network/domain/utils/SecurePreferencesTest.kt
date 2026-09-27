package com.maria.movierank.network.domain.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SecurePreferencesTest {

    private lateinit var context: Context
    private lateinit var securePreferences: SecurePreferences

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        securePreferences = SecurePreferences(context)

        // Очищаем хранилище перед каждым тестом
        securePreferences.edit()
            .clear()
            .commit()
    }

    @After
    fun tearDown() {
        securePreferences.edit()
            .clear()
            .commit()
    }

    @Test
    fun storesAndReadsEncryptedString() {
        securePreferences.edit()
            .putString("session_id", "test-session")
            .apply()

        val result = securePreferences.getString("session_id", null)

        assertEquals("test-session", result)
    }

    @Test
    fun defaultForMissingKey() {
        val result = securePreferences.getString(
            "missing_key",
            "default"
        )

        assertEquals("default", result)
    }

    @Test
    fun nullForMissingKeyWithoutDefault() {
        val result = securePreferences.getString("missing_key", null)

        assertNull(result)
    }

    @Test
    fun overwritesExisting() {
        securePreferences.edit()
            .putString("session_id", "old-session")
            .apply()

        securePreferences.edit()
            .putString("session_id", "new-session")
            .apply()

        assertEquals(
            "new-session",
            securePreferences.getString("session_id", null)
        )
    }

    @Test
    fun removesNullString() {
        securePreferences.edit()
            .putString("session_id", "test-session")
            .apply()

        securePreferences.edit()
            .putString("session_id", null)
            .apply()

        assertNull(
            securePreferences.getString("session_id", null)
        )

        assertFalse(securePreferences.contains("session_id"))
    }

    @Test
    fun savesToPrefs() {
        val committed = securePreferences.edit()
            .putString("session_id", "test-session")
            .commit()

        assertTrue(committed)

        assertEquals(
            "test-session",
            securePreferences.getString("session_id", null)
        )
    }

    @Test
    fun storesEmpty() {
        securePreferences.edit()
            .putString("empty", "")
            .apply()

        assertEquals(
            "",
            securePreferences.getString("empty", null)
        )
    }

    @Test
    fun storeAndReadUnicode() {
        val value = "Привет, 마리! 🎬"

        securePreferences.edit()
            .putString("greeting", value)
            .apply()

        assertEquals(
            value,
            securePreferences.getString("greeting", null)
        )
    }

    @Test
    fun storeAndReadBoolean() {
        securePreferences.edit()
            .putBoolean("is_logged_in", true)
            .apply()

        assertTrue(
            securePreferences.getBoolean("is_logged_in", false)
        )
    }

    @Test
    fun storeAndReadInt() {
        securePreferences.edit()
            .putInt("page", 42)
            .apply()

        assertEquals(
            42,
            securePreferences.getInt("page", 0)
        )
    }

    @Test
    fun storeAndReadLong() {
        securePreferences.edit()
            .putLong("timestamp", 123456789L)
            .apply()

        assertEquals(
            123456789L,
            securePreferences.getLong("timestamp", 0L)
        )
    }

    @Test
    fun storeAndReadFloat() {
        securePreferences.edit()
            .putFloat("rating", 8.5f)
            .apply()

        assertEquals(
            8.5f,
            securePreferences.getFloat("rating", 0f),
            0.001f
        )
    }

    @Test
    fun clearRemovesAll() {
        securePreferences.edit()
            .putString("session_id", "test-session")
            .putBoolean("is_logged_in", true)
            .putInt("page", 5)
            .apply()

        securePreferences.edit()
            .clear()
            .apply()

        assertTrue(securePreferences.getAll().isEmpty())
    }

    @Test
    fun getAllTest() {
        securePreferences.edit()
            .putString("session_id", "test-session")
            .putBoolean("is_logged_in", true)
            .apply()

        val all = securePreferences.getAll()

        assertTrue(all.containsKey("session_id"))
        assertTrue(all.containsKey("is_logged_in"))
    }

    @Test
    fun notStorePlainTextInPrefs() {
        val secret = "my-secret-token"

        securePreferences.edit()
            .putString("token", secret)
            .commit()

        val rawPreferences = context.getSharedPreferences(
            "movierank_secure_prefs",
            Context.MODE_PRIVATE
        )

        val rawValue = rawPreferences.getString("token", null)

        assertNotNull(rawValue)
        assertNotEquals(secret, rawValue)
    }
}