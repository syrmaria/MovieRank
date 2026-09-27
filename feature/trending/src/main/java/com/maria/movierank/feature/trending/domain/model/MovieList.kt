package com.maria.movierank.feature.trending.domain.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("genre_ids") val genreIds: List<Int>,
    @SerializedName("popularity") val popularity: Double,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    val genreNames: List<String> = emptyList()
)

fun Movie.withGenreNames(
    genreMap: Map<Int, String>
): Movie =
    copy(
        genreNames = genreIds.mapNotNull { genreMap[it] }
    )
