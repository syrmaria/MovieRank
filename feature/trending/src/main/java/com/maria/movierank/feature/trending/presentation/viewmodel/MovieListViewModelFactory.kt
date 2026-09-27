package com.maria.movierank.feature.trending.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.maria.movierank.feature.trending.di.TrendingScope
import com.maria.movierank.network.domain.repo.GuestSessionRepository
import com.maria.movierank.network.logs.AppLogger
import com.maria.movierank.feature.trending.domain.usecase.GetGenresUseCase
import com.maria.movierank.feature.trending.domain.usecase.GetTrendingMoviesUseCase
import javax.inject.Inject

@TrendingScope
class MovieListViewModelFactory @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val guestSessionRepository: GuestSessionRepository,
    private val getGenresUseCase: GetGenresUseCase,
    private val appLogger: AppLogger,
    private val movieDetailFactory: MovieDetailViewModelFactory
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MovieListViewModel(getTrendingMoviesUseCase, guestSessionRepository, getGenresUseCase, appLogger, movieDetailFactory) as T
}
