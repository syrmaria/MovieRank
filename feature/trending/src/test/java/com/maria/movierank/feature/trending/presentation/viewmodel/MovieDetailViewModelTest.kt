package com.maria.movierank.feature.trending.presentation.viewmodel

import com.maria.movierank.feature.trending.domain.model.Genre
import com.maria.movierank.feature.trending.domain.model.MovieDetails
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import com.maria.movierank.feature.trending.domain.usecase.GetMovieDetailUseCase
import com.maria.movierank.feature.trending.util.MainDispatcherRule
import com.maria.movierank.network.domain.model.NetworkException

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieRepository = mock<MovieRepository>()
    private val getMovieDetailUseCase = GetMovieDetailUseCase(movieRepository)
    private val movieId = 42
    private lateinit var viewModel: MovieDetailViewModel

    private fun createViewModel() {
        viewModel = MovieDetailViewModel(
            getMovieDetailUseCase = getMovieDetailUseCase,
            movieId = movieId
        )
    }

    @Test
    fun `loadDetail loads movie detail`() = runTest {
        val detail = MovieDetails(
            id = 42,
            title = "Test Movie",
            tagline = "tagline",
            overview = "overview",
            budget = 1_000_000,
            revenue = 5_000_000,
            runtime = 120,
            genres = listOf(Genre(28, "Action")),
            imdbId = "tt1234567",
            voteAverage = 7.5,
            voteCount = 1000,
            status = "Released",
            releaseDate = "2024-01-01",
            posterPath = "/poster.jpg",
            backdropPath = "/backdrop.jpg"
        )

        whenever(movieRepository.getMovieDetail(movieId)).thenReturn(detail)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(detail, state.movieDetail)
        assertNull(state.error)
    }

    @Test
    fun `loadDetail handles connection error`() = runTest {
        val exception = NetworkException.ConnectionError(RuntimeException())

        whenever(movieRepository.getMovieDetail(movieId)).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(exception.message, state.error)
        assertNull(state.movieDetail)
    }

    @Test
    fun `loadDetail handles timeout error`() = runTest {
        val exception = NetworkException.TimeoutError(RuntimeException())

        whenever(movieRepository.getMovieDetail(movieId)).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(exception.message, state.error)
        assertNull(state.movieDetail)
    }

    @Test
    fun `loadDetail handles HTTP error`() = runTest {
        val exception = NetworkException.HttpError(code = 404, body = null)

        whenever(movieRepository.getMovieDetail(movieId)).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(exception.message, state.error)
        assertNull(state.movieDetail)
    }

    @Test
    fun `loadDetail handles unexpected exception`() = runTest {
        val exception = RuntimeException("Unexpected error")

        whenever(movieRepository.getMovieDetail(movieId)).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals("Unexpected error", state.error)
        assertNull(state.movieDetail)
    }

    @Test
    fun `loadDetail uses fallback message when exception has no message`() = runTest {
        whenever(movieRepository.getMovieDetail(movieId)).thenThrow(RuntimeException())

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals("Failed to load details", state.error)
    }

    @Test
    fun `retry loads detail again after error`() = runTest {
        val detail = MovieDetails(
            id = 42,
            title = "Test Movie",
            tagline = null,
            overview = null,
            budget = 0,
            revenue = 0,
            runtime = null,
            genres = emptyList(),
            imdbId = null,
            voteAverage = 0.0,
            voteCount = 0,
            status = null,
            releaseDate = null,
            posterPath = null,
            backdropPath = null
        )

        whenever(movieRepository.getMovieDetail(movieId))
            .thenThrow(RuntimeException("Network error"))
            .thenReturn(detail)

        createViewModel()
        advanceUntilIdle()

        assertEquals("Network error", viewModel.state.value.error)

        viewModel.onEvent(MovieDetailEvent.Retry)
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(detail, state.movieDetail)

        verify(movieRepository, times(2)).getMovieDetail(movieId)
    }
}
