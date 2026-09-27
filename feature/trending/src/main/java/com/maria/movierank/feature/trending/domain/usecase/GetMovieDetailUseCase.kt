package com.maria.movierank.feature.trending.domain.usecase

import com.maria.movierank.feature.trending.domain.model.MovieDetails
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): MovieDetails =
        movieRepository.getMovieDetail(movieId)
}
