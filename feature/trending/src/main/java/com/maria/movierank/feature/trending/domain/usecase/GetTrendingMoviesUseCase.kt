package com.maria.movierank.feature.trending.domain.usecase

import com.maria.movierank.feature.trending.di.TrendingScope
import com.maria.movierank.feature.trending.domain.model.Movie
import com.maria.movierank.feature.trending.domain.model.withGenreNames
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import javax.inject.Inject

@TrendingScope
class GetTrendingMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(genres: Map<Int, String>): List<Movie> {
        val movies = movieRepository.getTrendingMovies(pages = 5)
        return movies.map { it.withGenreNames(genres) }
    }
}
