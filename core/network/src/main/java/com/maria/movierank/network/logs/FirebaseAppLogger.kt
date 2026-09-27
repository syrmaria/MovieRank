package com.maria.movierank.network.logs

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAppLogger @Inject constructor(
    private val crashlytics: FirebaseCrashlytics
) : AppLogger {

    override fun log(message: String) {
        crashlytics.log(message)
    }

    override fun logError(
        message: String,
        throwable: Throwable?
    ) {
        crashlytics.log(message)

        if (throwable != null) {
            crashlytics.recordException(throwable)
        }
    }

    override fun recordException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}