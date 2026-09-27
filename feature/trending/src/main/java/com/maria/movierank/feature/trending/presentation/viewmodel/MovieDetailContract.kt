package com.maria.movierank.feature.trending.presentation.viewmodel

import com.maria.movierank.feature.trending.domain.model.MovieDetails

data class MovieDetailState(
    val isLoading: Boolean = false,
    val movieDetail: MovieDetails? = null,
    val error: String? = null
)

sealed class MovieDetailEvent {
    data object Retry : MovieDetailEvent()
}
