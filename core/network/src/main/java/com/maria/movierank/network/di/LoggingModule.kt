package com.maria.movierank.network.di

import com.maria.movierank.network.logs.AppLogger
import com.maria.movierank.network.logs.FirebaseAppLogger
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
abstract class LoggingModule {

    @Binds
    @Singleton
    abstract fun bindAppLogger(
        implementation: FirebaseAppLogger
    ): AppLogger
}