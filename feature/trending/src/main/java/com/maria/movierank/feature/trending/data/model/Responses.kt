package com.maria.movierank.feature.trending.data.model

import com.google.gson.annotations.SerializedName
import com.maria.movierank.feature.trending.domain.model.Genre
import com.maria.movierank.feature.trending.domain.model.Movie

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)

data class MovieListResponse(
    @SerializedName("page") val page: Int,
    @SerializedName("results") val results: List<Movie>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)