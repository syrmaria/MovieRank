package com.maria.movierank.feature.trending.domain.model

import com.google.gson.annotations.SerializedName

data class MovieDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("tagline") val tagline: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("budget") val budget: Long,
    @SerializedName("revenue") val revenue: Long,
    @SerializedName("runtime") val runtime: Int?,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("imdb_id") val imdbId: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    @SerializedName("status") val status: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
)