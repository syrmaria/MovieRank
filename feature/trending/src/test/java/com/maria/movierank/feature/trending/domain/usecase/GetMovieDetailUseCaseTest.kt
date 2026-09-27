package com.maria.movierank.feature.trending.domain.usecase

import com.maria.movierank.feature.trending.domain.model.Genre
import com.maria.movierank.feature.trending.domain.model.MovieDetails
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetMovieDetailUseCaseTest {

    private lateinit var mockRepo: MovieRepository
    private lateinit var useCase: GetMovieDetailUseCase

    private val detail = MovieDetails(
        id = 42, title = "Test Movie", tagline = "tagline",
        overview = "overview", budget = 0L, revenue = 0L,
        runtime = 90, genres = listOf(Genre(28, "Action")), imdbId = null,
        voteAverage = 7.0, voteCount = 100, status = "Released",
        releaseDate = "2024-01-01", posterPath = null, backdropPath = null
    )

    @Before
    fun setUp() = runTest {
        mockRepo = mock()
        whenever(mockRepo.getMovieDetail(any())).thenReturn(detail)
        useCase = GetMovieDetailUseCase(mockRepo)
    }

    @Test
    fun `returns movie detail from repository`() = runTest {
        assertEquals(detail, useCase(42))
    }

    @Test
    fun `passes movieId to repository`() = runTest {
        val altDetail = detail.copy(id = 99, title = "Another Movie")
        whenever(mockRepo.getMovieDetail(99)).thenReturn(altDetail)
        assertEquals(altDetail, useCase(99))
    }

    @Test
    fun `propagates exception from repository`() = runTest {
        whenever(mockRepo.getMovieDetail(any())).thenThrow(RuntimeException("Detail not found"))
        var caught: Exception? = null
        try { useCase(42) } catch (e: Exception) { caught = e }
        assertEquals("Detail not found", caught?.message)
    }
}
