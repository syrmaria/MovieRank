package com.maria.movierank.feature.trending.domain.usecase

import com.maria.movierank.feature.trending.domain.model.Genre
import com.maria.movierank.feature.trending.data.model.GenreResponse
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetGenresUseCaseTest {

    private lateinit var mockRepo: MovieRepository
    private lateinit var useCase: GetGenresUseCase

    @Before
    fun setUp() {
        mockRepo = mock()
        useCase = GetGenresUseCase(mockRepo)
    }

    @Test
    fun `returns genres as id-to-name map`() = runTest {
        whenever(mockRepo.getGenres()).thenReturn(GenreResponse(listOf(Genre(28, "Action"), Genre(18, "Drama"))))
        assertEquals(mapOf(28 to "Action", 18 to "Drama"), useCase())
    }

    @Test
    fun `returns empty map when no genres`() = runTest {
        whenever(mockRepo.getGenres()).thenReturn(GenreResponse(emptyList()))
        assertTrue(useCase().isEmpty())
    }

    @Test
    fun `propagates exception from repository`() = runTest {
        whenever(mockRepo.getGenres()).thenThrow(RuntimeException("Genres unavailable"))
        var caught: Exception? = null
        try { useCase() } catch (e: Exception) { caught = e }
        assertEquals("Genres unavailable", caught?.message)
    }
}
