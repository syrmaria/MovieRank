package com.maria.movierank.network.di

import com.maria.movierank.network.domain.repo.GuestSessionRepository
import com.maria.movierank.network.logs.AppLogger
import retrofit2.Retrofit

interface NetworkDependencies {
    fun retrofit(): Retrofit
    fun guestSessionRepository(): GuestSessionRepository
    fun appLogger(): AppLogger
}