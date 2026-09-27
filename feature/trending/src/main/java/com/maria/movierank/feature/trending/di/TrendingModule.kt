package com.maria.movierank.feature.trending.di

import com.maria.movierank.feature.trending.data.api.MoviesApiService
import dagger.Binds
import dagger.Module
import dagger.Provides
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import com.maria.movierank.feature.trending.domain.repo.MovieRepositoryImpl
import retrofit2.Retrofit

@Module
abstract class TrendingModule {

    @Binds
    abstract fun bindMovieRepository(
        impl: MovieRepositoryImpl
    ): MovieRepository

    companion object {

        @Provides
        @TrendingScope
        fun provideMoviesApiService(
            retrofit: Retrofit
        ): MoviesApiService =
            retrofit.create(MoviesApiService::class.java)
    }
}