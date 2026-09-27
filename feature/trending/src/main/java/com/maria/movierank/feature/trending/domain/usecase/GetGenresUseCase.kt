package com.maria.movierank.feature.trending.domain.usecase

import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(): Map<Int, String> =
        movieRepository.getGenres().genres.associate { it.id to it.name }
}
