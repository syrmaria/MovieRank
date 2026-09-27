package com.maria.movierank.feature.trending.domain.repo

import com.maria.movierank.feature.trending.data.api.MoviesApiService
import com.maria.movierank.feature.trending.data.model.GenreResponse
import com.maria.movierank.feature.trending.di.TrendingScope
import com.maria.movierank.feature.trending.domain.model.Movie
import com.maria.movierank.feature.trending.domain.model.MovieDetails
import com.maria.movierank.network.domain.utils.safeApiCall
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

@TrendingScope
class MovieRepositoryImpl @Inject constructor(
    private val apiService: MoviesApiService
) : MovieRepository {

    override suspend fun getTrendingMovies(pages: Int): List<Movie> = safeApiCall {
        require(pages >= 1)
        coroutineScope {
            (1..pages)
                .map { page -> async { apiService.discoverMovies(page = page).results } }
                .awaitAll()
                .flatten()
                .distinctBy { it.id }
        }
    }

    override suspend fun getMovieDetail(movieId: Int): MovieDetails = safeApiCall {
        apiService.getMovieDetail(movieId)
    }

    override suspend fun getGenres(): GenreResponse = safeApiCall {
        apiService.getGenres()
    }
}