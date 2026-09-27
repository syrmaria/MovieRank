package com.maria.movierank.feature.trending.domain.repo

import com.maria.movierank.feature.trending.data.model.GenreResponse
import com.maria.movierank.feature.trending.domain.model.Movie
import com.maria.movierank.feature.trending.domain.model.MovieDetails

interface MovieRepository {
    suspend fun getTrendingMovies(pages: Int): List<Movie>
    suspend fun getMovieDetail(movieId: Int): MovieDetails
    suspend fun getGenres(): GenreResponse
}
