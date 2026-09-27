package com.maria.movierank.feature.trending.domain.usecase

import com.maria.movierank.feature.trending.domain.model.Movie
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetTrendingMoviesUseCaseTest {

    private lateinit var mockRepo: MovieRepository
    private lateinit var useCase: GetTrendingMoviesUseCase

    private val rawMovies = listOf(
        Movie(id = 1, title = "Action Movie", posterPath = null, releaseDate = "2024-01-01",
              genreIds = listOf(28, 12), popularity = 100.0, voteAverage = 7.0, voteCount = 500),
        Movie(id = 2, title = "Drama Film", posterPath = null, releaseDate = "2023-06-01",
              genreIds = listOf(18), popularity = 50.0, voteAverage = 8.0, voteCount = 1000)
    )

    @Before
    fun setUp() = runTest {
        mockRepo = mock()
        whenever(mockRepo.getTrendingMovies(any())).thenReturn(rawMovies)
        useCase = GetTrendingMoviesUseCase(mockRepo)
    }

    @Test
    fun `maps genre IDs to genre names`() = runTest {
        val genres = mapOf(28 to "Action", 12 to "Adventure", 18 to "Drama")
        val result = useCase(genres)
        assertEquals(listOf("Action", "Adventure"), result[0].genreNames)
        assertEquals(listOf("Drama"), result[1].genreNames)
    }

    @Test
    fun `unknown genre IDs are ignored`() = runTest {
        val result = useCase(mapOf(28 to "Action"))
        assertEquals(listOf("Action"), result[0].genreNames)
        assertTrue(result[1].genreNames.isEmpty())
    }

    @Test
    fun `empty genre map produces empty genre names`() = runTest {
        val result = useCase(emptyMap())
        assertTrue(result.all { it.genreNames.isEmpty() })
    }

    @Test
    fun `returns all movies from repository`() = runTest {
        val result = useCase(emptyMap())
        assertEquals(rawMovies.map { it.id }, result.map { it.id })
    }

    @Test
    fun `propagates exception from repository`() = runTest {
        whenever(mockRepo.getTrendingMovies(any())).thenThrow(RuntimeException("API error"))
        var caught: Exception? = null
        try { useCase(emptyMap()) } catch (e: Exception) { caught = e }
        assertEquals("API error", caught?.message)
    }
}
