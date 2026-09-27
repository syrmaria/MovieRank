package com.maria.movierank.feature.trending.presentation.viewmodel

import com.maria.movierank.feature.trending.domain.model.Movie

const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"

data class MovieListState(
    val isLoading: Boolean = false,
    val allMovies: List<Movie> = emptyList(),
    val sortField: SortField = SortField.POPULARITY,
    val sortDirection: SortDirection = SortDirection.DESC,
    val selectedGenre: String? = null,
    val error: String? = null,
    val selectedMovieId: Int? = null,
    val detailState: MovieDetailState? = null
) {
    val availableGenres: List<String>
        get() = allMovies.flatMap { it.genreNames }.distinct().sorted()

    val displayedMovies: List<Movie>
        get() {
            val filtered = if (selectedGenre == null) allMovies
                           else allMovies.filter { it.genreNames.contains(selectedGenre) }
            val sorted = when (sortField) {
                SortField.POPULARITY -> filtered.sortedBy { it.popularity }
                SortField.DATE -> filtered.sortedBy { it.releaseDate.orEmpty() }
                SortField.TITLE -> filtered.sortedBy { it.title.lowercase() }
            }
            return if (sortDirection == SortDirection.DESC) sorted.reversed() else sorted
        }
}

sealed class MovieListEvent {
    data object LoadMovies : MovieListEvent()
    data class MovieClicked(val movieId: Int) : MovieListEvent()
    data object DismissMovieDetail : MovieListEvent()
    data class DetailEvent(val event: MovieDetailEvent) : MovieListEvent()
    data object Retry : MovieListEvent()
    data class SortChanged(val field: SortField, val direction: SortDirection) : MovieListEvent()
    data class GenreFilterChanged(val genre: String?) : MovieListEvent()
}

enum class SortField { POPULARITY, DATE, TITLE }
enum class SortDirection { ASC, DESC }

val SortField.label get() = when (this) {
    SortField.POPULARITY -> "Popularity"
    SortField.DATE -> "Release Date"
    SortField.TITLE -> "Title"
}

val SortField.defaultDirection get() = when (this) {
    SortField.TITLE -> SortDirection.ASC
    else -> SortDirection.DESC
}
