package com.maria.movierank.feature.trending.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maria.movierank.network.domain.repo.GuestSessionRepository
import com.maria.movierank.network.domain.model.NetworkException
import com.maria.movierank.network.logs.AppLogger
import com.maria.movierank.feature.trending.domain.usecase.GetGenresUseCase
import com.maria.movierank.feature.trending.domain.usecase.GetTrendingMoviesUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class MovieListViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val guestSessionRepository: GuestSessionRepository,
    private val getGenresUseCase: GetGenresUseCase,
    private val appLogger: AppLogger,
    private val movieDetailFactory: MovieDetailViewModelFactory
) : ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state: StateFlow<MovieListState> = _state.asStateFlow()

    private var currentDetailVm: MovieDetailViewModel? = null
    private var detailCollectionJob: Job? = null

    init {
        onEvent(MovieListEvent.LoadMovies)
    }

    fun onEvent(event: MovieListEvent) {
        when (event) {
            is MovieListEvent.LoadMovies -> loadMovies()
            is MovieListEvent.MovieClicked -> openDetail(event.movieId)
            is MovieListEvent.DismissMovieDetail -> closeDetail()
            is MovieListEvent.DetailEvent -> currentDetailVm?.onEvent(event.event)
            is MovieListEvent.Retry -> loadMovies()
            is MovieListEvent.SortChanged -> _state.update { it.copy(sortField = event.field, sortDirection = event.direction) }
            is MovieListEvent.GenreFilterChanged -> _state.update { it.copy(selectedGenre = event.genre) }
        }
    }

    private fun openDetail(movieId: Int) {
        detailCollectionJob?.cancel()
        val detailVm = movieDetailFactory.create(movieId)
        currentDetailVm = detailVm
        _state.update { it.copy(selectedMovieId = movieId, detailState = MovieDetailState()) }
        detailCollectionJob = viewModelScope.launch {
            detailVm.state.collect { detailState ->
                _state.update { it.copy(detailState = detailState) }
            }
        }
    }

    private fun closeDetail() {
        detailCollectionJob?.cancel()
        detailCollectionJob = null
        currentDetailVm = null
        _state.update { it.copy(selectedMovieId = null, detailState = null) }
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true, error = null)
            }

            try {
                guestSessionRepository.getOrCreate()
                val genres = getGenresUseCase()
                val movies = getTrendingMoviesUseCase(genres)
                _state.update {
                    it.copy(isLoading = false, allMovies = movies)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: NetworkException.ConnectionError) {
                appLogger.logError(e.message.orEmpty(), e)
                _state.update { it.copy(isLoading = false, error = e.userMessage) }
            } catch (e: NetworkException.TimeoutError) {
                appLogger.logError(e.message.orEmpty(), e)
                _state.update { it.copy(isLoading = false, error = e.userMessage) }
            } catch (e: NetworkException.HttpError) {
                appLogger.logError(e.message.orEmpty(), e)
                _state.update { it.copy(isLoading = false, error = e.userMessage) }
            } catch (e: Exception) {
                appLogger.logError(e.message.orEmpty(), e)
                _state.update { it.copy(isLoading = false, error = e.message ?: "Failed to load movies") }
            }
        }
    }
}
