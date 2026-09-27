package com.maria.movierank.feature.trending.presentation.viewmodel

import com.maria.movierank.feature.trending.data.model.GenreResponse
import com.maria.movierank.feature.trending.domain.model.Genre
import com.maria.movierank.feature.trending.domain.repo.MovieRepository
import com.maria.movierank.feature.trending.domain.usecase.GetGenresUseCase
import com.maria.movierank.feature.trending.domain.usecase.GetTrendingMoviesUseCase
import com.maria.movierank.feature.trending.util.MainDispatcherRule
import com.maria.movierank.network.domain.model.NetworkException
import com.maria.movierank.network.domain.repo.GuestSessionRepository
import com.maria.movierank.network.logs.AppLogger

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

import org.mockito.Mockito.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MovieListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val movieRepository = mock<MovieRepository>()
    private val guestSessionRepository = mock<GuestSessionRepository>()
    private val getTrendingMoviesUseCase = GetTrendingMoviesUseCase(movieRepository)
    private val getGenresUseCase = GetGenresUseCase(movieRepository)
    private val appLogger = mock<AppLogger>()
    private val movieDetailFactory = mock<MovieDetailViewModelFactory>()
    private lateinit var viewModel: MovieListViewModel

    private fun createViewModel() {
        viewModel = MovieListViewModel(
            getTrendingMoviesUseCase = getTrendingMoviesUseCase,
            guestSessionRepository = guestSessionRepository,
            getGenresUseCase = getGenresUseCase,
            appLogger = appLogger,
            movieDetailFactory = movieDetailFactory
        )
    }

    @Test
    fun `loadMovies loads session genres and movies in order`() =
        runTest {
            val movies = emptyList<com.maria.movierank.feature.trending.domain.model.Movie>()

            whenever(guestSessionRepository.getOrCreate()).thenReturn("any string")
            whenever(movieRepository.getGenres()).thenReturn(GenreResponse(listOf(Genre(1, "genre"))))
            whenever(movieRepository.getTrendingMovies(5)).thenReturn(movies)

            createViewModel()
            advanceUntilIdle()
            val state = viewModel.state.value

            assertFalse(state.isLoading)
            assertEquals(movies, state.allMovies)
            assertNull(state.error)

            val order = inOrder(guestSessionRepository, movieRepository)
            order.verify(guestSessionRepository).getOrCreate()
            order.verify(movieRepository).getGenres()
            order.verify(movieRepository).getTrendingMovies(5)
        }

    @Test
    fun `loadMovies handles connection error`() = runTest {
        val exception = NetworkException.ConnectionError(RuntimeException())

        whenever(guestSessionRepository.getOrCreate()).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(exception.userMessage, state.error)

        verify(movieRepository, never()).getGenres()
        verify(movieRepository, never()).getTrendingMovies(org.mockito.kotlin.any())
        verify(appLogger)
            .logError(
                exception.message.orEmpty(),
                exception
            )
    }

    @Test
    fun `loadMovies handles timeout error`() = runTest {
        val exception = NetworkException.TimeoutError(RuntimeException())

        whenever(guestSessionRepository.getOrCreate()).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(exception.userMessage, state.error)

        verify(appLogger)
            .logError(
                exception.message.orEmpty(),
                exception
            )
    }

    @Test
    fun `loadMovies handles HTTP error`() = runTest {
        val exception = NetworkException.HttpError(code = 500, body = null)

        whenever(guestSessionRepository.getOrCreate()).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals(exception.userMessage, state.error)

        verify(appLogger)
            .logError(
                exception.message.orEmpty(),
                exception
            )
    }

    @Test
    fun `loadMovies handles unexpected exception`() = runTest {
        val exception = RuntimeException("Unexpected error")

        whenever(guestSessionRepository.getOrCreate()).thenThrow(exception)

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertEquals("Unexpected error", state.error)

        verify(appLogger)
            .logError(
                exception.message.orEmpty(),
                exception
            )
        verify(movieRepository, never()).getGenres()
    }

    @Test
    fun `retry loads movies again after error`() = runTest {
        val movies = emptyList<com.maria.movierank.feature.trending.domain.model.Movie>()

        whenever(guestSessionRepository.getOrCreate())
            .thenThrow(RuntimeException("Network error"))
            .thenReturn("any string")
        whenever(movieRepository.getGenres()).thenReturn(GenreResponse(listOf(Genre(1, "genre"))))
        whenever(movieRepository.getTrendingMovies(5)).thenReturn(movies)

        createViewModel()
        advanceUntilIdle()

        assertEquals("Network error", viewModel.state.value.error)

        viewModel.onEvent(MovieListEvent.Retry)
        advanceUntilIdle()
        val state = viewModel.state.value

        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(movies, state.allMovies)

        verify(guestSessionRepository, times(2)).getOrCreate()
        verify(movieRepository).getGenres()
        verify(movieRepository).getTrendingMovies(5)
    }

    @Test
    fun `loadMovies does not convert cancellation into error`() = runTest {

        whenever(guestSessionRepository.getOrCreate()).thenThrow(CancellationException())

        createViewModel()
        advanceUntilIdle()
        val state = viewModel.state.value

        assertNull(state.error)
        verify(movieRepository, never()).getGenres()
        verify(movieRepository, never()).getTrendingMovies(org.mockito.kotlin.any())
        verify(appLogger, never())
            .logError(
                org.mockito.kotlin.any(),
                org.mockito.kotlin.any()
            )
    }
}
