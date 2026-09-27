package com.maria.movierank.feature.trending.data.api

import com.maria.movierank.feature.trending.data.model.GenreResponse
import com.maria.movierank.feature.trending.data.model.MovieListResponse
import com.maria.movierank.feature.trending.domain.model.MovieDetails
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApiService {
    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): MovieDetails

    @GET("genre/movie/list")
    suspend fun getGenres(): GenreResponse
}