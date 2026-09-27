package com.maria.movierank.network.domain

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.maria.movierank.network.logs.FirebaseAppLogger
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.mockito.kotlin.*

@Ignore("Mockito cannot mock FirebaseCrashlytics atm due to inline mock limitations")
class FirebaseAppLoggerTest {

    private lateinit var crashlytics: FirebaseCrashlytics
    private lateinit var logger: FirebaseAppLogger

    @Before
    fun setUp() {
        crashlytics = mock()
        logger = FirebaseAppLogger(crashlytics)
    }

    @Test
    fun logDelegatesMessageToCrashlytics() {
        val message = "Test log message"

        logger.log(message)

        verify(crashlytics).log(message)
    }

    @Test
    fun logErrorLogsMessageAndRecordsException() {
        val message = "Something went wrong"
        val exception = RuntimeException("Test exception")

        logger.logError(message, exception)

        verify(crashlytics).log(message)
        verify(crashlytics).recordException(exception)
    }

    @Test
    fun logErrorDoesNotRecordExceptionWhenThrowableIsNull() {
        val message = "Something went wrong"

        logger.logError(message, null)

        verify(crashlytics).log(message)
        verify(crashlytics, never()).recordException(any())
    }

    @Test
    fun recordExceptionDelegatesToCrashlytics() {
        val exception = IllegalStateException("Test exception")

        logger.recordException(exception)

        verify(crashlytics).recordException(exception)
    }
}